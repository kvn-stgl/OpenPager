package de.openfiresource.openpager.dagger.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.openfiresource.openpager.ui.settings.RuleDetailFragment;

@Module
abstract class RuleDetailActivityModule {

    @ContributesAndroidInjector()
    abstract RuleDetailFragment provideRuleDetailFragment();

}
