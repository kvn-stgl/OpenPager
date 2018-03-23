package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.ui.MainActivity;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

//    @ContributesAndroidInjector
//    abstract OperationActivity bindOperationActivity();
//
//    @ContributesAndroidInjector
//    abstract RuleDetailActivity bindRuleDetailActivity();
//
//    @ContributesAndroidInjector
//    abstract RuleListActivity bindRuleListActivity();
//
//    @ContributesAndroidInjector
//    abstract SettingsActivity bindSettingsActivity();
}
