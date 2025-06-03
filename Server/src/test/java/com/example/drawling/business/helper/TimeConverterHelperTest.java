package com.example.drawling.business.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TimeConverterHelperTest {

    @Test
    void testConvertToUTC() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 1, 12, 0);
        ZonedDateTime expected = localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        ZonedDateTime result = TimeConverterHelper.convertToUTC(localDateTime);

        assertEquals(expected, result);
    }

    @Test
    void testConvertToLocal() {
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        ZoneId localZoneId = ZoneId.of("America/New_York");
        LocalDateTime expected = utcDateTime.withZoneSameInstant(localZoneId).toLocalDateTime();

        LocalDateTime result = TimeConverterHelper.convertToLocal(utcDateTime, localZoneId);

        assertEquals(expected, result);
    }

    @Test
    void testGetLocalTime() {
        LocalDateTime result = TimeConverterHelper.getLocalTime();
        assertNotNull(result);
    }

    @Test
    void testGetCurrentTimeInUTC() {
        ZonedDateTime result = TimeConverterHelper.getCurrentTimeInUTC();
        assertNotNull(result);
        assertEquals("UTC", result.getZone().getId());
    }

    @Test
    void testGetCurrentTimeInLocal() {
        ZoneId localZoneId = ZoneId.systemDefault();
        ZonedDateTime result = TimeConverterHelper.getCurrentTimeInLocal(localZoneId);
        assertNotNull(result);
        assertEquals(localZoneId, result.getZone());
    }
}