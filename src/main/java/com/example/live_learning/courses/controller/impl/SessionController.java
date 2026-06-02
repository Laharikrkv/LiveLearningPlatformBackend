package com.example.live_learning.courses.controller.impl;



import com.example.live_learning.courses.dto.AddSessionDto;
import com.example.live_learning.courses.dto.SessionDto;
import com.example.live_learning.courses.entity.Session;
import com.example.live_learning.courses.mapper.SessionMapper;
import com.example.live_learning.courses.service.impl.OfferingService;
import com.example.live_learning.courses.service.impl.SessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offerings/{offeringId}/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    private static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    @PostMapping("/add")
    public ResponseEntity<String> addSessionToOffering(
            @PathVariable("offeringId") Long offeringId, 
            @Valid @RequestBody AddSessionDto addSessionDto) {
            
        sessionService.addSingleSessionToOffering(offeringId, addSessionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("New session is successfully verified and integrated into the offering timeline.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<SessionDto>> getAllSessions(
            @PathVariable("offeringId") Long offeringId,
            @RequestHeader(value = "X-Timezone", required = false) String displayTimeZone) {
            
        String finalTimeZone = (displayTimeZone != null && !displayTimeZone.isBlank()) 
                ? displayTimeZone : DEFAULT_TIMEZONE;
                
        List<Session> sessions = sessionService.getAllSessionsByOffering(offeringId);
        List<SessionDto> dtos = sessionMapper.toDtoList(sessions, finalTimeZone);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<SessionDto>> getUpcomingSessions(
            @PathVariable("offeringId") Long offeringId,
            @RequestHeader(value = "X-Timezone", required = false) String displayTimeZone) {
            
        String finalTimeZone = (displayTimeZone != null && !displayTimeZone.isBlank()) 
                ? displayTimeZone : DEFAULT_TIMEZONE;
                
        List<Session> sessions = sessionService.getUpcomingSessionsByOffering(offeringId);
        List<SessionDto> dtos = sessionMapper.toDtoList(sessions, finalTimeZone);
        return ResponseEntity.ok(dtos);
    }
}
