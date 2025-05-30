-- Add metadata fields to vocabulary_entries table
ALTER TABLE vocabulary_entries
    ADD COLUMN notes TEXT,
    ADD COLUMN pronunciation VARCHAR(255);

-- Create table for vocabulary entry tags
CREATE TABLE vocabulary_entry_tags (
    vocabulary_entry_id BIGINT NOT NULL,
    tags VARCHAR(255),
    CONSTRAINT fk_vocabulary_entry_tags_vocabulary_entry_id FOREIGN KEY (vocabulary_entry_id) REFERENCES vocabulary_entries(id) ON DELETE CASCADE
);

-- Add index for vocabulary_entry_id to optimize queries
CREATE INDEX idx_vocabulary_entry_tags_vocabulary_entry_id ON vocabulary_entry_tags(vocabulary_entry_id);