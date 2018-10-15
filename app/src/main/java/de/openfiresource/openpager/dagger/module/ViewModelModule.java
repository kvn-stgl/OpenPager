package de.openfiresource.openpager.dagger.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.openfiresource.openpager.dagger.ViewModelFactory;
import de.openfiresource.openpager.dagger.ViewModelKey;
import de.openfiresource.openpager.ui.OperationListViewModel;
import de.openfiresource.openpager.ui.operation.OperationViewModel;
import de.openfiresource.openpager.ui.settings.RuleListViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(OperationListViewModel.class)
    abstract ViewModel bindOperationListViewModel(OperationListViewModel operationListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RuleListViewModel.class)
    abstract ViewModel bindRuleListViewModel(RuleListViewModel ruleListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OperationViewModel.class)
    abstract ViewModel bindOperationViewModel(OperationViewModel ruleListViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
