package com.example.live_learning.courses.mapper;

import org.mapstruct.Mapper;

import com.example.live_learning.courses.dto.CourseListDto;
import com.example.live_learning.courses.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseListMapper {

	CourseListDto mapToCourseListDto(Course course);
}
