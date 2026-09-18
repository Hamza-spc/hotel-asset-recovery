package com.hotel.lostfound.identity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum StaffRole {
    HOUSEKEEPING,
    FRONT_DESK,
    DUTY_MANAGER;

    public static final String HOUSEKEEPING_ROLE = "HOUSEKEEPING";
    public static final String FRONT_DESK_ROLE = "FRONT_DESK";
    public static final String DUTY_MANAGER_ROLE = "DUTY_MANAGER";

    public static Set<String> names() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.toUnmodifiableSet());
    }
}
