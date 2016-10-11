package de.openfiresource.falarm.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.openfiresource.falarm.R;

/**
 * Created by stieglit on 17.08.2016.
 */
public class Notification {
    private Context mContext;

    private SharedPreferences mSharedPreferences;

    private long mRuleId;

    public Notification(long ruleId, Context mContext) {
        this.mContext = mContext;
        this.mRuleId = ruleId;
        this.mSharedPreferences = mContext.getSharedPreferences(getSharedPreferencesName(ruleId), mContext.MODE_PRIVATE);
    }

    public void loadDefault() {
        PreferenceManager.setDefaultValues(mContext, getSharedPreferencesName(mRuleId), mContext.MODE_PRIVATE, R.xml.pref_notification, false);
    }

    public static Notification byRule(OperationRule rule, Context context) {
        long id = 0;
        if (rule != null && rule.isOwnNotification())
            id = rule.getId();

        return new Notification(id, context);
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
        assert mSharedPreferences != null;
        return mSharedPreferences.getBoolean("notifications_play_sound", true);
    }

    /**
     * Gibt den Alarmton an.
     *
     * @return Alarmton.
     */
    public String getRingtone() {
        assert mSharedPreferences != null;
        return mSharedPreferences.getString("notifications_new_message_ringtone", "");
    }

    /**
     * Gibt das Volumen in Prozent an, wie laut der Ton ist.
     *
     * @return Volume in Prozent, 0 wenn Telefonlautstärke.
     */
    public String getNewMessageVolume() {
        assert mSharedPreferences != null;
        return mSharedPreferences.getString("notifications_new_message_volume", "0");
    }

    /**
     * Gibt die Dauern an, wie lange das Handy bei einem neuen Alarm vibriert.
     *
     * @return Dauer, 0 wenn aus.
     */
    public String getNewMessageVibrate() {
        assert mSharedPreferences != null;
        return mSharedPreferences.getString("notifications_new_message_vibrate", "1000");
    }

    /**
     * Gibt an, ob die LED bei einen neuen Alarm blinken soll.
     *
     * @return True, wenn blinken
     */
    public Boolean isNewMessageLED() {
        assert mSharedPreferences != null;
        return mSharedPreferences.getBoolean("notifications_new_message_led", false);
    }

    /**
     * Gibt an, ob der SpeakService aktiviert ist.
     *
     * @return True, wenn aktiviert
     */
    public Boolean isSpeakServiceEnabled() {
        assert mSharedPreferences != null;
        return mSharedPreferences.getBoolean("notifications_speak", false);
    }

    public void delete() {
        mSharedPreferences.edit().clear().commit();
    }
}
