package de.openfiresource.falarm.ui.settings;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.database.OperationRule;

public class RuleListViewModel extends AndroidViewModel {

    private final LiveData<List<OperationRule>> operationRuleList;

    public RuleListViewModel(@NonNull Application application) {
        super(application);
        operationRuleList = AppDatabase.getInstance(application).operationRuleDao().getAllAsync();
    }

    public LiveData<List<OperationRule>> getOperationRuleList() {
        return operationRuleList;
    }
}
