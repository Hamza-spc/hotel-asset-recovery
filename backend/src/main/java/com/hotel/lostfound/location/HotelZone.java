package com.hotel.lostfound.location;

import java.util.UUID;

public record HotelZone(UUID id, String name, String floorCode, String geometryJson) {}
