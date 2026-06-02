package com.example.live_learning.courses.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;


import com.example.live_learning.courses.entity.Course;
import com.example.live_learning.courses.repository.CourseRepository;
import com.example.live_learning.courses.service.CourseServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService implements CourseServiceInterface {
	
	
	private final CourseRepository courseRepo;

	@Override
	public Course createCourse(Course course) {
		
		Course newCourse = courseRepo.save(course);
		return newCourse;
	}
	
	@Override
	public List<Course> getAllCourses(){
		
		List<Course> list = courseRepo.findAll();
		return list;
	}
	

	
	
	
}
 