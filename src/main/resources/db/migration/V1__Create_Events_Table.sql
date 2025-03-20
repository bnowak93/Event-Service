CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    star_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    otganizer_id BIGINT
);

CREATE INDEX idx_events_start_time on events(start_time);
CREATE INDEX idx_events_location on events(location);
CREATE INDEX idx_events_organizer_id on events(organizer_id);