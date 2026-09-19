package com.hotel.lostfound.location.application;

import com.hotel.lostfound.location.HotelZone;
import com.hotel.lostfound.location.UnknownZoneException;
import com.hotel.lostfound.location.persistence.JdbcZoneRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MapService {

    private final JdbcZoneRepository zones;

    MapService(JdbcZoneRepository zones) {
        this.zones = zones;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> zonesGeoJson() {
        List<Map<String, Object>> features = new ArrayList<>();
        for (HotelZone zone : zones.findAll()) {
            Map<String, Object> feature = new LinkedHashMap<>();
            feature.put("type", "Feature");
            feature.put("id", zone.id().toString());
            feature.put("geometry", JsonParserFactory.getJsonParser().parseMap(zone.geometryJson()));
            feature.put(
                    "properties",
                    Map.of("id", zone.id().toString(), "name", zone.name(), "floorCode", zone.floorCode()));
            features.add(feature);
        }
        Map<String, Object> collection = new LinkedHashMap<>();
        collection.put("type", "FeatureCollection");
        collection.put("features", features);
        return collection;
    }

    @Transactional(readOnly = true)
    public HotelZone resolve(double x, double y) {
        return zones.resolve(x, y).orElseThrow(() -> new UnknownZoneException(x, y));
    }
}
