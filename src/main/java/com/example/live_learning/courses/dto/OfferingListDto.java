package com.example.live_learning.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferingListDto {
	
	private Long id;
	private String title;
	private String teacherId;
	private String courseId;	
	private String startTime;
	private String endTime;
	
}
