package com.gbsoft.smartpatient.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// class to do time related operation to return result in human readable form
public class TimeHelper {

    // input - local time object
    // output - string in xh xm remaining
    public static String calculateTimeDiff(LocalTime localTime) {
        long min = Duration.between(LocalTime.now(), localTime).toMinutes();
        int hr = Math.toIntExact(min / 60);
        int mins = Math.toIntExact(min % 60);
        if (hr == 0 && mins == 0) {
            return "about to go off now";
        }
        if (hr == 0)
            return String.format(Locale.getDefault(), "%dm remaining", mins);
        else
            return String.format(Locale.getDefault(), "%dh remaining", hr);
    }

    public static String calculateLocalDateTimeDiff(LocalDateTime localDateTime) {
        long minutes = Duration.between(LocalDateTime.now(), localDateTime).toMinutes();
        int day = Math.toIntExact(minutes / 1440);
        int remainingMin = Math.toIntExact((minutes % 1440));
        int hr = Math.toIntExact(remainingMin / 60);
        int min = Math.toIntExact((remainingMin) % 60) + 1;

        if (day == 0) {
            if (hr == 0) {
                if (min == 0)
                    return "about to go off now";
                else if (min == 60)
                    return "1h remaining";
                else
                    return String.format(Locale.getDefault(), "%dm remaining", min);
            } else {
                if (min == 0)
                    return String.format(Locale.getDefault(), "%dh remaining", hr);
                else if (min == 60)
                    return String.format(Locale.getDefault(), "%dh remaining", hr + 1);
                else
                    return String.format(Locale.getDefault(), "%dh %dm remaining", hr, min);
            }
        } else {
            if (hr == 0)
                return String.format(Locale.getDefault(), "about %dd remaining", day);
            else
                return String.format(Locale.getDefault(), "about %dd %dh remaining", day, hr);
        }
    }

    public static long calculateTimeDiffLong(LocalTime localTime) {
        return Duration.between(LocalTime.now(), localTime).toMillis();
    }

    public static long calculateTimeDiffLong(LocalDateTime localDateTime) {
        return Duration.between(LocalDateTime.now(), localDateTime).toMillis();
    }

    public static LocalTime addHours(int hr, LocalTime localTime) {
        return localTime.plusHours(hr);
    }

    public static String formatLocalTime(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static LocalTime toLocalTime(String localTime) {
        return LocalTime.parse(localTime, DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static String formatLocalDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("uuuu/MM/dd"));
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("uuuu/MM/dd hh:mm a"));
    }

    public static long toTimestamp(LocalDate localDate) {
        return localDate.atStartOfDay().toInstant(ZoneOffset.ofHoursMinutes(5, 45)).toEpochMilli();
    }

    public static long toTimestamp(LocalTime localTime) {
        return localTime.toSecondOfDay() * 1000;
    }

    public static LocalTime toLocalTime(long timestamp) {
        return LocalTime.ofSecondOfDay(timestamp / 1000);
    }

    public static LocalDate toLocalDate(long timeStamp) {
        return Instant.ofEpochMilli(timeStamp).atOffset(ZoneOffset.ofHoursMinutes(5, 45)).toLocalDate();
    }

    public static String toString(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.ofHoursMinutes(5, 45)).
                format(DateTimeFormatter.ofPattern("uuuu/MM/dd hh:mm a"));
    }
}
