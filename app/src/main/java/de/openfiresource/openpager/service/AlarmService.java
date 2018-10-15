package de.openfiresource.openpager.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.DaggerService;
import de.openfiresource.openpager.models.AppDatabase;
import de.openfiresource.openpager.models.Notification;
import de.openfiresource.openpager.models.database.OperationMessage;
import de.openfiresource.openpager.models.database.OperationRule;
import de.openfiresource.openpager.ui.operation.OperationActivity;
import de.openfiresource.openpager.utils.Preferences;
import de.openfiresource.openpager.utils.rx.RxMediaPlayer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class AlarmService extends DaggerService {

    // Time period between two vibration events
    private final static int VIBRATE_DELAY_TIME = 1000;

    private final CompositeDisposable alarmDisposable = new CompositeDisposable();

    private Notification mNotification;

    @Inject
    AppDatabase database;

    @Inject
    Preferences preferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long operationId = intent.getLongExtra(OperationActivity.OPERATION_ID, 0);

        OperationMessage operationMessage = database.operationMessageDao().findById(operationId);
        OperationRule rule = database.operationRuleDao().findById(operationMessage.getOperationRuleId());
        mNotification = Notification.byRule(rule, this);

        //Start alarm Service
        startPlayer();

        // Start the activity where you can stop alarm
        Intent i = new Intent(this, OperationActivity.class);
        i.putExtra(OperationActivity.OPERATION_ID, operationMessage.getId());
        i.putExtra(OperationActivity.IS_ALARM, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        alarmDisposable.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startPlayer() {
        int alarmTimeout = preferences.getAlarmTimeout();
        if (alarmTimeout > 0) {
            alarmDisposable.clear();
        }

        try {
            // add vibration to alarm alert if it is set
            final int vibrateDelayTime = Integer.parseInt(mNotification.getNewMessageVibrate());
            if (vibrateDelayTime > 0 && ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                alarmDisposable.add
                        (Observable.interval(vibrateDelayTime + VIBRATE_DELAY_TIME, TimeUnit.MILLISECONDS, Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aLong -> vibrator.vibrate(vibrateDelayTime), throwable -> Timber.e(throwable, "Error on vibration Observable"))
                        );
            }

            // Player setup is here
            if (mNotification.isPlayingSound()) {
                String ringtone = mNotification.getRingtone(); // App.getState().settings().ringtone();
                if ((ringtone.startsWith("content://media/external/audio/media/") &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        || TextUtils.isEmpty(ringtone)) {
                    ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
                }

                MediaPlayer mPlayer = new MediaPlayer();
                float volume = Float.parseFloat(mNotification.getNewMessageVolume()) / 100;
                if (volume != 0) {
                    mPlayer.setVolume(volume, volume);
                }

                mPlayer.setDataSource(this, Uri.parse(ringtone));
                mPlayer.setLooping(true);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

                alarmDisposable.add(RxMediaPlayer.play(mPlayer)
                        .subscribe(integerIntegerPair -> {

                        }, throwable -> {
                            Timber.e(throwable, "Error playing music");
                            stopSelf();
                        }));

            }

        } catch (Exception e) {
            Timber.e(e, "Error playing alarm");
            stopSelf();
        }
    }
}
