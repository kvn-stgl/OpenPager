package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.MyFirebaseMessagingService;
import de.openfiresource.falarm.service.AlarmService;
import de.openfiresource.falarm.service.SpeakService;
import de.openfiresource.falarm.ui.MainActivity;
import de.openfiresource.falarm.ui.OperationActivity;
import de.openfiresource.falarm.ui.settings.RuleDetailActivity;
import de.openfiresource.falarm.ui.settings.RuleListActivity;
import de.openfiresource.falarm.ui.settings.SettingsActivity;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = OperationActivityModule.class)
    abstract OperationActivity bindOperationActivity();

    @ContributesAndroidInjector(modules = RuleDetailActivityModule.class)
    abstract RuleDetailActivity bindRuleDetailActivity();

    @ContributesAndroidInjector(modules = RuleDetailActivityModule.class)
    abstract RuleListActivity bindRuleListActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity bindSettingsActivity();

    @ContributesAndroidInjector
    abstract AlarmService bindAlarmService();

    @ContributesAndroidInjector
    abstract SpeakService bindSpeakService();

    @ContributesAndroidInjector
    abstract MyFirebaseMessagingService bindFirebaseService();
}
