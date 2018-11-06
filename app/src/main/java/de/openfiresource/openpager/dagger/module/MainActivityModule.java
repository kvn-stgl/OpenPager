package de.openfiresource.openpager.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.openpager.ui.OperationListFragment;

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector()
    abstract OperationListFragment provideOperationListFragment();
}