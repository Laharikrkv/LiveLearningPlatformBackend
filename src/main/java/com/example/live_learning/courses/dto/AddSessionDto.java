package com.example.live_learning.courses.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class AddSessionDto {
    
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String date;
	
    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private String startTime; 
    
    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private String endTime;   
    
    private String timezone;         
}