package com.hotel.lostfound.matching.web;

import com.hotel.lostfound.matching.application.MatchingService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
class MatchController {

    private final MatchingService matching;

    MatchController(MatchingService matching) {
        this.matching = matching;
    }

    @GetMapping
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    List<MatchResponse> pending() {
        return matching.pending().stream().map(MatchResponse::from).toList();
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    MatchResponse accept(@PathVariable UUID id) {
        return MatchResponse.from(matching.accept(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('DUTY_MANAGER')")
    MatchResponse reject(@PathVariable UUID id) {
        return MatchResponse.from(matching.reject(id));
    }
}
