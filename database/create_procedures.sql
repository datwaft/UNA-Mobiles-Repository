\echo '=> Connecting to database...'

\c airline

\echo '=> Creating functions and procedures...'

\echo '-> Creating user functions and procedures...'

CREATE OR REPLACE FUNCTION "can_log_in" (
  "input_username" VARCHAR,
  "input_password" VARCHAR
)
RETURNS BOOLEAN
AS $$
  DECLARE
    "true_password" VARCHAR;
  BEGIN
    SELECT "password"
    FROM "user"
    INTO "true_password"
    WHERE "username" = "input_username";

    IF "true_password" = "input_password" then
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  END;
$$ LANGUAGE plpgsql;

\echo '-> Creating flight functions and procedures...'

CREATE OR REPLACE FUNCTION "get_all_flights" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "view_flight";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;
