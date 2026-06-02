package com.example.live_learning.bookings.controller;

import com.example.live_learning.bookings.entity.Booking;
import com.example.live_learning.bookings.mapper.BookingMapper;
import com.example.live_learning.bookings.service.BookingService;
import com.example.live_learning.courses.dto.BookingDto;
import com.example.live_learning.users.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Long> createBooking(@Valid @RequestBody BookingDto bookingDto) {
        Booking booking = bookingService.bookOffering(bookingDto.getParentId(), bookingDto.getOfferingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(booking.getId());
    }
    
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        String parentTimezone = getTimezoneByUserId(booking.getParentId());
        BookingDto bookingDto = bookingMapper.toDto(booking, parentTimezone);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<BookingDto>> getBookingsByParent(@PathVariable Long parentId) {
        String parentTimezone = getTimezoneByUserId(parentId);
        List<Booking> bookings = bookingService.getBookingsByParent(parentId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(booking -> bookingMapper.toDto(booking, parentTimezone))
                .toList();
        return ResponseEntity.ok(bookingDtos);
    }

    private String getTimezoneByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getTimezone())
                .filter(tz -> tz != null && !tz.isBlank())
                .orElse("Asia/Kolkata");
    }
}
