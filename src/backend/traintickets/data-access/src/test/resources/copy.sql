COPY users(user_name, pass_word, real_name, user_role, is_active)
    FROM '/data/users.csv'
    DELIMITER ',' CSV HEADER;

COPY railcars(railcar_model, railcar_type)
    FROM '/data/railcars.csv'
    DELIMITER ',' CSV HEADER;

COPY places(railcar_id, place_number, description, purpose, place_cost)
    FROM '/data/places.csv'
    DELIMITER ',' CSV HEADER;

COPY trains(train_class)
    FROM '/data/trains.csv'
    DELIMITER ',' CSV HEADER;

COPY railcars_in_trains(train_id, railcar_id)
    FROM '/data/railcars_in_trains.csv'
    DELIMITER ',' CSV HEADER;

COPY races(train_id, finished)
    FROM '/data/races.csv'
    DELIMITER ',' CSV HEADER;

COPY schedule(race_id, station_name, arrival, departure, multiplier)
    FROM '/data/schedule.csv'
    DELIMITER ',' CSV HEADER NULL AS '';

COPY tickets(user_id, passenger, race_id, railcar, place_id, departure, destination, ticket_cost)
    FROM '/data/tickets.csv'
    DELIMITER ',' CSV HEADER NULL AS '';

COPY train_comments(user_id, train_id, score, comment_text)
    FROM '/data/train_comments.csv'
    DELIMITER ',' CSV HEADER;

COPY filters(user_id, filter_name, departure, destination, transfers)
    FROM '/data/filters.csv'
    DELIMITER ',' CSV HEADER;

COPY passengers(filter_id, passengers_type, passengers_count)
    FROM '/data/passengers.csv'
    DELIMITER ',' CSV HEADER;