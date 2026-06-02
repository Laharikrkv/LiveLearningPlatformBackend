package com.example.live_learning;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.live_learning.common.exceptions.ResourceNotFoundException;
import com.example.live_learning.courses.dto.OfferingDto;

import com.example.live_learning.courses.entity.Course;
import com.example.live_learning.courses.entity.Offering;
import com.example.live_learning.courses.entity.Session;
import com.example.live_learning.courses.repository.CourseRepository;
import com.example.live_learning.courses.repository.OfferingRepository;

import com.example.live_learning.courses.service.impl.OfferingService;
import com.example.live_learning.users.UserDto;
import com.example.live_learning.users.UserQueryService;

@ExtendWith(MockitoExtension.class)
class OfferingServiceTest {

    @Mock
    private OfferingRepository offeringRepo;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private OfferingService offeringService;

    private Course course;
    private UserDto teacherDto;
    private OfferingDto offeringDto;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);

        teacherDto = new UserDto();
        teacherDto.setId(2L);
        teacherDto.setTimezone("Asia/Kolkata");

        offeringDto = new OfferingDto();
        offeringDto.setCourseId(1L);
        offeringDto.setTeacherId(2L);
        offeringDto.setTitle("Java Backend Basics");
        offeringDto.setMaxCapacity(15);
        offeringDto.setStartTime(LocalTime.of(10, 0));
        offeringDto.setEndTime(LocalTime.of(11, 30));
        offeringDto.setStartDate(LocalDate.of(2026, 6, 1));
        offeringDto.setEndDate(LocalDate.of(2026, 6, 5));
        offeringDto.setDaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
    }

    @Test
    @DisplayName("1. Should successfully generate sessions and compute metadata boundaries in UTC")
    void createOfferingWithCompactSchedule_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userQueryService.getUserById(2L)).thenReturn(teacherDto);
        when(offeringRepo.hasTeacherConflict(eq(2L), any(Instant.class), any(Instant.class))).thenReturn(false);
        when(offeringRepo.save(any(Offering.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Offering result = offeringService.createOfferingWithCompactSchedule(offeringDto);

        assertNotNull(result);
        assertEquals("Java Backend Basics", result.getTitle());
        assertEquals(2, result.getSessions().size());

        Session firstSession = result.getSessions().get(0);
        assertEquals(Instant.parse("2026-06-01T04:30:00Z"), firstSession.getStartTime());
        assertEquals(Instant.parse("2026-06-01T06:00:00Z"), firstSession.getEndTime());
        assertEquals("2026-06-01", firstSession.getSessionDate());

        assertEquals(Instant.parse("2026-06-01T04:30:00Z"), result.getStartTime());
        assertEquals(Instant.parse("2026-06-03T06:00:00Z"), result.getEndTime());

        verify(offeringRepo, times(1)).save(any(Offering.class));
    }

    @Test
    @DisplayName("2. Should throw IllegalArgumentException when time parameters are chronologically reversed")
    void createOffering_ThrowsException_WhenEndTimeIsBeforeStartTime() {
        offeringDto.setStartTime(LocalTime.of(16, 0));
        offeringDto.setEndTime(LocalTime.of(15, 0));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userQueryService.getUserById(2L)).thenReturn(teacherDto);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offeringService.createOfferingWithCompactSchedule(offeringDto);
        });

        assertEquals("End time must be after start time.", exception.getMessage());
        verify(offeringRepo, never()).save(any());
    }

    @Test
    @DisplayName("3. Should throw IllegalArgumentException when schedule overlaps an existing teacher assignment")
    void createOffering_ThrowsException_WhenTeacherHasScheduleConflict() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userQueryService.getUserById(2L)).thenReturn(teacherDto);
        when(offeringRepo.hasTeacherConflict(eq(2L), any(Instant.class), any(Instant.class))).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offeringService.createOfferingWithCompactSchedule(offeringDto);
        });

        assertEquals("The teacher is already assigned to a session during this timeframe!", exception.getMessage());
        verify(offeringRepo, never()).save(any());
    }

    @Test
    @DisplayName("4. Should throw ResourceNotFoundException when fetching target upcoming teacher courses returns empty")
    void getUpcomingOfferingsByTeacher_ThrowsException_WhenEmpty() {
        when(offeringRepo.findUpcomingOfferingsByTeacher(eq(2L), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            offeringService.getUpcomingOfferingsByTeacher(2L);
        });

        assertEquals("No offerings found", exception.getMessage());
    }

    @Test
    @DisplayName("5. Should throw ResourceNotFoundException when query parameters match zero course records")
    void getOfferingByCourseId_ThrowsException_WhenCourseOfferingsMissing() {
        Long invalidCourseId = 999L;
        when(offeringRepo.findByCourseId(invalidCourseId)).thenReturn(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            offeringService.getOfferingByCourseId(invalidCourseId);
        });

        assertEquals("No offerings found for course ID: " + invalidCourseId, exception.getMessage());
    }
}
