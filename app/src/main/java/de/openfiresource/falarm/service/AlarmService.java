package de.openfiresource.falarm.service;

import android.Manifest;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.android.gms.contextmanager.internal.InterestUpdateBatchImpl;
import com.orhanobut.logger.Logger;

import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.OperationMessage;
import de.openfiresource.falarm.models.OperationRule;
import de.openfiresource.falarm.ui.OperationActivity;


public class AlarmService extends Service {
    private static final String TAG = "AlarmService";

    // Time period between two vibration events
    private final static int VIBRATE_DELAY_TIME = 1000;
    private MediaPlayer mPlayer;
    private Vibrator mVibrator;
    private int mVibrateDelayTime;
    private OperationMessage mOperationMessage;
    private Notification mNotification;

    private Handler mHandler = new Handler();
    private Runnable mVibrationRunnable = new Runnable() {
        @Override
        public void run() {
            mVibrator.vibrate(mVibrateDelayTime);

            mHandler.postDelayed(mVibrationRunnable,
                    mVibrateDelayTime + VIBRATE_DELAY_TIME);
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = (mp, what, extra) -> {
        mp.stop();
        mp.release();
        mHandler.removeCallbacksAndMessages(null);
        AlarmService.this.stopSelf();
        return true;
    };

    @Override
    public void onCreate() {
        HandlerThread ht = new HandlerThread("alarm_serviceHandlerThread");
        ht.start();
        mHandler = new Handler(ht.getLooper());

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long operationId = intent.getLongExtra(OperationActivity.EXTRA_ID, 0);

        mOperationMessage = OperationMessage.findById(OperationMessage.class, operationId);
        OperationRule rule = mOperationMessage.getRule();
        mNotification = Notification.byRule(rule, this);

        //Start alarm Service
        startPlayer();

        // Start the activity where you can stop alarm
        Intent i = new Intent(this, OperationActivity.class);
        i.putExtra(OperationActivity.EXTRA_ID, mOperationMessage.getId());
        i.putExtra(OperationActivity.EXTRA_TYPE_ALARM, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopAlarm() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        }

        mHandler.removeCallbacksAndMessages(null);
    }

    private void startPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(mErrorListener);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int alarmTimeout = Integer.parseInt(preferences.getString("general_alarm_timeout", "120"));
        if(alarmTimeout > 0) {
            mHandler.postDelayed(() -> stopSelf(), alarmTimeout * 1000);
        }

        try {
            // add vibration to alarm alert if it is set
            mVibrateDelayTime = Integer.parseInt(mNotification.getNewMessageVibrate());
            if (mVibrateDelayTime > 0
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mHandler.post(mVibrationRunnable);
            }
            // Player setup is here
            if (mNotification.isPlayingSound()) {
                String ringtone = mNotification.getRingtone(); // App.getState().settings().ringtone();
                if ((ringtone.startsWith("content://media/external/audio/media/") &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        || TextUtils.isEmpty(ringtone)) {
                    ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
                }

                float volume = Float.parseFloat(mNotification.getNewMessageVolume()) / 100;
                if (volume != 0)
                    mPlayer.setVolume(volume, volume);

                mPlayer.setDataSource(this, Uri.parse(ringtone));
                mPlayer.setLooping(true);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.prepare();
                mPlayer.start();
            }

        } catch (Exception e) {
            Logger.e(e, "Error playing alarm");
            stopSelf();
        }
    }
}
