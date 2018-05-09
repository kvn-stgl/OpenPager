package de.openfiresource.falarm.service;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;
import java.util.Vector;

import javax.inject.Inject;

import dagger.android.DaggerService;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.models.database.OperationRule;
import de.openfiresource.falarm.ui.operation.OperationActivity;

public class SpeakService extends DaggerService {

    private static final String TAG = "SpeakService";
    
    public static final String STOP_NOW = "stop_now";
    private final Vector<OperationMessage> queue = new Vector<>();

    private TextToSpeech tts;
    private boolean initialized = false;
    private boolean temporaryDisable = false;

    @Inject
    AppDatabase database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Handler for TTS Messages.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            synchronized (queue) {
                if (!initialized) {
                    queue.add((OperationMessage) msg.obj);
                } else {
                    speak((OperationMessage) msg.obj);
                }
            }
        }
    };


    private void speak(OperationMessage operationMessage) {
        if (temporaryDisable || operationMessage == null)
            return;

        OperationRule rule = operationMessage.getRule();
        Notification notification = Notification.byRule(rule, this);

        float volume = Integer.parseInt(notification.getNewMessageVolume());

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            int amStreamMusicMaxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (volume != 0) {
                int amVolume = (int) (volume * amStreamMusicMaxVol) / 100;
                am.setStreamVolume(AudioManager.STREAM_MUSIC, amVolume, 0);
            }
        } else {
            Log.e(TAG, "speak: AudioManager is null");
        }


        String utteranceId = "id" + this.hashCode();
        tts.speak(operationMessage.getMessage(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(tm != null) {
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            Log.e(TAG, "onCreate: TelephonyManager is null");
        }

        tts = new TextToSpeech(this, status -> {
            tts.setLanguage(Locale.GERMAN);

            if (status == TextToSpeech.SUCCESS) {
                synchronized (queue) {
                    initialized = true;

                    for (OperationMessage message : queue) {
                        speak(message);
                    }

                    queue.clear();
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null) {
            //We should not reach here with no intent because START_REDELIVER_INTENT
            throw new java.lang.IllegalArgumentException("No intent found.");
        }

        long operationId = intent.getLongExtra(OperationActivity.OPERATION_ID, 0);
        boolean stopInstead = intent.getExtras().getBoolean(SpeakService.STOP_NOW);

        if (stopInstead && tts != null) {
            this.tts.stop();
        } else if (operationId != 0 && !temporaryDisable) {
            OperationMessage operationMessage = database.operationMessageDao().findById(operationId);

            //TODO: Delay inmplementation
            int delaySend = 0;

            Message msg = Message.obtain();
            msg.obj = operationMessage;
            if (delaySend > 0) {
                handler.sendMessageDelayed(msg, delaySend * 1000);
            } else {
                handler.sendMessage(msg);
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        this.tts.shutdown();
        this.initialized = false;
    }

    private PhoneStateListener mPhoneListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    tts.stop();
                    temporaryDisable = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    temporaryDisable = false;
                    break;
                default:
                    break;
            }
        }
    };
}