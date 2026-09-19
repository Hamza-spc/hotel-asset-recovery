package com.hotel.lostfound.audit;

import java.util.List;

public interface AuditLog {

    AuditEntry append(AuditEntry entry);

    List<AuditEntry> recent();
}
