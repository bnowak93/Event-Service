CREATE TABLE public.events (
                               id BIGSERIAL PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               description VARCHAR(255) NOT NULL,
                               location VARCHAR(255) NOT NULL,
                               start_time TIMESTAMP NOT NULL,
                               end_time TIMESTAMP NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               organizer_id BIGINT
);