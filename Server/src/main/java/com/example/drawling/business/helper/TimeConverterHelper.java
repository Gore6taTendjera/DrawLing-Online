package com.example.drawling.business.helper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TimeConverterHelper {
    private TimeConverterHelper() {}

    public static ZonedDateTime convertToUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
    }

    public static LocalDateTime convertToLocal(ZonedDateTime utcDateTime, ZoneId localZoneId) {
        return utcDateTime.withZoneSameInstant(localZoneId).toLocalDateTime();
    }

    public static LocalDateTime getLocalTime(){
        return LocalDateTime.now();
    }

    public static ZonedDateTime getCurrentTimeInUTC() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public static ZonedDateTime getCurrentTimeInLocal(ZoneId localZoneId) {
        return ZonedDateTime.now(localZoneId);
    }
}
