package com.hotel.lostfound.location.persistence;

import com.hotel.lostfound.location.HotelZone;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcZoneRepository {

    private static final RowMapper<HotelZone> ZONE_MAPPER = (rs, rowNum) -> new HotelZone(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getString("floor_code"),
            rs.getString("geojson"));

    private final JdbcTemplate jdbc;

    JdbcZoneRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<HotelZone> findAll() {
        return jdbc.query(
                """
                SELECT id, name, floor_code, ST_AsGeoJSON(geom) AS geojson
                FROM hotel_zone
                ORDER BY ST_Area(geom) DESC
                """,
                ZONE_MAPPER);
    }

    public Optional<HotelZone> resolve(double x, double y) {
        List<HotelZone> matches = jdbc.query(
                """
                SELECT id, name, floor_code, ST_AsGeoJSON(geom) AS geojson
                FROM hotel_zone
                WHERE ST_Intersects(geom, ST_SetSRID(ST_MakePoint(?, ?), 0))
                ORDER BY ST_Area(geom) ASC
                LIMIT 1
                """,
                ZONE_MAPPER,
                x,
                y);
        return matches.stream().findFirst();
    }
}
