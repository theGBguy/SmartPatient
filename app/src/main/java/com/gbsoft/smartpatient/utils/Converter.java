package com.gbsoft.smartpatient.utils;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Converter {
    @TypeConverter
    public static LocalDate toLocalDate(Long timestamp) {
        return timestamp == null ? null : TimeHelper.toLocalDate(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(LocalDate localDate) {
        return localDate == null ? null : TimeHelper.toTimestamp(localDate);
    }

    @TypeConverter
    public static Long toTimestamp(LocalTime localTime) {
        return localTime == null ? null : TimeHelper.toTimestamp(localTime);
    }

    @TypeConverter
    public static LocalTime toLocalTime(Long timestamp) {
        return timestamp == null ? null : TimeHelper.toLocalTime(timestamp);
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(String localDateTime) {
        return LocalDateTime.parse(localDateTime);
    }

    @TypeConverter
    public static String toLocalDateTimeString(LocalDateTime localDateTime) {
        return localDateTime.toString();
    }
}
