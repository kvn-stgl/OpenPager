package de.openfiresource.falarm.utils;

import android.content.SharedPreferences;

public class Preferences {

    private final SharedPreferences sharedPreferences;

    public Preferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
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
