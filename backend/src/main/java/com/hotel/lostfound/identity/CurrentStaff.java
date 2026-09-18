package com.hotel.lostfound.identity;

import java.util.Set;

public record CurrentStaff(String subject, String username, Set<String> roles) {

    public boolean hasRole(StaffRole role) {
        return roles.contains(role.name());
    }
}
