package de.openfiresource.falarm.ui.settings;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.UserRepository;
import de.openfiresource.falarm.utils.Constants;
import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements HasFragmentInjector {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            CharSequence summary = index >= 0
                    ? listPreference.getEntries()[index]
                    : null;

            preference.setSummary(summary);

        } else if (preference instanceof RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to 'silent' (no ringtone).
                preference.setSummary(R.string.pref_ringtone_silent);

            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            }

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference, SharedPreferences sharedPreferences) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                sharedPreferences.getString(preference.getKey(), ""));
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        bindPreferenceSummaryToValue(preference, sharedPreferences);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.view_settings_toolbar, root, false);
        Toolbar toolbar = (Toolbar) bar.getChildAt(0);
        root.addView(bar, 0); // insert at top

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_GENERAL_ALARM_TIMEOUT));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_GENERAL_ALARM_MAPS));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class NotificationPreferenceFragment extends PreferenceFragment {

        public static final String ARG_RULE_ID = "ruleid";

        /**
         * The actual rule Id
         */
        private long mRuleId;

        /**
         * The actual preference Manager.
         */
        private PreferenceManager mPreferenceManager;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getArguments() != null) {
                mRuleId = getArguments().getLong(ARG_RULE_ID, 0);
            }

            mPreferenceManager = getPreferenceManager();
            mPreferenceManager.setSharedPreferencesName(Notification.getSharedPreferencesName(mRuleId));
            mPreferenceManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_volume"), mPreferenceManager.getSharedPreferences());
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"), mPreferenceManager.getSharedPreferences());
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_vibrate"), mPreferenceManager.getSharedPreferences());
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class DataSyncPreferenceFragment extends PreferenceFragment {

        @Inject
        public UserRepository userRepository;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            AndroidInjection.inject(this);

            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_password"));

            Preference fcmPreference = new Preference(getActivity());
            fcmPreference.setTitle(getString(R.string.pref_title_fcm));
            final String token = FirebaseInstanceId.getInstance().getToken();
            fcmPreference.setSummary(token);
            fcmPreference.setOnPreferenceClickListener(preference -> {
                new AlertDialog
                        .Builder(getActivity())
                        .setTitle(R.string.pref_fcm_alert_title)
                        .setMessage(R.string.pref_fcm_alert_message)
                        .setPositiveButton(android.R.string.yes, (dialog, id) -> sendFCMKey())
                        .setNegativeButton(android.R.string.no, (dialog, id) -> {
                            // User cancelled the dialog
                        }).create().show();
                return true;
            });
            getPreferenceScreen().addPreference(fcmPreference);
        }

        private void sendFCMKey() {
            String token = FirebaseInstanceId.getInstance().getToken();
            userRepository.sendDeviceInfo(token)
                    .subscribe(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            Timber.d("Successfully sent token %s to server", token);
                            dispose();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "Error sending token to server");
                            dispose();
                        }
                    });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
