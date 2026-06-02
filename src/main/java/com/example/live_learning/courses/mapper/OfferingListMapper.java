
	package com.example.live_learning.courses.mapper;

	import com.example.live_learning.courses.dto.OfferingListDto;
	import com.example.live_learning.courses.entity.Offering;
	import org.mapstruct.Context;
	import org.mapstruct.Mapper;
	import org.mapstruct.Mapping;
	import org.mapstruct.Named;

	import java.time.Instant;
	import java.time.ZoneId;
	import java.time.ZonedDateTime;
	import java.time.format.DateTimeFormatter;
	import java.util.List;

	@Mapper(componentModel = "spring")
	public interface OfferingListMapper {

		@Mapping(target = "courseId", source = "course.id")
	    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "convertTimeToTargetZone")
	    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "convertTimeToTargetZone")
	    OfferingListDto toDto(Offering offering, @Context String timezone);

	 
	    List<OfferingListDto> toDtoList(List<Offering> offerings, @Context String timezone);

	    
	    @Named("convertTimeToTargetZone")
	    default String convertTimeToTargetZone(Instant utcInstant, @Context String timezone) {
	        if (utcInstant == null) return null;
	        ZoneId targetZone = (timezone != null) ? ZoneId.of(timezone) : ZoneId.systemDefault();
	        ZonedDateTime localTime = utcInstant.atZone(targetZone);
	        return localTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); 
	    
	    }
	}

