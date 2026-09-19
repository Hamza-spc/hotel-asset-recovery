package com.hotel.lostfound.audit.web;

import com.hotel.lostfound.audit.application.AuditService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
class AuditController {

    private final AuditService audit;

    AuditController(AuditService audit) {
        this.audit = audit;
    }

    @GetMapping
    List<AuditEntryResponse> recent() {
        return audit.recent().stream().map(AuditEntryResponse::from).toList();
    }
}
