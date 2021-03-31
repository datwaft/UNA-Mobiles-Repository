\echo '=> Connecting to database...'

\c airline

\echo '=> Creating functions and procedures...'

\echo '-> Creating user functions and procedures...'

CREATE OR REPLACE FUNCTION "get_authorization" (
  "input_username" VARCHAR,
  "input_password" VARCHAR
) RETURNS VARCHAR
AS $$
  DECLARE
    "user_password"      VARCHAR;
    "user_authorization" VARCHAR;
  BEGIN
    SELECT "password"
      FROM "user"
      INTO "user_password"
      WHERE "username" = "input_username";

    IF "user_password" = "input_password" then
      SELECT "authorization"
        FROM "user"
        INTO "user_authorization"
        WHERE "username" = "input_username";
      RETURN "user_authorization";
    ELSE
      RETURN 'none';
    END IF;
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "register_user" (
  "input_username"    VARCHAR,
  "input_password"    VARCHAR,
  "input_name"        VARCHAR,
  "input_lastname"    VARCHAR,
  "input_email"       VARCHAR,
  "input_address"     VARCHAR,
  "input_workphone"   VARCHAR,
  "input_mobilephone" VARCHAR
)
AS $$
  BEGIN
    INSERT INTO "user" (
      "username",
      "password",
      "authorization",
      "name",
      "lastname",
      "email",
      "address",
      "workphone",
      "mobilephone"
    ) VALUES (
      "input_username",
      "input_password",
      'user',
      "input_name",
      "input_lastname",
      "input_email",
      "input_address",
      "input_workphone",
      "input_mobilephone"
    );
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

\echo '-> Creating schedule functions and procedures...'

CREATE OR REPLACE FUNCTION "get_all_schedules_with_discount" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "view_schedule" where "discount" > 0;
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;
