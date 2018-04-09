package de.openfiresource.falarm.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.openfiresource.falarm.R;
import de.openfiresource.falarm.models.database.OperationRule;

public class Notification {

    private Context context;

    private SharedPreferences sharedPreferences;

    private long ruleId;

    private Notification(long ruleId, Context context) {
        this.context = context;
        this.ruleId = ruleId;
        this.sharedPreferences = context.getSharedPreferences(getSharedPreferencesName(ruleId), Context.MODE_PRIVATE);
    }

    public static Notification get(long ruleId, Context context) {
        return new Notification(ruleId, context);
    }

    public static Notification byRule(OperationRule rule, Context context) {
        long id = 0;
        if (rule != null && rule.isOwnNotification()) {
            id = rule.getId();
        }

        return get(id, context);
    }

    public void loadDefault() {
        PreferenceManager.setDefaultValues(context, getSharedPreferencesName(ruleId), Context.MODE_PRIVATE, R.xml.pref_notification, false);
    }

    /**
     * A name for the shared preference.
     *
     * @return SharedPreferences Name
     */
    public static String getSharedPreferencesName(long id) {
        return "rule_" + id;
    }

    /**
     * Gibt an, ob ein Ton bei einem neuen Alarm gespielt werden soll.
     *
     * @return True, wenn ein Ton gespielt wird.
     */
    public boolean isPlayingSound() {
        assert sharedPreferences != null;
        return sharedPreferences.getBoolean("notifications_play_sound", true);
    }

    /**
     * Gibt den Alarmton an.
     *
     * @return Alarmton.
     */
    public String getRingtone() {
        assert sharedPreferences != null;
        return sharedPreferences.getString("notifications_new_message_ringtone", "");
    }

    /**
     * Gibt das Volumen in Prozent an, wie laut der Ton ist.
     *
     * @return Volume in Prozent, 0 wenn Telefonlautstärke.
     */
    public String getNewMessageVolume() {
        assert sharedPreferences != null;
        return sharedPreferences.getString("notifications_new_message_volume", "0");
    }

    /**
     * Gibt die Dauern an, wie lange das Handy bei einem neuen Alarm vibriert.
     *
     * @return Dauer, 0 wenn aus.
     */
    public String getNewMessageVibrate() {
        assert sharedPreferences != null;
        return sharedPreferences.getString("notifications_new_message_vibrate", "1000");
    }

    /**
     * Gibt an, ob die LED bei einen neuen Alarm blinken soll.
     *
     * @return True, wenn blinken
     */
    public Boolean isNewMessageLED() {
        assert sharedPreferences != null;
        return sharedPreferences.getBoolean("notifications_new_message_led", false);
    }

    /**
     * Gibt an, ob der SpeakService aktiviert ist.
     *
     * @return True, wenn aktiviert
     */
    public Boolean isSpeakServiceEnabled() {
        assert sharedPreferences != null;
        return sharedPreferences.getBoolean("notifications_speak", false);
    }

    public void delete() {
        sharedPreferences.edit().clear().apply();
    }
}
