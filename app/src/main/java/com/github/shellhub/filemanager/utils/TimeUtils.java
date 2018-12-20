package com.github.shellhub.filemanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String formatDuration(long duration) {
        long hours = duration / ConstantUtils.ONE_HOUR;
        long minutes = (duration - hours * ConstantUtils.ONE_HOUR) / ConstantUtils.ONE_MINUTE;
        long seconds = (duration - hours * ConstantUtils.ONE_HOUR - minutes * ConstantUtils.ONE_MINUTE) / ConstantUtils.ONE_SECOND;

        StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            if (hours < 10) {
                builder.append("0").append(hours);
            } else {
                builder.append(hours);
                builder.append(hours);
            }
            builder.append(":");
        }
        if (minutes < 10) {
            builder.append("0").append(minutes);
        } else {
            builder.append(minutes);
        }
        builder.append(":");

        if (seconds < 10) {
            builder.append("0").append(seconds);
        } else {
            builder.append(seconds);
        }
        return builder.toString();
    }

    public static String formatDate (long time){
        return new SimpleDateFormat("MM/d/YY,hh:mm a", Locale.ENGLISH)
                .format(new Date(time));
    }
}
