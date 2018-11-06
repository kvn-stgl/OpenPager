package de.openfiresource.openpager.dagger.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.openfiresource.openpager.utils.Preferences;

@Module(includes = AppModule.class)
public class PreferencesModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    Preferences providePreferences(SharedPreferences sharedPreferences) {
        return new Preferences(sharedPreferences);
    }
}
