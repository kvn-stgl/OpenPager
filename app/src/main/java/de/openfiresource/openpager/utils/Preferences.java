package de.openfiresource.openpager.utils;

import android.content.SharedPreferences;

import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;

public class Preferences {

    private final SharedPreferences sharedPreferences;
    private final RxSharedPreferences rxPreferences;

    public Preferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        rxPreferences = RxSharedPreferences.create(sharedPreferences);
    }

    public Preference<String> getUserKey() {
        return rxPreferences.getString("user_key", "");
    }

    public int getAlarmFontSize() {
        String fontSizeString = sharedPreferences.getString(Constants.PREF_GENERAL_ALARM_FONTSIZE, "14");
        return Integer.parseInt(fontSizeString);
    }

    public int getAlarmTimeout() {
        return Integer.parseInt(sharedPreferences.getString(Constants.PREF_GENERAL_ALARM_TIMEOUT, "120"));
    }

    public boolean isPushActive() {
        return sharedPreferences.getBoolean(Constants.PREF_IS_PUSH_ACTIVE, true);
    }

    public String getAlarmMaps() {
        return sharedPreferences.getString(Constants.PREF_GENERAL_ALARM_MAPS, "both");
    }

    public boolean isSyncEncryptionEnabled() {
        return sharedPreferences.getBoolean(Constants.PREF_SYNC_ENCRYPTION_ENABLED, false);
    }

    public String getSyncEncryptionPassword() {
        return sharedPreferences.getString(Constants.PREF_SYNC_ENCRYPTION_PASSWORD, "");
    }
}
