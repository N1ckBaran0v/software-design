CREATE OR REPLACE VIEW users_view AS SELECT * FROM users;

CREATE OR REPLACE FUNCTION insteadof_delete()
    RETURNS TRIGGER
AS $$
BEGIN
    UPDATE users_view
    SET is_active = FALSE WHERE id = old.id;
    RETURN old;
END $$
LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER inof_del
    INSTEAD OF DELETE ON users_view
    FOR EACH ROW
EXECUTE PROCEDURE insteadof_delete();
