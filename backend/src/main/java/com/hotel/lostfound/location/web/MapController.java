package com.hotel.lostfound.location.web;

import com.hotel.lostfound.location.HotelZone;
import com.hotel.lostfound.location.application.MapService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/map")
class MapController {

    private final MapService maps;

    MapController(MapService maps) {
        this.maps = maps;
    }

    @GetMapping("/zones")
    Map<String, Object> zones() {
        return maps.zonesGeoJson();
    }

    @GetMapping("/resolve")
    ZoneResolved resolve(@RequestParam double x, @RequestParam double y) {
        HotelZone zone = maps.resolve(x, y);
        return new ZoneResolved(zone.name(), zone.floorCode());
    }
}
