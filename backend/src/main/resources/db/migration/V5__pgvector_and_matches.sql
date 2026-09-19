CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE item_embedding (
    item_id UUID PRIMARY KEY,
    embedding vector(768) NOT NULL,
    description TEXT NOT NULL,
    map_x DOUBLE PRECISION,
    map_y DOUBLE PRECISION,
    embedded_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_item_embedding_hnsw ON item_embedding USING hnsw (embedding vector_cosine_ops);

CREATE TABLE report_embedding (
    report_id UUID PRIMARY KEY,
    embedding vector(768) NOT NULL,
    description TEXT NOT NULL,
    map_x DOUBLE PRECISION,
    map_y DOUBLE PRECISION,
    embedded_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_report_embedding_hnsw ON report_embedding USING hnsw (embedding vector_cosine_ops);

CREATE TABLE match_suggestion (
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL,
    report_id UUID NOT NULL,
    item_label VARCHAR(160) NOT NULL,
    report_label VARCHAR(160) NOT NULL,
    text_score DOUBLE PRECISION NOT NULL,
    spatial_score DOUBLE PRECISION NOT NULL,
    combined_score DOUBLE PRECISION NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    UNIQUE (item_id, report_id)
);

CREATE INDEX idx_match_suggestion_status ON match_suggestion (status, combined_score DESC);
