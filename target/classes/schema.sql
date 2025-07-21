CREATE TABLE IF NOT EXISTS assets (
    id UUID PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    url TEXT NOT NULL,
    size BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL
);
DELETE FROM assets;
INSERT INTO assets (id, filename, content_type, url, size, upload_date) VALUES
    ('c1a7e2b4-1b5a-4d2a-a0f1-1fa1a8f72e9f', 'example-image.jpg', 'image/jpg', 'http://localhost:8080/assets/example-image.jpg', 123456, '2025-07-15T10:00:00'),
    ('b2c3f1d0-12a9-4c31-9b0c-25ef5b4c67e1', 'example-video.mp4', 'video/mp4', 'http://localhost:8080/assets/example-video.mp4', 987654, '2025-07-20T12:00:00'),
    ('9f3a9a64-b1d3-45de-a24d-2e4a7928fd7c', 'demo.png', 'image/png', 'http://localhost:8080/assets/demo.png', 234567, '2025-07-25T15:30:00');