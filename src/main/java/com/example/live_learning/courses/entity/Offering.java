package com.example.live_learning.courses.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Entity
@Table(name = "offerings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;
    
    @Column(name = "current_bookings_count", nullable = false)
    private Integer currentBookingsCount = 0; // Defaulting to zero prevents NullPointerExceptions
    
    @OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();
    
    @Column(name = "start_time", nullable = false)
    private Instant startTime;       
    
    @Column(name = "end_time", nullable = false)
    private Instant endTime;         
    
    @Column(name = "is_enrollment_open", nullable = false)
    private Boolean isEnrollmentOpen = true;

    // Helper method for clean Service-layer loop execution
    public void addSession(Session session) {
        this.sessions.add(session);
        session.setOffering(this); 
    }
}



	

