package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.ui.settings.RuleDetailFragment;
import de.openfiresource.falarm.ui.settings.SettingsActivity;

@Module
abstract class SettingsActivityModule {

    @ContributesAndroidInjector()
    abstract SettingsActivity.DataSyncPreferenceFragment provideDataSyncPreferenceFragment();

}
