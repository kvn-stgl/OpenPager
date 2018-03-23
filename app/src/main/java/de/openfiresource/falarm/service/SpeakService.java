package de.openfiresource.falarm.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.Locale;
import java.util.Vector;

import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.models.database.OperationRule;
import de.openfiresource.falarm.ui.OperationActivity;

public class SpeakService extends Service implements TextToSpeech.OnInitListener {
    public static final String STOP_NOW = "stop_now";
    private Vector<OperationMessage> queue = new Vector<>();

    private TextToSpeech tts;
    private boolean initialized = false;
    private boolean temporaryDisable = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Init the TTS System.
     *
     * @param status
     */
    public void onInit(int status) {
        tts.setLanguage(Locale.GERMAN);

        if (status == TextToSpeech.SUCCESS) {
            synchronized (queue) {
                initialized = true;

                for (OperationMessage message : queue) {
                    this.speak(message);
                }

                queue.clear();
            }
        }
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
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);

        if (volume != 0) {
            int amVolume = (int) (volume * amStreamMusicMaxVol) / 100;
            am.setStreamVolume(am.STREAM_MUSIC, amVolume, 0);
        }
        String utteranceId = "id" + this.hashCode();

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(operationMessage.getMessage(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            tts.speak(operationMessage.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        this.tts = new TextToSpeech(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null) {
            //We should not reach here with no intent because START_REDELIVER_INTENT
            throw new java.lang.IllegalArgumentException("No intent found.");
        }

        long operationId = intent.getLongExtra(OperationActivity.EXTRA_ID, 0);
        boolean stopInstead = intent.getExtras().getBoolean(SpeakService.STOP_NOW);

        if (stopInstead && tts != null) {
            this.tts.stop();
        } else if (operationId != 0 && !temporaryDisable) {
            OperationMessage operationMessage = AppDatabase.getInstance(this).operationMessageDao().findById(operationId);

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