-- Create a review_sessions table to track review sessions
CREATE TABLE review_sessions (
    id BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_entries INT NOT NULL,
    completed_entries INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP NOT NULL
);

-- Create a table to track entries included in a session
CREATE TABLE review_session_entries (
    session_id BIGINT NOT NULL,
    vocabulary_entry_id BIGINT NOT NULL,
    reviewed BOOLEAN NOT NULL DEFAULT FALSE,
    quality_rating INT,
    review_time TIMESTAMP,
    PRIMARY KEY (session_id, vocabulary_entry_id),
    CONSTRAINT fk_review_session_entries_session_id FOREIGN KEY (session_id) REFERENCES review_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_session_entries_vocabulary_entry_id FOREIGN KEY (vocabulary_entry_id) REFERENCES vocabulary_entries(id) ON DELETE CASCADE
);

-- Add indexes for common queries
CREATE INDEX idx_review_sessions_status ON review_sessions(status);
CREATE INDEX idx_review_sessions_start_time ON review_sessions(start_time);
CREATE INDEX idx_review_session_entries_reviewed ON review_session_entries(reviewed);