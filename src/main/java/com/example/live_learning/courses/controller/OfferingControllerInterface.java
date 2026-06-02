package com.example.live_learning.courses.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.live_learning.courses.dto.OfferingDto;
import com.example.live_learning.courses.dto.OfferingListDto;


public interface OfferingControllerInterface {

	ResponseEntity<String> createOffering(OfferingDto offering);
	
	ResponseEntity<List<OfferingListDto>> getAllOfferings(String parentTimeZone);

	ResponseEntity<List<OfferingListDto>> getOfferingByCourseId(Long courseId, String parentTimeZone);
	
	ResponseEntity<List<OfferingListDto>> getUpcomingSchedulesByTeacher(Long teacherId, String displayTimeZone);

	
}
