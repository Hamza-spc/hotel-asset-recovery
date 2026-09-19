CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE hotel_zone (
    id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    floor_code VARCHAR(32) NOT NULL,
    geom geometry(Polygon, 0) NOT NULL
);

CREATE INDEX idx_hotel_zone_geom ON hotel_zone USING GIST (geom);

INSERT INTO hotel_zone (id, name, floor_code, geom) VALUES
    ('11111111-1111-1111-1111-111111111101', 'Storage', 'ground',
     ST_GeomFromText('POLYGON((0 0, 15 0, 15 15, 0 15, 0 0))', 0)),
    ('11111111-1111-1111-1111-111111111102', 'Guest wing', 'ground',
     ST_GeomFromText('POLYGON((0 0, 70 0, 70 40, 0 40, 0 0))', 0)),
    ('11111111-1111-1111-1111-111111111103', 'Lobby', 'ground',
     ST_GeomFromText('POLYGON((0 40, 40 40, 40 70, 0 70, 0 40))', 0)),
    ('11111111-1111-1111-1111-111111111104', 'Restaurant', 'ground',
     ST_GeomFromText('POLYGON((40 40, 70 40, 70 70, 40 70, 40 40))', 0)),
    ('11111111-1111-1111-1111-111111111105', 'Gym', 'ground',
     ST_GeomFromText('POLYGON((70 0, 100 0, 100 35, 70 35, 70 0))', 0)),
    ('11111111-1111-1111-1111-111111111106', 'Pool', 'ground',
     ST_GeomFromText('POLYGON((70 35, 100 35, 100 70, 70 70, 70 35))', 0));

ALTER TABLE found_item ADD COLUMN map_x DOUBLE PRECISION;
ALTER TABLE found_item ADD COLUMN map_y DOUBLE PRECISION;
ALTER TABLE found_item ADD COLUMN location geometry(Point, 0)
    GENERATED ALWAYS AS (
        CASE
            WHEN map_x IS NULL OR map_y IS NULL THEN NULL
            ELSE ST_SetSRID(ST_MakePoint(map_x, map_y), 0)
        END
    ) STORED;
CREATE INDEX idx_found_item_location ON found_item USING GIST (location);

ALTER TABLE loss_report ADD COLUMN map_x DOUBLE PRECISION;
ALTER TABLE loss_report ADD COLUMN map_y DOUBLE PRECISION;
ALTER TABLE loss_report ADD COLUMN location geometry(Point, 0)
    GENERATED ALWAYS AS (
        CASE
            WHEN map_x IS NULL OR map_y IS NULL THEN NULL
            ELSE ST_SetSRID(ST_MakePoint(map_x, map_y), 0)
        END
    ) STORED;
CREATE INDEX idx_loss_report_location ON loss_report USING GIST (location);
