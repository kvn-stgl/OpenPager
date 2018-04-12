package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.ui.OperationListFragment;

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector()
    abstract OperationListFragment provideOperationListFragment();
}