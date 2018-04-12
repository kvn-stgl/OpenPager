package de.openfiresource.falarm.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationMessage;

public class OperationListViewModel extends ViewModel {

    private final LiveData<List<OperationMessage>> operationMessageList;

    @Inject
    OperationListViewModel(final @NonNull AppDatabase database) {
        operationMessageList = database.operationMessageDao().getAllAsync();
    }

    public LiveData<List<OperationMessage>> getOperationMessageList() {
        return operationMessageList;
    }
}