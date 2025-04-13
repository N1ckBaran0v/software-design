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

ALTER TABLE users
    ADD CONSTRAINT pk_users     PRIMARY KEY(id),
    ADD CONSTRAINT nn_user_name CHECK(user_name IS NOT NULL),
    ADD CONSTRAINT un_user_name UNIQUE(user_name),
    ADD CONSTRAINT nn_pass_word CHECK(pass_word IS NOT NULL),
    ADD CONSTRAINT nn_real_name CHECK(real_name IS NOT NULL),
    ADD CONSTRAINT nn_user_role CHECK(user_role IS NOT NULL),
    ADD CONSTRAINT nn_is_active CHECK(is_active IS NOT NULL);

ALTER TABLE filters
    ADD CONSTRAINT pk_filters     PRIMARY KEY(id),
    ADD CONSTRAINT fk_user_id     FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE cascade,
    ADD CONSTRAINT nn_filter_name CHECK(filter_name IS NOT NULL),
    ADD CONSTRAINT un_pair        UNIQUE(user_id, filter_name),
    ADD CONSTRAINT nn_departure   CHECK(departure IS NOT NULL),
    ADD CONSTRAINT nn_destination CHECK(destination IS NOT NULL),
    ADD CONSTRAINT nn_transfers   CHECK(transfers IS NOT NULL AND transfers >= 0);

ALTER TABLE passengers
    ADD CONSTRAINT pk_passengers       PRIMARY KEY(id),
    ADD CONSTRAINT fk_filter_id        FOREIGN KEY(filter_id) REFERENCES filters(id) ON DELETE CASCADE,
    ADD CONSTRAINT nn_passengers_type  CHECK(passengers_type IS NOT NULL),
    ADD CONSTRAINT un_pair_passengers  UNIQUE(filter_id, passengers_type),
    ADD CONSTRAINT nn_passengers_count CHECK(passengers_count IS NOT NULL AND passengers_count > 0);

ALTER TABLE railcars
    ADD CONSTRAINT pk_railcars      PRIMARY KEY(id),
    ADD CONSTRAINT nn_railcar_model CHECK(railcar_model IS NOT NULL),
    ADD CONSTRAINT un_railcar_model UNIQUE(railcar_model),
    ADD CONSTRAINT nn_railcar_type  CHECK(railcar_type IS NOT NULL);

ALTER TABLE places
    ADD CONSTRAINT pk_places       PRIMARY KEY(id),
    ADD CONSTRAINT fk_railcar_id   FOREIGN KEY(railcar_id) REFERENCES railcars(id) ON DELETE CASCADE,
    ADD CONSTRAINT nn_place_number CHECK(place_number IS NOT NULL),
    ADD CONSTRAINT un_pair_places  UNIQUE(railcar_id, place_number),
    ADD CONSTRAINT nn_description  CHECK(description IS NOT NULL),
    ADD CONSTRAINT nn_purpose 	   CHECK(purpose IS NOT NULL),
    ADD CONSTRAINT nn_place_cost   CHECK(place_cost IS NOT NULL AND place_cost >= 0);

ALTER TABLE trains
    ADD CONSTRAINT pk_trains      PRIMARY KEY(id),
    ADD CONSTRAINT nn_train_class CHECK(train_class IS NOT NULL);

ALTER TABLE railcars_in_trains
    ADD CONSTRAINT pk_railcars_in_trains PRIMARY KEY(id),
    ADD CONSTRAINT fk_train_id           FOREIGN KEY(train_id)   REFERENCES trains(id)   ON DELETE CASCADE,
    ADD CONSTRAINT fk_railcar_id         FOREIGN KEY(railcar_id) REFERENCES railcars(id) ON DELETE CASCADE;


ALTER TABLE races
    ADD CONSTRAINT pk_races    PRIMARY KEY(id),
    ADD CONSTRAINT fk_train_id FOREIGN KEY(train_id) REFERENCES trains(id) ON DELETE CASCADE,
    ADD CONSTRAINT nn_finished CHECK(finished IS NOT NULL);

ALTER TABLE schedule
    ADD CONSTRAINT pk_schedule     PRIMARY KEY(id),
    ADD CONSTRAINT fk_race_id      FOREIGN KEY(race_id) REFERENCES races(id) ON DELETE cascade,
    ADD CONSTRAINT nn_station_name CHECK(station_name IS NOT NULL),
    ADD CONSTRAINT time_constraint CHECK((arrival IS NOT NULL AND departure IS NOT NULL AND arrival < departure) OR (arrival IS NOT NULL AND departure IS NULL) OR (arrival IS NULL AND departure IS NOT NULL)),
    ADD CONSTRAINT nn_multiplier   CHECK(multiplier IS NOT NULL AND multiplier >= 0);

ALTER TABLE tickets
    ADD CONSTRAINT pk_tickets     PRIMARY KEY(id),
    ADD CONSTRAINT fk_user_id     FOREIGN KEY(user_id)     REFERENCES users(id)    ON DELETE CASCADE,
    ADD CONSTRAINT fk_race        FOREIGN KEY(race_id)     REFERENCES races(id)    ON DELETE CASCADE,
    ADD CONSTRAINT fk_departure   FOREIGN KEY(departure)   REFERENCES schedule(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_destination FOREIGN KEY(destination) REFERENCES schedule(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_place_id    FOREIGN KEY(place_id)    REFERENCES places(id)   ON DELETE cascade,
    ADD CONSTRAINT nn_passenger   CHECK(passenger IS NOT NULL),
    ADD CONSTRAINT nn_railcar     CHECK(railcar IS NOT NULL AND railcar > 0),
    ADD CONSTRAINT nn_ticket_cost CHECK(ticket_cost IS NOT NULL AND ticket_cost >= 0);

ALTER TABLE train_comments
    ADD CONSTRAINT pk_comments     PRIMARY KEY(id),
    ADD CONSTRAINT fk_user_id      FOREIGN KEY(user_id)  REFERENCES users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_train_id     FOREIGN KEY(train_id) REFERENCES trains(id) ON DELETE CASCADE,
    ADD CONSTRAINT nn_score        CHECK(score IS NOT NULL AND score > 0 AND score < 6),
    ADD CONSTRAINT nn_comment_text CHECK(comment_text IS NOT NULL);

CREATE OR REPLACE VIEW users_view AS SELECT * FROM users;

CREATE OR REPLACE FUNCTION insteadof_delete()
    returns trigger
as $$
begin
    UPDATE users_view
    SET is_active = FALSE
    where id = old.id;
    return old;
end $$
    language plpgsql;

create or replace trigger inof_del
    instead of delete on users_view
    for each row
execute procedure insteadof_delete();
