\echo '=> Connecting to database...'

\c airline

\echo '=> Creating functions and procedures...'

CREATE OR REPLACE FUNCTION can_log_in (
  "input_username" VARCHAR,
  "input_password" VARCHAR
)
RETURNS BOOLEAN
LANGUAGE PLPGSQL
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
END; $$
