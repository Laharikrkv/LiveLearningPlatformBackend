package com.example.live_learning.courses.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookingDto {

    private Long bookingId;

    @NotNull(message = "Parent ID cannot be null")
    private Long parentId;

    @NotNull(message = "Offering ID cannot be null")
    private Long offeringId;

    private String bookingStatus;

    @NotBlank(message = "Booking date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Booking date must match yyyy-MM-dd format")
    private String localizedBookingDate;

}

