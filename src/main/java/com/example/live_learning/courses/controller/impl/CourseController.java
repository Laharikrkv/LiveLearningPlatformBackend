package com.example.live_learning.courses.controller.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.live_learning.courses.mapper.CourseMapper;
import  com.example.live_learning.courses.controller.CourseControllerInterface;
import com.example.live_learning.courses.dto.CourseDto;
import com.example.live_learning.courses.dto.CourseListDto;
import com.example.live_learning.courses.entity.Course;
import com.example.live_learning.courses.mapper.CourseListMapper;
import com.example.live_learning.courses.service.CourseServiceInterface;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController implements CourseControllerInterface {

	private final  CourseMapper courseMapper;
	private final CourseListMapper courseListMapper;
	private final CourseServiceInterface courseService;
	
	@PostMapping("/create")
	@Override
	public ResponseEntity<String> createCourse(@Valid @RequestBody CourseDto courseDto) {
		
		Course newCourse = courseMapper.mapToCourse(courseDto);
		Course saved = courseService.createCourse(newCourse);
		
		return new ResponseEntity<String>("Course created successfully with id: " + saved.getId(), HttpStatus.CREATED);
	}
	
    @GetMapping("/all")
    @Override
	public ResponseEntity<List<CourseListDto>> getAllCourses() {
		List<Course> courses = courseService.getAllCourses();
		List<CourseListDto> list = courses.stream().map(courseListMapper::mapToCourseListDto).collect(Collectors.toList());
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
       
}
