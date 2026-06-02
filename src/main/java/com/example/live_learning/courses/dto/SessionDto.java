

package com.example.live_learning.courses.dto;

import lombok.Data;

@Data
public class SessionDto {
	
    private Long id;  
    private String sessionDate; 
    private String startTime; 
    private String endTime;   
}
