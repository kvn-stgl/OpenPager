package de.openfiresource.falarm.utils;

import java.util.Date;

public class TimeHelper {
    public static String getDiffText(Date timestamp) {
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
        if (diff[0] > 0)
            text += String.format("%d Tag%s, ", diff[0], diff[0] > 1 ? "e" : "");
        if (diff[1] > 0)
            text += String.format("%d Stunde%s, ", diff[1], diff[1] > 1 ? "n" : "");
        if (diff[2] > 0)
            text += String.format("%d Minute%s, ", diff[2], diff[2] > 1 ? "n" : "");

        return text + String.format("%d Sekunde%s", diff[3], diff[3] > 1 ? "n" : "");
    }
}
