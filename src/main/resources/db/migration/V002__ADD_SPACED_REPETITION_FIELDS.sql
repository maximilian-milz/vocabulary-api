-- Add fields for spaced repetition algorithm to vocabulary_entries table
ALTER TABLE vocabulary_entries
    ADD COLUMN repetitions INT,
    ADD COLUMN ease_factor DOUBLE PRECISION,
    ADD COLUMN last_review_date DATE;

-- Add index for last_review_date to optimize queries
CREATE INDEX idx_vocabulary_entries_last_review_date ON vocabulary_entries(last_review_date);