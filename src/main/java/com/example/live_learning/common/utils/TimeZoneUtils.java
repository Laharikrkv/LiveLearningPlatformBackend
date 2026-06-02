package com.example.live_learning.common.utils;



import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeZoneUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a z");

    public String localizeInstant(Instant instant, String targetTimeZoneStr) {
        if (instant == null) return null;
        
        // Default to India Standard Time if the user profile doesn't specify a timezone
        String timeZoneId = (targetTimeZoneStr != null) ? targetTimeZoneStr : "Asia/Kolkata";
        ZoneId zoneId = ZoneId.of(timeZoneId);
        
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.format(FORMATTER);
    }
}
