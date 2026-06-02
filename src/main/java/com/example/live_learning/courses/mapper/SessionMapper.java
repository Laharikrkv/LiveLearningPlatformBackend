package com.example.live_learning.courses.mapper;

import com.example.live_learning.courses.dto.SessionDto;
import com.example.live_learning.courses.entity.Session;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Mapper(componentModel = "spring")
public interface SessionMapper {

   
    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "convertInstantToStringTime")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "convertInstantToStringTime")

    SessionDto toDto(Session session, @Context String targetTimezone);

    List<SessionDto> toDtoList(List<Session> sessions, @Context String targetTimezone);

    @Named("convertInstantToStringTime")
    default String convertInstantToStringTime(Instant utcInstant, @Context String targetTimezone) {
        if (utcInstant == null) {
            return null;
        }
        ZoneId zoneId = (targetTimezone != null && !targetTimezone.isBlank()) 
                ? ZoneId.of(targetTimezone) 
                : ZoneId.of("Asia/Kolkata");
                
        return utcInstant.atZone(zoneId)
                .format(DateTimeFormatter.ISO_LOCAL_TIME); // e.g., "17:00:00"
    }
}