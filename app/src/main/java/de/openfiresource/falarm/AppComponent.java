package de.openfiresource.falarm;


import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import de.openfiresource.falarm.dagger.module.ActivityBuildersModule;
import de.openfiresource.falarm.dagger.module.DatabaseModule;
import de.openfiresource.falarm.dagger.module.PreferencesModule;
import de.openfiresource.falarm.dagger.module.ViewModelModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
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
