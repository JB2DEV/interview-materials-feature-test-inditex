CREATE TABLE IF NOT EXISTS assets (
    id UUID PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    url TEXT NOT NULL,
    size BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL
);