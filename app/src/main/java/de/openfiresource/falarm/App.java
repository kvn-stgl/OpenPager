package de.openfiresource.falarm;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

import de.openfiresource.falarm.models.Notification;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        //Load Default settings
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        new Notification(0, this).loadDefault();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}