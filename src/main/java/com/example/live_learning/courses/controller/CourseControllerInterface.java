package com.example.live_learning.courses.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.live_learning.courses.dto.CourseDto;
import com.example.live_learning.courses.dto.CourseListDto;
import com.example.live_learning.courses.entity.Course;

public interface CourseControllerInterface {
	
	ResponseEntity<List<CourseListDto>> getAllCourses();
	
	ResponseEntity<String> createCourse(CourseDto course);
	
}
