package me.joeleoli.hcfactions.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatToMMSS(int seconds) {
        int millis = seconds * 1000;
        int sec = millis / 1000 % 60;
        int min = millis / 60000 % 60;
        int hr = millis / 3600000 % 24;
        return ((hr > 0) ? String.format("%02d", hr) : "") + String.format("%02d:%02d", min, sec);
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return sb.toString();
    }

}