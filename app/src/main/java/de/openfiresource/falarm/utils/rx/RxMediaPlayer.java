package de.openfiresource.falarm.utils.rx;


import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RxMediaPlayer {

    @NonNull
    public static Observable<Pair<Integer, Integer>> play(@NonNull MediaPlayer mediaPlayer) {
        return prepare(mediaPlayer).flatMap(RxMediaPlayer::stream);
    }

    @NonNull
    private static Observable<MediaPlayer> prepare(@NonNull final MediaPlayer mediaPlayer) {
        return Observable
                .fromCallable(() -> {
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        mediaPlayer.reset();
                        mediaPlayer.release();

                        Timber.e(e, "Error prepare MediaPlayer");
                    }
                    return mediaPlayer;
                })
                .doOnError(throwable -> {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                });
    }

    @NonNull
    private static Observable<Pair<Integer, Integer>> stream(@NonNull final MediaPlayer mediaPlayer) {
        return Completable
                .create(emitter -> {
                    emitter.setCancellable(() -> {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    });

                    mediaPlayer.start();
                })
                .andThen(ticks(mediaPlayer))
                .takeUntil(complete(mediaPlayer));
    }

    @NonNull
    private static Observable<Pair<Integer, Integer>> ticks(@NonNull final MediaPlayer mediaPlayer) {
        return Observable.interval(16, TimeUnit.MILLISECONDS, Schedulers.computation())
                .map(value -> {
                    int currentPositionInSeconds = mediaPlayer.getCurrentPosition() / 1000;
                    int durationInSeconds = mediaPlayer.getDuration() / 1000;
                    return Pair.create(currentPositionInSeconds, durationInSeconds);
                });
    }

    @NonNull
    private static Observable<MediaPlayer> complete(@NonNull final MediaPlayer mediaPlayer) {
        return Observable.create(emitter -> mediaPlayer.setOnCompletionListener(player -> {
            emitter.onNext(player);
            emitter.onComplete();
        }));
    }
}