package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.ui.OperationFragment;

@Module
abstract class OperationActivityModule {

    @ContributesAndroidInjector()
    abstract OperationFragment provideOperationFragment();

}
