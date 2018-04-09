package de.openfiresource.falarm.ui.settings;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationRule;

public class RuleListViewModel extends ViewModel {

    private final LiveData<List<OperationRule>> operationRuleList;

    @Inject
    RuleListViewModel(final @NonNull AppDatabase database) {
        operationRuleList = database.operationRuleDao().getAllAsync();
    }

    public LiveData<List<OperationRule>> getOperationRuleList() {
        return operationRuleList;
    }
}
