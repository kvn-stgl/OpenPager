package de.openfiresource.openpager.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.openpager.ui.settings.SettingsActivity;

@Module
abstract class SettingsActivityModule {

    @ContributesAndroidInjector()
    abstract SettingsActivity.DataSyncPreferenceFragment provideDataSyncPreferenceFragment();

}
