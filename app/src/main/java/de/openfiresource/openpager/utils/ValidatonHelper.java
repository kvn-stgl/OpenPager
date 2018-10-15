package de.openfiresource.openpager.utils;

import android.text.TextUtils;

public class ValidatonHelper {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
