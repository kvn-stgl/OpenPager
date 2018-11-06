package de.openfiresource.openpager.utils;

import de.openfiresource.openpager.Properties;

public class Constants {

    private static final String BACKEND_URL = Properties.BACKEND_URL;

    public static final String BACKEND_URL_API = BACKEND_URL + "/api/";

    public static final String BACKEND_URL_SIGN_UP = BACKEND_URL + "/accounts/signup/";

    public static final String BACKEND_URL_PASSWORD_RESET = BACKEND_URL + "/accounts/password/reset/";

    public static final String PREF_GENERAL_ALARM_FONTSIZE = "general_alarm_fontsize";

    public static final String PREF_GENERAL_ALARM_TIMEOUT = "general_alarm_timeout";

    public static final String PREF_GENERAL_ALARM_MAPS = "general_alarm_maps";

    public static final String PREF_SYNC_ENCRYPTION_ENABLED = "sync_encryption";

    public static final String PREF_SYNC_ENCRYPTION_PASSWORD = "sync_password";

    public static final String PREF_IS_PUSH_ACTIVE = "active";
}
