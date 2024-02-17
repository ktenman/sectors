CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE profile
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    agree_to_terms BOOLEAN      NOT NULL,
    session_id UUID NOT NULL UNIQUE
);

CREATE TABLE profile_sector
(
    profile_id BIGINT NOT NULL REFERENCES profile (id),
    sector_id  BIGINT NOT NULL REFERENCES sector (id),
    PRIMARY KEY (profile_id, sector_id)
);
