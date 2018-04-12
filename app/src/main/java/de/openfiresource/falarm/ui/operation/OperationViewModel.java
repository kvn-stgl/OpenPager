package de.openfiresource.falarm.ui.operation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;

import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationMessage;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class OperationViewModel extends ViewModel {

    private static final String TAG = "OperationViewModel";

    private final AppDatabase database;

    private final BehaviorSubject<Long> operationIdSubject = BehaviorSubject.create();

    @Inject
    OperationViewModel(final @NonNull AppDatabase database) {
        this.database = database;
    }

    LiveData<OperationMessage> getOperation() {
        Flowable<OperationMessage> operationMessageFlowable = operationIdSubject
                .toFlowable(BackpressureStrategy.BUFFER)
                .switchMap(database.operationMessageDao()::findByIdAsync)
                .observeOn(Schedulers.io());

        return LiveDataReactiveStreams.fromPublisher(operationMessageFlowable);
    }

    void setOperationId(long operationId) {
        operationIdSubject.onNext(operationId);
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
    }
}
