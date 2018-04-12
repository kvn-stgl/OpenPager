package de.openfiresource.falarm.dagger.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.openfiresource.falarm.dagger.ViewModelFactory;
import de.openfiresource.falarm.dagger.ViewModelKey;
import de.openfiresource.falarm.ui.OperationListViewModel;
import de.openfiresource.falarm.ui.settings.RuleListViewModel;

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
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
