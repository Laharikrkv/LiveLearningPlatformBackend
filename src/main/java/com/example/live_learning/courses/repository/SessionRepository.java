package com.example.live_learning.courses.repository;



import com.example.live_learning.courses.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;



@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByOfferingIdOrderByStartTimeAsc(Long offeringId);

    @Query("SELECT s FROM Session s WHERE s.offering.id = :offeringId AND s.startTime >= :now ORDER BY s.startTime ASC")
    List<Session> findUpcomingSessions(@Param("offeringId") Long offeringId, @Param("now") Instant now);

   
    @Query("SELECT COUNT(s) > 0 FROM Session s WHERE s.offering.id = :offeringId " +
           "AND (:newStart < s.endTime AND :newEnd > s.startTime)")
    boolean existsOverlappingSession(
            @Param("offeringId") Long offeringId,
            @Param("newStart") Instant newStart,
            @Param("newEnd") Instant newEnd
    );
}