package com.example.live_learning.courses.mapper;

import org.mapstruct.Mapper;

import com.example.live_learning.courses.dto.CourseDto;
import com.example.live_learning.courses.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {
	
	Course mapToCourse(CourseDto courseDto);
	
}
