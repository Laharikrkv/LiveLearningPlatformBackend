package com.example.live_learning.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.live_learning.courses.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {


}
