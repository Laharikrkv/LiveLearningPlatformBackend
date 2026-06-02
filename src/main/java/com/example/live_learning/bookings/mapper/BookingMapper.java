package com.example.live_learning.bookings.mapper;


import com.example.live_learning.bookings.entity.Booking;
import com.example.live_learning.courses.dto.BookingDto;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "bookingStatus", source = "status")
    @Mapping(target = "localizedBookingDate", source = "bookedAtUtc", qualifiedByName = "toParentLocalDate")
    BookingDto toDto(Booking booking, @Context String parentTimezone);

    @Named("toParentLocalDate")
    default String toParentLocalDate(Instant utcInstant, @Context String parentTimezone) {
        if (utcInstant == null) return null;
        ZoneId zoneId = resolveParentZone(parentTimezone);
        return utcInstant.atZone(zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    default ZoneId resolveParentZone(String parentTimezone) {
        return (parentTimezone != null && !parentTimezone.isBlank()) 
                ? ZoneId.of(parentTimezone) 
                : ZoneId.of("Asia/Kolkata");
    }
}