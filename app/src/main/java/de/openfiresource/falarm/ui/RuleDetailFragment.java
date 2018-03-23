package de.openfiresource.falarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.orhanobut.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.AppDatabase;
import de.openfiresource.falarm.models.Notification;
import de.openfiresource.falarm.models.database.OperationRule;

/**
 * A fragment representing a single Rule detail screen.
 * This fragment is either contained in a {@link RuleListActivity}
 * in two-pane mode (on tablets) or a {@link RuleDetailActivity}
 * on handsets.
 */
public class RuleDetailFragment extends PreferenceFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The actual notification rule.
     */
    private OperationRule operationRule;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RuleDetailFragment() {
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value and save the preference to the database.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceToDatabaseListener = (preference, value) -> {
        if (value == null)
            return true;
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
            Logger.e(e, "Security Exception on relfection");
        } catch (NoSuchMethodException e) {
            Logger.e(e, "Getter not found");
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        //todo: save operationRule

        //Send Broadcast (title or time changed)
        Intent brIntent = new Intent();
        brIntent.setAction(RuleListActivity.INTENT_RULE_CHANGED);
        getActivity().sendBroadcast(brIntent);

        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            long id = getArguments().getInt(ARG_ITEM_ID);
            operationRule = AppDatabase.getInstance(getActivity()).operationRuleDao().findById(id);

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
                                    new Notification(operationRule.getId(), getActivity()).delete();

                                    // todo: delete operationRule

                                    if (activity.getClass().equals(RuleDetailActivity.class))
                                        activity.navigateUpTo(new Intent(activity, RuleListActivity.class));
                                    else
                                        startActivity(new Intent(activity, RuleListActivity.class));
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
        methodName = type + methodName.substring(0, 1).toUpperCase() + methodName.substring(1); //GetX
        return methodName;
    }

    private void bindPreferenceToDatabase(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceToDatabaseListener);

        // Trigger the listener immediately with the preference's
        // current value.
        Method getter;
        String type = "get";
        if (preference instanceof CheckBoxPreference) type = "is";
        String methodName = getMethodFromPrefKey(preference, type);

        try {
            getter = operationRule.getClass().getMethod(methodName);
            Object value = getter.invoke(operationRule);

            if (preference instanceof CheckBoxPreference)
                ((CheckBoxPreference) preference).setChecked((Boolean) value);

            sBindPreferenceToDatabaseListener.onPreferenceChange(preference, value);
        } catch (SecurityException e) {
            Logger.e(e, "Security Exception on relfection");
        } catch (NoSuchMethodException e) {
            Logger.e(e, "Getter not found");
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
