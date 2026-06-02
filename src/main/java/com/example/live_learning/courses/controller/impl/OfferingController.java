package com.example.live_learning.courses.controller.impl;

import com.example.live_learning.courses.dto.OfferingDto;
import com.example.live_learning.courses.dto.OfferingListDto;
import com.example.live_learning.courses.entity.Offering;
import com.example.live_learning.courses.mapper.OfferingListMapper;
import com.example.live_learning.courses.service.OfferingServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OfferingController {
    
    private final OfferingServiceInterface offeringService;
    private final OfferingListMapper offeringListMapper;
    
  
    private static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    // TEACHER APIs
    
    @PostMapping("/offerings/create")
    public ResponseEntity<String> createOffering(@Valid @RequestBody OfferingDto offeringDto){ 
        Offering createdOffering = offeringService.createOfferingWithCompactSchedule(offeringDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Offering created successfully with ID: " + createdOffering.getId());
    }
    
    @GetMapping("/offerings/teacher/{teacherId}/upcoming")
    public ResponseEntity<List<OfferingListDto>> getUpcomingSchedulesByTeacher(
            @PathVariable Long teacherId,
            @RequestHeader(value = "X-Timezone", required = false) String displayTimeZone) {
            
        String finalTimeZone = (displayTimeZone != null && !displayTimeZone.isBlank()) 
                ? displayTimeZone : DEFAULT_TIMEZONE;
            
        List<Offering> offerings = offeringService.getUpcomingOfferingsByTeacher(teacherId);
        List<OfferingListDto> responseDtos = offeringListMapper.toDtoList(offerings, finalTimeZone);
        return ResponseEntity.ok(responseDtos);
    }
    
    // =========================================================================
    // PARENT APIs
    // =========================================================================          
    
    @GetMapping("/offerings/course/{courseId}")
    public ResponseEntity<List<OfferingListDto>> getOfferingByCourseId(
            @PathVariable Long courseId,
            @RequestHeader(value = "X-Timezone", required = false) String parentTimeZone) {
            
        String finalTimeZone = (parentTimeZone != null && !parentTimeZone.isBlank()) 
                ? parentTimeZone : DEFAULT_TIMEZONE;
            
        List<Offering> offerings = offeringService.getOfferingByCourseId(courseId);
        List<OfferingListDto> offeringDtos = offeringListMapper.toDtoList(offerings, finalTimeZone);
        return ResponseEntity.ok(offeringDtos);
    }
    
    @GetMapping("/offerings/all")
    public ResponseEntity<List<OfferingListDto>> getAllOfferings(
            @RequestHeader(value = "X-Timezone", required = false) String parentTimeZone) {
            
        String finalTimeZone = (parentTimeZone != null && !parentTimeZone.isBlank()) 
                ? parentTimeZone : DEFAULT_TIMEZONE;
            
        List<Offering> offerings = offeringService.getAllOfferings();
        List<OfferingListDto> offeringDtos = offeringListMapper.toDtoList(offerings, finalTimeZone);
        return ResponseEntity.ok(offeringDtos);
    }
    

}