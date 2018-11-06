package de.openfiresource.openpager.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.openpager.MyFirebaseMessagingService;
import de.openfiresource.openpager.service.AlarmService;
import de.openfiresource.openpager.service.SpeakService;
import de.openfiresource.openpager.ui.LoginActivity;
import de.openfiresource.openpager.ui.LogoutActivity;
import de.openfiresource.openpager.ui.MainActivity;
import de.openfiresource.openpager.ui.StartActivity;
import de.openfiresource.openpager.ui.operation.OperationActivity;
import de.openfiresource.openpager.ui.settings.RuleDetailActivity;
import de.openfiresource.openpager.ui.settings.RuleListActivity;
import de.openfiresource.openpager.ui.settings.SettingsActivity;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract StartActivity bindStartActivity();

    @ContributesAndroidInjector()
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector()
    abstract LogoutActivity bindLogoutActivity();

    @ContributesAndroidInjector(modules = OperationActivityModule.class)
    abstract OperationActivity bindOperationActivity();

    @ContributesAndroidInjector(modules = RuleDetailActivityModule.class)
    abstract RuleDetailActivity bindRuleDetailActivity();

    @ContributesAndroidInjector(modules = RuleDetailActivityModule.class)
    abstract RuleListActivity bindRuleListActivity();

    @ContributesAndroidInjector(modules = SettingsActivityModule.class)
    abstract SettingsActivity bindSettingsActivity();

    @ContributesAndroidInjector
    abstract AlarmService bindAlarmService();

    @ContributesAndroidInjector
    abstract SpeakService bindSpeakService();

    @ContributesAndroidInjector
    abstract MyFirebaseMessagingService bindFirebaseService();
}
