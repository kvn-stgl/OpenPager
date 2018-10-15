package de.openfiresource.openpager.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.openfiresource.openpager.R;
import de.openfiresource.openpager.dagger.Injectable;
import de.openfiresource.openpager.models.AppDatabase;
import de.openfiresource.openpager.models.Notification;
import de.openfiresource.openpager.models.database.OperationRule;
import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A fragment representing a single Rule detail screen.
 * This fragment is either contained in a {@link RuleListActivity}
 * in two-pane mode (on tablets) or a {@link RuleDetailActivity}
 * on handsets.
 */
public class RuleDetailFragment extends PreferenceFragment implements Injectable {

    private static final String TAG = "RuleDetailFragment";

    /**
     * The fragment argument representing the rule ID that this fragment
     * represents.
     */
    public static final String ARG_RULE_ID = "rule_id";

    /**
     * The actual notification rule.
     */
    private OperationRule operationRule;

    @Inject
    AppDatabase database;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value and save the preference to the database.
     */
    private final Preference.OnPreferenceChangeListener sBindPreferenceToDatabaseListener = (preference, value) -> {
        if (value == null) {
            return true;
        }
        String methodName = getMethodFromPrefKey(preference, "set");
        String stringValue = value.toString();
        Method setter;

        try {
            if (preference instanceof CheckBoxPreference) {
                setter = operationRule.getClass().getMethod(methodName, boolean.class);
                setter.invoke(operationRule, Boolean.parseBoolean(stringValue));
            } else {
                setter = operationRule.getClass().getMethod(methodName, String.class);
                setter.invoke(operationRule, stringValue);
                preference.setSummary(stringValue);
            }
        } catch (SecurityException e) {
            Timber.e(e, "Security Exception on relfection");
        } catch (NoSuchMethodException e) {
            Timber.e(e, "Getter not found");
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Completable.fromAction(() -> database.operationRuleDao().updateOperationRule(operationRule))
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: save operation rule");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: save operation rule: ", e);
                    }
                });

        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RULE_ID)) {
            long id = getArguments().getLong(ARG_RULE_ID);
            operationRule = database.operationRuleDao().findById(id);

            final Activity activity = this.getActivity();
            Toolbar appBarLayout = activity.findViewById(R.id.toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle(operationRule.getTitle());
            }

            addPreferencesFromResource(R.xml.rule_pref);

            int preferences = getPreferenceScreen().getPreferenceCount();
            for (int i = 0; i < preferences; i++) {
                Preference preference = getPreferenceScreen().getPreference(i);
                bindPreferenceToDatabase(preference);
            }

            Preference notification = new Preference(getActivity());
            this.getPreferenceScreen().addPreference(notification);
            notification.setTitle(getString(R.string.pref_title_notification));
            notification.setSummary(getString(R.string.pref_desc_notification));
            notification.setDependency("rule_ownNotification");
            notification.setOnPreferenceClickListener(preference -> {
                Bundle bundle = new Bundle();
                bundle.putLong(SettingsActivity.NotificationPreferenceFragment.ARG_RULE_ID, operationRule.getId());
                SettingsActivity.NotificationPreferenceFragment fragment
                        = new SettingsActivity.NotificationPreferenceFragment();
                fragment.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.rule_detail_container, fragment)
                        .addToBackStack(null)
                        .commit();

                return true;
            });

            Preference delete = new Preference(getActivity());
            delete.setTitle(getString(R.string.pref_title_delete));
            delete.setSummary(getString(R.string.pref_desc_delete));
            delete.setOnPreferenceClickListener(preference -> {
                // User clicked delete button.
                // Confirm that's what they want.
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.pref_title_delete))
                        .setMessage(getString(R.string.pref_desc_delete))
                        .setPositiveButton(getString(R.string.delete),
                                (dialog, whichButton) -> {
                                    Completable.fromAction(() -> database.operationRuleDao().deleteOperationRule(operationRule))
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(new DisposableCompletableObserver() {
                                                @Override
                                                public void onComplete() {
                                                    Log.d(TAG, "onComplete: delete operation rule");
                                                    Notification.byRule(operationRule, getActivity()).delete();
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Log.e(TAG, "onError deleting operation rule: ", e);
                                                }
                                            });

                                    if (activity.getClass().equals(RuleDetailActivity.class)) {
                                        activity.navigateUpTo(new Intent(activity, RuleListActivity.class));
                                    } else {
                                        startActivity(new Intent(activity, RuleListActivity.class));
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                (dialog, whichButton) -> {
                                    // No need to take any action.
                                }).show();
                return true;
            });
            this.getPreferenceScreen().addPreference(delete);
        }
    }

    private String getMethodFromPrefKey(Preference preference, String type) {
        String methodName = preference.getKey().substring(5); //Cut rule_
        methodName = type + methodName.substring(0, 1).toUpperCase(Locale.ROOT) + methodName.substring(1); //GetX
        return methodName;
    }

    private void bindPreferenceToDatabase(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceToDatabaseListener);

        // Trigger the listener immediately with the preference's
        // current value.
        Method getter;
        String type = "get";
        if (preference instanceof CheckBoxPreference) {
            type = "is";
        }
        String methodName = getMethodFromPrefKey(preference, type);

        try {
            getter = operationRule.getClass().getMethod(methodName);
            Object value = getter.invoke(operationRule);

            if (preference instanceof CheckBoxPreference) {
                ((CheckBoxPreference) preference).setChecked((Boolean) value);
            }

            sBindPreferenceToDatabaseListener.onPreferenceChange(preference, value);
        } catch (SecurityException e) {
            Timber.e(e, "Security Exception on relfection");
        } catch (NoSuchMethodException e) {
            Timber.e(e, "Getter not found");
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // We have to inject here because this Fragment is not a SupportFragment
        AndroidInjection.inject(this);
    }
}
