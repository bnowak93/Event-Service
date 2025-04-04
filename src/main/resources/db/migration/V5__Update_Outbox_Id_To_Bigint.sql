-- src/main/resources/db/migration/V5__Update_Outbox_Id_To_Bigint.sql

-- First drop the primary key constraint
ALTER TABLE outbox_events DROP CONSTRAINT outbox_events_pkey;

-- Change the id column type to bigint
ALTER TABLE outbox_events ALTER COLUMN id TYPE bigint;

-- Rename the misspelled column
ALTER TABLE outbox_events RENAME COLUMN aggregate_is TO aggregate_id;

-- Add a new sequence for bigint
CREATE SEQUENCE IF NOT EXISTS outbox_events_id_seq_bigint
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Set the sequence to the current max id (if any rows exist)
SELECT SETVAL('outbox_events_id_seq_bigint', COALESCE((SELECT MAX(id) FROM outbox_events), 0) + 1, false);

-- Update the column to use the new sequence
ALTER TABLE outbox_events ALTER COLUMN id SET DEFAULT nextval('outbox_events_id_seq_bigint'::regclass);

-- Re-create the primary key constraint
ALTER TABLE outbox_events ADD CONSTRAINT outbox_events_pkey PRIMARY KEY (id);