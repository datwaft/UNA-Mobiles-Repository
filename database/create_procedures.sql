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

CREATE OR REPLACE FUNCTION "view_all_flight" ()
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

\echo '-> Creating purchase functions and procedures...'

CREATE OR REPLACE FUNCTION "view_all_purchase" (
  "in_username" VARCHAR
)
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * 
      FROM "view_purchase"
      WHERE "user" = "in_username";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "create_purchase" (
  "in_ticket_number" NUMERIC,
  "in_flight"        INTEGER,
  "in_user"          VARCHAR
)
AS $$
  BEGIN
    INSERT INTO "purchase" (
      "ticket_number",
      "flight",
      "user"
    ) VALUES (
      "in_ticket_number",
      "in_flight",
      "in_user"
    );
  END;
$$ LANGUAGE plpgsql;

\echo '-> Creating ticket functions and procedures...'

CREATE OR REPLACE FUNCTION "view_all_ticket_per_flight" (
  "in_flight" INTEGER
)
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * 
      FROM "view_ticket"
      WHERE "flight" = "in_flight";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION "view_all_ticket_per_purchase" (
  "in_purchase" INTEGER
)
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT
        "identifier" AS "identifier",
        "row" AS "row",
        "column" AS "column"
      FROM "ticket"
      WHERE "purchase" = "in_purchase";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "create_ticket" (
  "in_purchase" INTEGER,
  "in_row"      NUMERIC,
  "in_column"   NUMERIC
)
AS $$
  BEGIN
    INSERT INTO "ticket" (
      "purchase",
      "row",
      "column"
    ) VALUES (
      "in_purchase",
      "in_row",
      "in_column"
    );
  END;
$$ LANGUAGE plpgsql;

\echo '-> Creating plane type functions and procedures...'

CREATE OR REPLACE FUNCTION "get_all_plane_type" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "plane_type" ORDER BY "identifier";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "create_plane_type" (
  "in_year"    NUMERIC,
  "in_model"   VARCHAR,
  "in_brand"   VARCHAR,
  "in_rows"    NUMERIC,
  "in_columns" NUMERIC
)
AS $$
  BEGIN
    INSERT INTO "plane_type" (
      "year",
      "model",
      "brand",
      "rows",
      "columns"
    ) VALUES (
      "in_year",
      "in_model",
      "in_brand",
      "in_rows",
      "in_columns"
    );
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "update_plane_type" (
  "in_identifier"    INTEGER,
  "in_year"    NUMERIC,
  "in_model"   VARCHAR,
  "in_brand"   VARCHAR,
  "in_rows"    NUMERIC,
  "in_columns" NUMERIC
)
AS $$
  BEGIN
    UPDATE "plane_type" SET
      "year"    = "in_year",
      "model"   = "in_model",
      "brand"   = "in_brand",
      "rows"    = "in_rows",
      "columns" = "in_columns"
    WHERE "identifier" = "in_identifier";
  END;
$$ LANGUAGE plpgsql;

\echo '-> Creating route functions and procedures...'

CREATE OR REPLACE FUNCTION "get_all_route" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "route" ORDER BY "identifier";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "create_route" (
  "in_origin"      VARCHAR,
  "in_destination" VARCHAR,
  "in_duration"    TIME,
  "in_price"       NUMERIC
)
AS $$
  BEGIN
    INSERT INTO "route" (
      "origin",
      "destination",
      "duration",
      "price"
    ) VALUES (
      "in_origin",
      "in_destination",
      "in_duration",
      "in_price"
    );
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "update_route" (
  "in_identifier"  INTEGER,
  "in_origin"      VARCHAR,
  "in_destination" VARCHAR,
  "in_duration"    TIME,
  "in_price"       NUMERIC
)
AS $$
  BEGIN
    UPDATE "route" SET
      "origin"      = "in_origin",
      "destination" = "in_destination",
      "duration"    = "in_duration",
      "price"       = "in_price"
    WHERE "identifier" = "in_identifier";
  END;
$$ LANGUAGE plpgsql;

\echo '-> Creating plane functions and procedures...'

CREATE OR REPLACE FUNCTION "get_all_plane" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "plane" ORDER BY "identifier";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "create_plane" (
  "in_name" VARCHAR,
  "in_type" INTEGER
)
AS $$
  BEGIN
    INSERT INTO "plane" (
      "name",
      "type"
    ) VALUES (
      "in_name",
      "in_type"
    );
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "update_plane" (
  "in_identifier"  INTEGER,
  "in_name"        VARCHAR,
  "in_type"        INTEGER
)
AS $$
  BEGIN
    UPDATE "plane" SET
      "name" = "in_name",
      "type" = "in_type"
    WHERE "identifier" = "in_identifier";
  END;
$$ LANGUAGE plpgsql;

\echo '-> Creating schedule functions and procedures...'

CREATE OR REPLACE FUNCTION "view_all_schedule_with_discount" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "view_schedule" WHERE "discount" > 0 ORDER BY "identifier";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION "get_all_schedule" ()
RETURNS REFCURSOR
AS $$
  DECLARE
    "out_cursor" REFCURSOR;
  BEGIN
    OPEN "out_cursor" FOR
      SELECT * FROM "schedule" ORDER BY "identifier";
    RETURN "out_cursor";
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "create_schedule" (
  "in_route"          INTEGER,
  "in_departure_time" TIME,
  "in_weekday"        NUMERIC(1, 0),
  "in_discount"       NUMERIC(3, 2)
)
AS $$
  BEGIN
    INSERT INTO "schedule" (
      "route",
      "departure_time",
      "weekday",
      "discount"
    ) VALUES (
      "in_route",
      "in_departure_time",
      "in_weekday",
      "in_discount"
    );
  END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE "update_schedule" (
  "in_identifier"     INTEGER,
  "in_route"          INTEGER,
  "in_departure_time" TIME,
  "in_weekday"        NUMERIC(1, 0),
  "in_discount"       NUMERIC(3, 2)
)
AS $$
  BEGIN
    UPDATE "schedule" SET
      "route"          = "in_route",
      "departure_time" = "in_departure_time",
      "weekday"        = "in_weekday",
      "discount"       = "in_discount"
    WHERE "identifier" = "in_identifier";
  END;
$$ LANGUAGE plpgsql;
