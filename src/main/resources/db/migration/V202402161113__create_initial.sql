CREATE TABLE sector
(
    id        BIGINT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    parent_id BIGINT REFERENCES sector (id)
);
