package com.example.live_learning.courses.service;

import java.util.List;

import com.example.live_learning.courses.dto.AddSessionDto;
import com.example.live_learning.courses.dto.OfferingDto;
import com.example.live_learning.courses.entity.Offering;

public interface OfferingServiceInterface {
	

	Offering createOfferingWithCompactSchedule(OfferingDto dto);

	List<Offering> getOfferingByCourseId(Long courseId);

	List<Offering> getAllOfferings();
	
	List<Offering> getUpcomingOfferingsByTeacher(Long teacherId);
	

}
