package com.example.live_learning.bookings.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.live_learning.bookings.entity.Booking;
import com.example.live_learning.bookings.entity.BookingStatus;
import com.example.live_learning.bookings.repository.BookingRepository;
import com.example.live_learning.common.exceptions.ResourceNotFoundException;
import com.example.live_learning.courses.entity.Offering;
import com.example.live_learning.courses.entity.Session;
import com.example.live_learning.courses.repository.OfferingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OfferingRepository offeringRepository; 

    @Transactional
    public Booking bookOffering(Long parentId, Long offeringId) {
        
		if (bookingRepository.findByParentIdAndOfferingId(parentId, offeringId) != null) {
			throw new IllegalStateException("You have already booked this class batch.");
		}
    	
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found with ID: " + offeringId));

        if (offering.getCurrentBookingsCount() >= offering.getMaxCapacity()) {
            throw new IllegalStateException("Registration failed: This class batch is already completely full.");
        }

        for (Session session : offering.getSessions()) {
            boolean isOverlapping = bookingRepository.hasParentOverlapConflict(
                    parentId, 
                    session.getStartTime(), 
                    session.getEndTime()
            );
            
            if (isOverlapping) {
                throw new IllegalArgumentException("Schedule Conflict: One or more sessions in this batch overlap with your student's existing calendar.");
            }
        }

        Booking booking = new Booking();
        booking.setParentId(parentId);
        booking.setOfferingId(offeringId);
        booking.setStatus(BookingStatus.CONFIRMED);
   
        offering.setCurrentBookingsCount(offering.getCurrentBookingsCount() + 1);
        offeringRepository.save(offering);

        return bookingRepository.save(booking);
    }

    
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByParent(Long parentId) {
        return bookingRepository.findByParentIdAndStatus(parentId, BookingStatus.CONFIRMED);
    }

	public Booking getBookingById(Long bookingId) {
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
	}

	
}
