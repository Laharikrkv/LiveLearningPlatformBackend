package com.example.live_learning.courses.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.live_learning.courses.entity.Offering;

import jakarta.persistence.LockModeType;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
	
	@Query("SELECT COUNT(s.id) > 0 FROM Offering o " +
	           "JOIN o.sessions s " +
	           "WHERE o.teacherId = :teacherId " +
	           "AND s.startTime < :newEndTime " +
	           "AND s.endTime > :newStartTime")
	    boolean hasTeacherConflict(@Param("teacherId") Long teacherId, 
	                               @Param("newStartTime") Instant newStartTime, 
	                               @Param("newEndTime") Instant newEndTime);

	List<Offering> findByTeacherId(Long teacherId);

	List<Offering> findByCourseId(Long courseId);
	
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offering o WHERE o.id = :id")
    Optional<Offering> findById(@Param("id") Long id);
	
	
	@Query("SELECT o FROM Offering o WHERE o.teacherId = :teacherId AND o.endTime > :nowTime ORDER BY o.startTime ASC")
	List<Offering> findUpcomingOfferingsByTeacher(@Param("teacherId") Long teacherId, @Param("nowTime") Instant nowTime);
}
