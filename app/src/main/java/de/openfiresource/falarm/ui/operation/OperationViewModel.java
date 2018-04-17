package de.openfiresource.falarm.ui.operation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationMessage;
import de.openfiresource.falarm.utils.TimeHelper;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class OperationViewModel extends ViewModel {

    private static final String TAG = "OperationViewModel";

    private final MutableLiveData<Long> operationId = new MutableLiveData<>();

    private final LiveData<OperationMessage> operation;

    private final LiveData<String> timer;

    @Inject
    OperationViewModel(final @NonNull AppDatabase database) {
        this.operation = Transformations.switchMap(operationId, database.operationMessageDao()::findByIdAsync);
        this.timer = Transformations.switchMap(operation, operation -> LiveDataReactiveStreams.fromPublisher(createTimer(operation)));

        Log.d(TAG, "OperationViewModel() called");
    }

    public LiveData<OperationMessage> getOperation() {
        return operation;
    }

    public LiveData<String> getTimer() {
        return timer;
    }

    void setOperationId(long operationId) {
        this.operationId.postValue(operationId);
    }

    private Flowable<String> createTimer(OperationMessage operation) {
        return Flowable
                .interval(0,1, TimeUnit.SECONDS, Schedulers.io())
                .switchMapSingle(interval ->
                        Single.create(
                                emitter -> emitter.onSuccess(TimeHelper.getDiffText(operation.getTimestamp()))
                        )
                );
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
    }
}
