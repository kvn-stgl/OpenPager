package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.ui.MainActivity;
import de.openfiresource.falarm.ui.OperationActivity;
import de.openfiresource.falarm.ui.settings.RuleDetailActivity;
import de.openfiresource.falarm.ui.settings.RuleListActivity;
import de.openfiresource.falarm.ui.settings.SettingsActivity;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract OperationActivity bindOperationActivity();

    @ContributesAndroidInjector
    abstract RuleDetailActivity bindRuleDetailActivity();

    @ContributesAndroidInjector
    abstract RuleListActivity bindRuleListActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity bindSettingsActivity();
}
