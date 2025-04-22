CREATE TABLE users (
    id        BIGSERIAL,
    user_name TEXT,
    pass_word TEXT,
    real_name TEXT,
    user_role TEXT,
    is_active BOOLEAN
);

CREATE TABLE filters (
    id          BIGSERIAL,
    user_id     BIGINT,
    filter_name TEXT,
    departure   TEXT,
    destination TEXT,
    transfers   INT
);

CREATE TABLE passengers (
    id               BIGSERIAL,
    filter_id        BIGINT,
    passengers_type  TEXT,
    passengers_count INT
);

CREATE TABLE railcars (
    id            BIGSERIAL,
    railcar_model TEXT,
    railcar_type  TEXT
);

CREATE TABLE places (
    id           BIGSERIAL,
    railcar_id   BIGINT,
    place_number INT,
    description  TEXT,
    purpose      TEXT,
    place_cost   DECIMAL
);

CREATE TABLE trains (
    id          BIGSERIAL,
    train_class TEXT
);

CREATE TABLE railcars_in_trains (
    id         BIGSERIAL,
    train_id   BIGINT,
    railcar_id INT
);

CREATE TABLE races (
    id       BIGSERIAL,
    train_id BIGINT,
    finished BOOLEAN
);

CREATE TABLE schedule (
    id           BIGSERIAL,
    race_id      BIGINT,
    station_name TEXT,
    arrival      TIMESTAMP,
    departure    TIMESTAMP,
    multiplier   DOUBLE PRECISION
);


CREATE TABLE tickets (
    id          BIGSERIAL,
    user_id     BIGINT,
    passenger   TEXT,
    race_id     BIGINT,
    railcar     INT,
    place_id    BIGINT,
    departure   BIGINT,
    destination BIGINT,
    ticket_cost DECIMAL
);

CREATE TABLE train_comments (
    id           BIGSERIAL,
    user_id      BIGINT,
    train_id     BIGINT,
    score        INT,
    comment_text TEXT
);
