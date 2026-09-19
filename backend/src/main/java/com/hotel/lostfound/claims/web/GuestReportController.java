package com.hotel.lostfound.claims.web;

import com.hotel.lostfound.claims.MapLocation;
import com.hotel.lostfound.claims.application.ClaimsService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/loss-reports")
class GuestReportController {

    private final ClaimsService claims;

    GuestReportController(ClaimsService claims) {
        this.claims = claims;
    }

    @PostMapping
    GuestReportResponse file(@Valid @RequestBody GuestReportRequest request) {
        return GuestReportResponse.from(claims.file(
                request.guestName(),
                request.roomNumber(),
                request.contact(),
                request.description(),
                request.zoneName(),
                request.mapX() == null || request.mapY() == null
                        ? null
                        : new MapLocation(request.mapX(), request.mapY()),
                "guest"));
    }

    @GetMapping("/{id}")
    GuestReportResponse status(@PathVariable UUID id) {
        return GuestReportResponse.from(claims.get(id));
    }
}
