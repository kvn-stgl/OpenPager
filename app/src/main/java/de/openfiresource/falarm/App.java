package de.openfiresource.falarm;

import android.Manifest;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.orm.SugarApp;
import com.orm.SugarContext;
import com.squareup.leakcanary.LeakCanary;

import de.openfiresource.falarm.models.Notification;

/**
 * Created by stieglit on 03.08.2016.
 */
public class App extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        LeakCanary.install(this);
        Dexter.initialize(this);

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

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }
}