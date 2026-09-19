package com.hotel.lostfound.claims.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record GuestReportRequest(
        @NotBlank @Size(max = 80) String guestName,
        @NotBlank @Size(max = 16) String roomNumber,
        @NotBlank @Size(max = 120) String contact,
        @NotBlank @Size(max = 500) String description,
        @NotBlank @Size(max = 64) String zoneName,
        Double mapX,
        Double mapY) {}
