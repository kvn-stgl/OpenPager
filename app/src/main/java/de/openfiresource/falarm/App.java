package de.openfiresource.falarm;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;
import de.openfiresource.falarm.dagger.AppInjector;
import de.openfiresource.falarm.models.Notification;
import timber.log.Timber;

public class App extends Application implements
        HasActivityInjector,
        HasServiceInjector,
        HasBroadcastReceiverInjector {


    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector;

    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;

    @Inject
    DispatchingAndroidInjector<ContentProvider> contentProviderInjector;

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = AppInjector.init(this);

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO: 05.10.2018 CrashReportingTree
        }

        //Load Default settings
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Notification.get(0, this).loadDefault();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AppComponent getComponent() {
        return component;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return broadcastReceiverInjector;
    }
}