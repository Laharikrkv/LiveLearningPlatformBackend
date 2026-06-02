package com.example.live_learning.courses.service;

import java.util.List;

import com.example.live_learning.courses.entity.Course;

public interface CourseServiceInterface {

	Course createCourse(Course course);
	List<Course> getAllCourses();

	
	
}
