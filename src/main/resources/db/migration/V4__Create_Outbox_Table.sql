CREATE TABLE outbox_events
(
    id             SERIAL PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_is   VARCHAR(255) NOT NULL,
    event_type     VARCHAR(255) NOT NULL,
    payload        TEXT         NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    processed_at   TIMESTAMP
);

CREATE INDEX idx_outbox_processed_created ON outbox_events (processed_at, created_at);