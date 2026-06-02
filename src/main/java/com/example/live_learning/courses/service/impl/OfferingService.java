package com.example.live_learning.courses.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.live_learning.common.exceptions.ResourceNotFoundException;
import com.example.live_learning.courses.dto.OfferingDto;
import com.example.live_learning.courses.entity.Course;
import com.example.live_learning.courses.entity.Offering;
import com.example.live_learning.courses.entity.Session;
import com.example.live_learning.courses.repository.CourseRepository;
import com.example.live_learning.courses.repository.OfferingRepository;
import com.example.live_learning.courses.service.OfferingServiceInterface;
import com.example.live_learning.users.UserDto;
import com.example.live_learning.users.UserQueryService;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfferingService implements OfferingServiceInterface {
	
	private final OfferingRepository offeringRepo;
	private final CourseRepository courseRepository;
	private final UserQueryService userQueryService;
	
	@Override
	@Transactional
	public Offering createOfferingWithCompactSchedule(OfferingDto dto) {
	    Course course = courseRepository.findById(dto.getCourseId())
	            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

	    UserDto teacher = userQueryService.getUserById(dto.getTeacherId());  
	    ZoneId teacherZoneId = ZoneId.of(teacher.getTimezone());

	   
	    Offering offering = new Offering();
	    offering.setTitle(dto.getTitle());
	    offering.setCourse(course);
	    offering.setTeacherId(dto.getTeacherId());
	    offering.setMaxCapacity(dto.getMaxCapacity());
	    offering.setCurrentBookingsCount(0);

	    
	    if (dto.getEndTime().isBefore(dto.getStartTime())) {
	        throw new IllegalArgumentException("End time must be after start time.");
	    }

	   
	    LocalDate currentLoopDate = dto.getStartDate();
	    LocalDate terminationDate = dto.getEndDate();

	    while (!currentLoopDate.isAfter(terminationDate)) {
	     
	        if (dto.getDaysOfWeek().contains(currentLoopDate.getDayOfWeek())) {
	            
	            LocalDateTime localStart = LocalDateTime.of(currentLoopDate, dto.getStartTime());
	            LocalDateTime localEnd = LocalDateTime.of(currentLoopDate, dto.getEndTime());

	            Instant startUtc = ZonedDateTime.of(localStart, teacherZoneId).toInstant();
	            Instant endUtc = ZonedDateTime.of(localEnd, teacherZoneId).toInstant();
	            
	            boolean isConflict = offeringRepo.hasTeacherConflict(dto.getTeacherId(), startUtc, endUtc);

	            if (isConflict) {
	                throw new IllegalArgumentException("The teacher is already assigned to a session during this timeframe!");
	            }

	            Session session = new Session();
	            session.setStartTime(startUtc);
	            session.setEndTime(endUtc);
	            session.setOffering(offering); 
	            session.setTeacherId(offering.getTeacherId());
	            session.setSessionDate(currentLoopDate.toString());

	            offering.getSessions().add(session);
	        }
	    
	        currentLoopDate = currentLoopDate.plusDays(1);
	    }

	    if (offering.getSessions().isEmpty()) {
	        throw new IllegalArgumentException("No matching calendar days found in the provided range.");
	    }

	
	    Instant absoluteEarliest = offering.getSessions().stream()
	            .map(Session::getStartTime)
	            .min(Instant::compareTo).orElseThrow();

	    Instant absoluteLatest = offering.getSessions().stream()
	            .map(Session::getEndTime)
	            .max(Instant::compareTo).orElseThrow();

	    offering.setStartTime(absoluteEarliest);
	    offering.setEndTime(absoluteLatest);

	
	    return offeringRepo.save(offering);
	}

	
	@Override	
	public List<Offering> getOfferingByCourseId(Long courseId) {
		List<Offering> offerings = offeringRepo.findByCourseId(courseId);
		if (offerings.isEmpty()) {
			throw new ResourceNotFoundException("No offerings found for course ID: " + courseId);
		}
		return offerings;
	}

	@Override
	public List<Offering> getAllOfferings() {

		List<Offering> offerings = offeringRepo.findAll();
		if (offerings.isEmpty()) {
			throw new ResourceNotFoundException("No offerings found");
		}
		return offerings;
	}
	
	

	@Override
	public List<Offering> getUpcomingOfferingsByTeacher(Long teacherId) {
		
		 Instant now = Instant.now();
		List<Offering> offerings = offeringRepo.findUpcomingOfferingsByTeacher(teacherId, now);
		if (offerings.isEmpty()) {
			throw new ResourceNotFoundException("No offerings found");
		}
		return offerings;
	}
}
