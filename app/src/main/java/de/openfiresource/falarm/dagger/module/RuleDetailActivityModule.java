package de.openfiresource.falarm.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.falarm.ui.settings.RuleDetailFragment;

@Module
abstract class RuleDetailActivityModule {

    @ContributesAndroidInjector()
    abstract RuleDetailFragment provideRuleDetailFragment();

}
