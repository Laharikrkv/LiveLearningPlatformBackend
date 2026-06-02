package com.example.live_learning.bookings.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.live_learning.bookings.entity.Booking;
import com.example.live_learning.bookings.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByParentIdAndStatus(Long parentId, BookingStatus status);
	
	@Query("SELECT COUNT(b.id) > 0 FROM Booking b " +
	           "JOIN Offering o ON b.offeringId = o.id " +
	           "JOIN o.sessions s " +
	           "WHERE b.parentId = :parentId " +
	           "AND b.status = 'CONFIRMED' " +
	           "AND s.startTime < :newEndTime " +
	           "AND s.endTime > :newStartTime")
	    boolean hasParentOverlapConflict(@Param("parentId") Long parentId, 
	                                     @Param("newStartTime") Instant newStartTime, 
	                                     @Param("newEndTime") Instant newEndTime);
		
	//write derived method to retrive booking when both parent id and offering id are same
	Booking findByParentIdAndOfferingId(Long parentId, Long offeringId);
	
}
