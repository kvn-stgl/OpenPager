package de.openfiresource.openpager.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.openpager.ui.operation.OperationFragment;
import de.openfiresource.openpager.ui.operation.OsmMapFragment;

@Module
abstract class OperationActivityModule {

    @ContributesAndroidInjector()
    abstract OperationFragment provideOperationFragment();

    @ContributesAndroidInjector()
    abstract OsmMapFragment provideOsmMapFragment();

}
