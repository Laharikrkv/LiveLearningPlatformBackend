package com.example.live_learning.courses.dto;

import java.util.ArrayList;
import java.util.List;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {

	
	@Size(min = 5, max = 30, message = "Course name must be between 5 and 30 characters")
	@NotEmpty(message = "Course name cannot be blank")
	private String name;
	
	@Size(min = 3, message = "Category must be at least 3 characters")
	@NotEmpty(message = "Category cannot be blank")
	private String category;
	
	@NotEmpty(message = "Must specify at least one skill")
	private List<String> skills = new ArrayList<>();
}
