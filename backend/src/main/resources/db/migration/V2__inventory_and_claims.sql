CREATE SEQUENCE found_item_tracking_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE found_item (
    id UUID PRIMARY KEY,
    tracking_code VARCHAR(32) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    category VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    photo_object_key VARCHAR(512),
    zone_name VARCHAR(128) NOT NULL,
    storage_location VARCHAR(128),
    found_by VARCHAR(128) NOT NULL,
    found_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_found_item_status ON found_item (status);
CREATE INDEX idx_found_item_found_at ON found_item (found_at DESC);

CREATE TABLE loss_report (
    id UUID PRIMARY KEY,
    guest_name VARCHAR(128) NOT NULL,
    room_number VARCHAR(32) NOT NULL,
    contact VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    photo_object_key VARCHAR(512),
    zone_name VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL,
    filed_by VARCHAR(128) NOT NULL,
    filed_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_loss_report_status ON loss_report (status);
CREATE INDEX idx_loss_report_filed_at ON loss_report (filed_at DESC);
