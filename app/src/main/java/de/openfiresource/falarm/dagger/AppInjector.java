package de.openfiresource.falarm.dagger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import de.openfiresource.falarm.App;
import de.openfiresource.falarm.AppComponent;
import de.openfiresource.falarm.DaggerAppComponent;

/**
 * Helper class to automatically inject fragments if they implement {@link Injectable}.
 */
public class AppInjector {
    private AppInjector() {
    }

    public static AppComponent init(App app) {
        AppComponent appComponent = DaggerAppComponent
                .builder()
                .application(app)
                .build();

        appComponent.inject(app);

        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        return appComponent;
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasSupportFragmentInjector || activity instanceof Injectable) {
            AndroidInjection.inject(activity);
        }
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity)
                    .getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                        @Override
                        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
                            super.onFragmentPreAttached(fm, f, context);

                            if (f instanceof Injectable) {
                                AndroidSupportInjection.inject(f);
                            }
                        }
                    }, true);
        }
    }
}