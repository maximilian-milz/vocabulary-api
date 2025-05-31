-- Create a review_history table to track review results
CREATE TABLE review_history (
    id BIGSERIAL PRIMARY KEY,
    vocabulary_entry_id BIGINT NOT NULL,
    review_date DATE NOT NULL,
    quality_rating INT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_review_history_vocabulary_entry_id FOREIGN KEY (vocabulary_entry_id) REFERENCES vocabulary_entries(id) ON DELETE CASCADE
);

-- Add indexes for common queries
CREATE INDEX idx_review_history_vocabulary_entry_id ON review_history(vocabulary_entry_id);
CREATE INDEX idx_review_history_review_date ON review_history(review_date);