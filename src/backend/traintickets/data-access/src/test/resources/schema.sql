CREATE table if not exists Users (
                                     id BIGSERIAL,
                                     user_name VARCHAR(50),
                                     pass_word VARCHAR(50),
                                     real_name VARCHAR(50),
                                     user_role TEXT,
                                     is_active BOOLEAN
);

CREATE table if not exists Filters (
                                       id BIGSERIAL,
                                       user_id BIGINT,
                                       filter_name TEXT,
                                       departure TEXT,
                                       destination TEXT,
                                       transfers INT
);

CREATE table if not exists Passengers (
                                          id BIGSERIAL,
                                          filter_id BIGINT,
                                          passengers_type TEXT,
                                          passengers_count INT
);

CREATE table if not exists Places (
                                      id BIGSERIAL,
                                      railcar_id BIGINT,
                                      place_number INT,
                                      description TEXT,
                                      purpose TEXT,
                                      place_cost DECIMAL
);

CREATE table if not exists Railcars (
                                        id BIGSERIAL,
                                        railcar_model TEXT,
                                        railcar_type TEXT
);

CREATE table if not exists Trains (
                                      id BIGSERIAL,
                                      train_class TEXT
);

CREATE table if not exists RailcarsInTrains (
                                                id BIGSERIAL,
                                                train_id BIGINT,
                                                railcar_id INT
);

CREATE table if not exists Races (
                                     id BIGSERIAL,
                                     train_id BIGINT,
                                     finished BOOLEAN
);

CREATE table if not exists Schedule (
                                        id BIGSERIAL,
                                        race_id BIGINT,
                                        station_name TEXT,
                                        arrival TIMESTAMP,
                                        departure TIMESTAMP,
                                        multiplier DOUBLE PRECISION
);


CREATE table if not exists Tickets (
                                       id BIGSERIAL,
                                       user_id BIGINT,
                                       passenger TEXT,
                                       race_id BIGINT,
                                       railcar INT,
                                       place_id BIGINT,
                                       departure BIGINT,
                                       destination BIGINT,
                                       ticket_cost DECIMAL
);

CREATE table if not exists Comments (
                                        id BIGSERIAL,
                                        user_id BIGINT,
                                        train_id BIGINT,
                                        score INT,
                                        comment_text TEXT
);

ALTER TABLE Users
    ADD CONSTRAINT pk_users PRIMARY KEY(id),
    ADD CONSTRAINT nn_user_name CHECK(user_name IS NOT NULL),
    ADD CONSTRAINT nn_pass_word CHECK(pass_word IS NOT NULL),
    ADD CONSTRAINT nn_real_name CHECK(real_name IS NOT NULL),
    ADD CONSTRAINT nn_user_role CHECK(user_role IS NOT NULL),
    ADD CONSTRAINT nn_is_active CHECK(is_active IS NOT NULL);

ALTER TABLE Filters
    ADD CONSTRAINT pk_filters PRIMARY KEY(id),
    ADD CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE CASCADE;

--CREATE table if not exists Filters (
--	id BIGSERIAL,
--	user_id BIGINT,
--	filter_name TEXT,
--	departure TEXT,
--	destination TEXT,
--	train_class TEXT,
--	transfers INT
--);

ALTER TABLE Passengers
    ADD CONSTRAINT pk_passengers PRIMARY KEY(id),
    ADD CONSTRAINT fk_filter_id FOREIGN KEY(filter_id) REFERENCES Filters(id) ON DELETE CASCADE;

--CREATE table if not exists Passengers (
--	id BIGSERIAL,
--	filter_id BIGINT,
--	passengers_type TEXT,
--	passengers_count INT
--);

ALTER TABLE Railcars
    ADD CONSTRAINT pk_railcars PRIMARY KEY(id);

--CREATE table if not exists Railcars (
--	id BIGSERIAL,
--	railcar_type TEXT
--);

ALTER TABLE Places
    ADD CONSTRAINT pk_places PRIMARY KEY(id),
    ADD CONSTRAINT fk_railcar_id FOREIGN KEY(railcar_id) REFERENCES Railcars(id) ON DELETE CASCADE;

--CREATE table if not exists Places (
--	id BIGSERIAL,
--	railcar_id BIGINT,
--	place_number INT,
--	description TEXT,
--	purpose TEXT,
--	place_cost DECIMAL
--);

ALTER TABLE Trains
    ADD CONSTRAINT pk_trains PRIMARY KEY(id);

--CREATE table if not exists Trains (
--	id BIGSERIAL,
--	train_class TEXT
--);

ALTER TABLE RailcarsInTrains
    ADD CONSTRAINT pk_railcars_in_trains PRIMARY KEY(id),
    ADD CONSTRAINT fk_train_id FOREIGN KEY(train_id) REFERENCES Trains(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_railcar_id FOREIGN KEY(railcar_id) REFERENCES Railcars(id) ON DELETE CASCADE;

--CREATE table if not exists RailcarsInTrains (
--	id BIGSERIAL,
--	train_id BIGINT,
--	railcar_number INT,
--	railcar_id INT
--);

ALTER TABLE Races
    ADD CONSTRAINT pk_races PRIMARY KEY(id),
    ADD CONSTRAINT fk_train_id FOREIGN KEY(train_id) REFERENCES Trains(id) ON DELETE CASCADE;

--CREATE table if not exists Races (
--	id BIGSERIAL,
--	train_id BIGINT,
--	finished BOOLEAN
--);

ALTER TABLE Schedule
    ADD CONSTRAINT pk_schedule PRIMARY KEY(id),
    ADD CONSTRAINT fk_race_id FOREIGN KEY(race_id) REFERENCES Races(id) ON DELETE CASCADE;

--CREATE table if not exists Schedule (
--	id BIGSERIAL,
--	race_id BIGINT,
--	station_name TEXT,
--	arrival TIMESTAMP,
--	departure TIMESTAMP,
--	multiplier DOUBLE PRECISION
--);

ALTER TABLE Tickets
    ADD CONSTRAINT pk_tickets PRIMARY KEY(id),
    ADD CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_race FOREIGN KEY(race_id) REFERENCES Races(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_departure FOREIGN KEY(departure) REFERENCES Schedule(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_destination FOREIGN KEY(destination) REFERENCES Schedule(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_place_id FOREIGN KEY(place_id) REFERENCES Places(id) ON DELETE CASCADE;

--CREATE table if not exists Tickets (
--	id BIGSERIAL,
--	user_id BIGINT,
--	passenger TEXT,
--	race_id BIGINT,
--	railcar INT,
--	place BIGINT,
--	departure BIGINT,
--	destination BIGINT,
--	ticket_cost DECIMAL
--);

ALTER TABLE Comments
    ADD CONSTRAINT pk_comments PRIMARY KEY(id),
    ADD CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_train_id FOREIGN KEY(train_id) REFERENCES Trains(id) ON DELETE CASCADE;

--CREATE table if not exists Comments (
--	id BIGSERIAL,
--	user_id BIGINT,
--	train_id BIGINT,
--	score INT,
--	comment_text TEXT
--);

create or replace view users_view as select * from Users;

create or replace function insteadof_delete()
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
