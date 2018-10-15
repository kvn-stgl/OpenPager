package de.openfiresource.openpager.service;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.DaggerService;
import de.openfiresource.openpager.models.AppDatabase;
import de.openfiresource.openpager.models.Notification;
import de.openfiresource.openpager.models.database.OperationMessage;
import de.openfiresource.openpager.models.database.OperationRule;
import de.openfiresource.openpager.ui.operation.OperationActivity;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;
import timber.log.Timber;

public class SpeakService extends DaggerService {

    public static final String INTENT_STOP_NOW = "intent_stop_now";

    private TextToSpeech tts;
    private boolean temporaryDisable = false;

    private ReplaySubject<OperationMessage> replaySubject;

    private final CompositeDisposable alarmDisposable = new CompositeDisposable();

    @Inject
    AppDatabase database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        replaySubject = ReplaySubject.create();

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm != null) {
            tm.listen(new PhoneStateListener() {
                @Override
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
            }, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            Timber.e("onCreate: TelephonyManager is null");
        }


        tts = new TextToSpeech(this, status -> {
            tts.setLanguage(Locale.GERMAN);

            if (status == TextToSpeech.SUCCESS) {
                alarmDisposable.add(replaySubject.subscribe(this::speak, Timber::e));
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
        boolean stopInstead = intent.getExtras().getBoolean(SpeakService.INTENT_STOP_NOW);

        if (stopInstead && tts != null) {
            this.tts.stop();
        } else if (operationId != 0 && !temporaryDisable) {
            OperationMessage operationMessage = database.operationMessageDao().findById(operationId);

            //TODO: Delay inmplementation
            int delaySend = 0;

            alarmDisposable.add(
                    Completable.timer(delaySend, TimeUnit.MILLISECONDS, Schedulers.computation())
                            .subscribe(() -> replaySubject.onNext(operationMessage), throwable -> Timber.e(throwable, "Error on tts timer"))
            );
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        this.tts.shutdown();
    }

    private void speak(OperationMessage operationMessage) {
        if (temporaryDisable || operationMessage == null) {
            return;
        }

        OperationRule rule = database.operationRuleDao().findById(operationMessage.getOperationRuleId());
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
            Timber.e("speak: AudioManager is null");
        }


        String utteranceId = "id" + this.hashCode();
        tts.speak(operationMessage.getMessage(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}