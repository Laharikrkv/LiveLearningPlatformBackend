package com.example.live_learning.courses.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.live_learning.courses.dto.AddSessionDto;
import com.example.live_learning.courses.entity.Offering;
import com.example.live_learning.courses.entity.Session;
import com.example.live_learning.courses.repository.OfferingRepository;
import com.example.live_learning.courses.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {
	
	
	private final SessionRepository sessionRepository;
	private final OfferingRepository offeringRepository;

	public List<Session> getAllSessionsByOffering(Long offeringId) {
	    return sessionRepository.findByOfferingIdOrderByStartTimeAsc(offeringId);
	}

	public List<Session> getUpcomingSessionsByOffering(Long offeringId) {
	    return sessionRepository.findUpcomingSessions(offeringId, Instant.now());
	}
	
	@Transactional
	public void addSingleSessionToOffering(Long offeringId, AddSessionDto dto) {
	    

	    Offering offering = offeringRepository.findById(offeringId)
	            .orElseThrow(() -> new IllegalArgumentException("Offering not found with ID: " + offeringId));

	    String tzStr = (dto.getTimezone() != null && !dto.getTimezone().isBlank()) 
	            ? dto.getTimezone() : "Asia/Kolkata";
	    ZoneId teacherZone = ZoneId.of(tzStr);
	    
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	    
	    Instant utcStart;
	    Instant utcEnd;
	    
	    try {
	        String combinedStartStr = dto.getDate() + "T" + dto.getStartTime();
	        String combinedEndStr = dto.getDate() + "T" + dto.getEndTime();

	        LocalDateTime localStart = LocalDateTime.parse(combinedStartStr, timeFormatter);
	        LocalDateTime localEnd = LocalDateTime.parse(combinedEndStr, timeFormatter);

	        utcStart = localStart.atZone(teacherZone).toInstant();
	        utcEnd = localEnd.atZone(teacherZone).toInstant();
	        
	    } catch (DateTimeParseException e) {
	        throw new IllegalArgumentException("Invalid date or time formatting provided in payload.", e);
	    }

	    if (!utcStart.isBefore(utcEnd)) {
	        throw new IllegalArgumentException("Session start time must be chronologically before the end time.");
	    }

	 
	    boolean isOverlapping = sessionRepository.existsOverlappingSession(offeringId, utcStart, utcEnd);
	    if (isOverlapping) {
	        throw new IllegalStateException("Schedule Conflict: Another session is already scheduled within this time window.");
	    }

	    
	    Session session = new Session();
	    
	    session.setStartTime(utcStart);
	    session.setEndTime(utcEnd);
	    session.setOffering(offering);
	    session.setTeacherId(offering.getTeacherId()); 
	    session.setSessionDate(dto.getDate());         

	    sessionRepository.save(session);
	}
}
