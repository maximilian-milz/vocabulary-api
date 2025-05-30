-- Create a vocabulary_entries table
CREATE TABLE vocabulary_entries (
    id BIGSERIAL PRIMARY KEY,
    word_pt VARCHAR(255) NOT NULL,
    word_de VARCHAR(255) NOT NULL,
    example TEXT NOT NULL,
    level INT NOT NULL,
    next_review DATE NOT NULL,
    category VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Add indexes for common queries
CREATE INDEX idx_vocabulary_entries_category ON vocabulary_entries(category);
CREATE INDEX idx_vocabulary_entries_next_review ON vocabulary_entries(next_review);
