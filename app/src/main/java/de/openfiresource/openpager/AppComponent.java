package de.openfiresource.openpager;


import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import de.openfiresource.openpager.dagger.module.ActivityBuildersModule;
import de.openfiresource.openpager.dagger.module.DatabaseModule;
import de.openfiresource.openpager.dagger.module.PreferencesModule;
import de.openfiresource.openpager.dagger.module.ViewModelModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBuildersModule.class,
        DatabaseModule.class,
        PreferencesModule.class,
        ViewModelModule.class
})
public interface AppComponent extends AndroidInjector {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(App app);
}
