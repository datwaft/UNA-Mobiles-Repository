\echo '=> Connecting to database...'

\c airline

\echo '=> Creating functions and procedures...'

\echo '-> Creating user functions and procedures...'

CREATE OR REPLACE FUNCTION "get_authorization" (
  "in_username" VARCHAR,
  "in_password" VARCHAR
) RETURNS VARCHAR
AS $$
  DECLARE
    "user_password"      VARCHAR;
    "user_authorization" VARCHAR;
  BEGIN
    SELECT "password"
      FROM "user"
      INTO "user_password"
      WHERE "username" = "in_username";

    IF "user_password" = "in_password" then
      SELECT "authorization"
        FROM "user"
        INTO "user_authorization"
        WHERE "username" = "in_username";
      RETURN "user_authorization";
    ELSE
      RETURN 'none';
    END IF;
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "register_user" (
  "in_username"    VARCHAR,
  "in_password"    VARCHAR,
  "in_name"        VARCHAR,
  "in_lastname"    VARCHAR,
  "in_email"       VARCHAR,
  "in_address"     VARCHAR,
  "in_workphone"   VARCHAR,
  "in_mobilephone" VARCHAR
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
      "in_username",
      "in_password",
      'user',
      "in_name",
      "in_lastname",
      "in_email",
      "in_address",
      "in_workphone",
      "in_mobilephone"
    );
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION "get_user" (
  "in_username" VARCHAR
) RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT 
        "name",
        "lastname",
        "email",
        "address",
        "workphone",
        "mobilephone"
      FROM "user"
      WHERE "username" = "in_username";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "update_user" (
  "in_username"    VARCHAR,
  "in_name"        VARCHAR,
  "in_lastname"    VARCHAR,
  "in_email"       VARCHAR,
  "in_address"     VARCHAR,
  "in_workphone"   VARCHAR,
  "in_mobilephone" VARCHAR
)
AS $$
  BEGIN
    UPDATE "user" SET
      "name"        = "in_name",
      "lastname"    = "in_lastname",
      "email"       = "in_email",
      "address"     = "in_address",
      "workphone"   = "in_workphone",
      "mobilephone" = "in_mobilephone"
    WHERE "username" = "in_username";
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
