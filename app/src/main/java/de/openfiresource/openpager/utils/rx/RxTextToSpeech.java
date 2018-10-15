package de.openfiresource.openpager.utils.rx;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RxTextToSpeech {

    private static final String ENGINE = "com.google.android.tts";

    public static Single<TextToSpeech> init(Context context, int delay) {
        return Single.timer(delay, TimeUnit.MILLISECONDS, Schedulers.computation())
                .flatMap(aLong -> prepare(context));
    }

    private static Single<TextToSpeech> prepare(Context context) {
        return Single.create(emitter -> new TextToSpeech(context, status -> Timber.d("TTS status code: %s", status), ENGINE));
    }
}
