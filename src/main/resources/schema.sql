CREATE TABLE IF NOT EXISTS sport_types
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS sport_events
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    sport_type_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    status VARCHAR
(
    255
)
    );
