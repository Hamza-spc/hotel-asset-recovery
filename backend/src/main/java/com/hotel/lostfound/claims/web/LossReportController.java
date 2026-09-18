package com.hotel.lostfound.claims.web;

import com.hotel.lostfound.claims.application.ClaimsService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loss-reports")
class LossReportController {

    private final ClaimsService claims;

    LossReportController(ClaimsService claims) {
        this.claims = claims;
    }

    @GetMapping
    List<LossReportResponse> list() {
        return claims.list().stream().map(LossReportResponse::from).toList();
    }

    @GetMapping("/{id}")
    LossReportResponse get(@PathVariable UUID id) {
        return LossReportResponse.from(claims.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('FRONT_DESK')")
    LossReportResponse file(
            @RequestParam @NotBlank String guestName,
            @RequestParam @NotBlank String roomNumber,
            @RequestParam @NotBlank String contact,
            @RequestParam @NotBlank String description,
            @RequestParam @NotBlank String zoneName,
            @AuthenticationPrincipal Jwt jwt) {
        return LossReportResponse.from(
                claims.file(guestName, roomNumber, contact, description, zoneName, username(jwt)));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    LossReportResponse close(@PathVariable UUID id) {
        return LossReportResponse.from(claims.close(id));
    }

    private static String username(Jwt jwt) {
        String preferred = jwt.getClaimAsString("preferred_username");
        return preferred == null || preferred.isBlank() ? jwt.getSubject() : preferred;
    }
}
