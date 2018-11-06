package de.openfiresource.openpager.utils;

import android.content.Context;

import java.util.Date;

import de.openfiresource.openpager.R;

public class TimeHelper {
    public static String getDiffText(Context context, Date timestamp) {
        Date actual = new Date();
        long diffInSeconds = (actual.getTime() - timestamp.getTime()) / 1000;
        long diff[] = new long[]{0, 0, 0, 0};
        /* sec */
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        diff[0] = (diffInSeconds / 24);

        //String
        String text = "vor ";
        if (diff[0] > 0) {
            text += context.getResources().getQuantityString(R.plurals.time_helper_diff_day, (int) diff[0], diff[0]) + " ";
        }
        if (diff[1] > 0) {
            text += context.getResources().getQuantityString(R.plurals.time_helper_diff_hour, (int) diff[1], diff[1]) + " ";
        }
        if (diff[2] > 0) {
            text += context.getResources().getQuantityString(R.plurals.time_helper_diff_minute, (int) diff[2], diff[2]) + " ";
        }

        text += context.getResources().getQuantityString(R.plurals.time_helper_diff_second, (int) diff[3], diff[3]);
        return text;
    }
}
