package com.example.live_learning;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.live_learning.bookings.entity.Booking;
import com.example.live_learning.bookings.entity.BookingStatus;
import com.example.live_learning.bookings.repository.BookingRepository;
import com.example.live_learning.bookings.service.BookingService;
import com.example.live_learning.common.exceptions.ResourceNotFoundException;
import com.example.live_learning.courses.entity.Offering;
import com.example.live_learning.courses.entity.Session;
import com.example.live_learning.courses.repository.OfferingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OfferingRepository offeringRepository;

    @InjectMocks
    private BookingService bookingService;

    private Long parentId;
    private Long offeringId;
    private Offering offering;

    @BeforeEach
    void setUp() {
        parentId = 1L;
        offeringId = 10L;

        offering = new Offering();
        offering.setId(offeringId);
        offering.setCurrentBookingsCount(0);
        offering.setMaxCapacity(5);
        offering.setSessions(new ArrayList<>());
    }

    @Test
    @DisplayName("Should successfully book offering when all business rules pass")
    void bookOffering_Success() {
      
        Session session = new Session();
        session.setStartTime(Instant.parse("2026-06-05T10:00:00Z"));
        session.setEndTime(Instant.parse("2026-06-05T11:00:00Z"));
        offering.getSessions().add(session);

        when(bookingRepository.findByParentIdAndOfferingId(parentId, offeringId)).thenReturn(null);
        when(offeringRepository.findById(offeringId)).thenReturn(Optional.of(offering));
        when(bookingRepository.hasParentOverlapConflict(eq(parentId), any(Instant.class), any(Instant.class))).thenReturn(false);
        
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.bookOffering(parentId, offeringId);

        assertNotNull(result);
        assertEquals(parentId, result.getParentId());
        assertEquals(offeringId, result.getOfferingId());
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        assertEquals(1, offering.getCurrentBookingsCount()); // Verifies offering count increments

        verify(offeringRepository, times(1)).save(offering);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when user has already booked the exact same batch")
    void bookOffering_ThrowsException_WhenAlreadyBooked() {

    	Booking existingBooking = new Booking();
        when(bookingRepository.findByParentIdAndOfferingId(parentId, offeringId)).thenReturn(existingBooking);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.bookOffering(parentId, offeringId);
        });

        assertEquals("You have already booked this class batch.", exception.getMessage());
        verify(offeringRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when target offering batch has reached its max capacity")
    void bookOffering_ThrowsException_WhenCapacityFull() {

    	offering.setCurrentBookingsCount(5);
        offering.setMaxCapacity(5);

        when(bookingRepository.findByParentIdAndOfferingId(parentId, offeringId)).thenReturn(null);
        when(offeringRepository.findById(offeringId)).thenReturn(Optional.of(offering));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.bookOffering(parentId, offeringId);
        });

        assertEquals("Registration failed: This class batch is already completely full.", exception.getMessage());
        verify(bookingRepository, never()).hasParentOverlapConflict(anyLong(), any(), any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when a session overlaps with parent's existing schedule")
    void bookOffering_ThrowsException_WhenScheduleOverlaps() {

    	Session session = new Session();
        session.setStartTime(Instant.parse("2026-06-05T10:00:00Z"));
        session.setEndTime(Instant.parse("2026-06-05T11:00:00Z"));
        offering.getSessions().add(session);

        when(bookingRepository.findByParentIdAndOfferingId(parentId, offeringId)).thenReturn(null);
        when(offeringRepository.findById(offeringId)).thenReturn(Optional.of(offering));
        
        when(bookingRepository.hasParentOverlapConflict(parentId, session.getStartTime(), session.getEndTime()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookOffering(parentId, offeringId);
        });

        assertEquals("Schedule Conflict: One or more sessions in this batch overlap with your student's existing calendar.", exception.getMessage());
        verify(offeringRepository, never()).save(any(Offering.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}