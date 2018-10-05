package de.openfiresource.falarm.utils;

import android.os.Build;

import de.openfiresource.falarm.models.api.Device;

public class DeviceInfoHelper {
    public static Device create(String fcmToken) {
        Device.Builder builder = new Device.Builder();

        builder.fcmToken(fcmToken)
                .device(Build.DEVICE)
                .deviceName(getDeviceName())
                .manufacturer(Build.MANUFACTURER)
                .platform("Android")
                .version(Build.VERSION.RELEASE);

        return builder.build();
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
