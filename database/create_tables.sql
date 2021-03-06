\echo '=> Connecting to database...'

\c airline

\echo '=> Creating tables...'

CREATE TABLE IF NOT EXISTS "user" (
  "username"      VARCHAR NOT NULL PRIMARY KEY,
  "password"      VARCHAR NOT NULL,
  "authorization" VARCHAR NOT NULL,
  "name"          VARCHAR NOT NULL,
  "lastname"      VARCHAR NOT NULL,
  "email"         VARCHAR NOT NULL,
  "address"       VARCHAR NOT NULL,
  "workphone"     VARCHAR NOT NULL,
  "mobilephone"   VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "plane_type" (
  "identifier" SERIAL  NOT NULL PRIMARY KEY,
  "year"       NUMERIC NOT NULL,
  "model"      VARCHAR NOT NULL,
  "brand"      VARCHAR NOT NULL,
  "rows"       NUMERIC NOT NULL,
  "columns"    NUMERIC NOT NULL
);

CREATE TABLE IF NOT EXISTS "route" (
  "identifier"  SERIAL  NOT NULL PRIMARY KEY,
  "origin"      VARCHAR NOT NULL,
  "destination" VARCHAR NOT NULL,
  "duration"    TIME    NOT NULL,
  "price"       NUMERIC NOT NULL
);

CREATE TABLE IF NOT EXISTS "plane" (
  "identifier"  SERIAL  NOT NULL PRIMARY KEY,
  "name"        VARCHAR NOT NULL,
  "type"        SERIAL  NOT NULL
);

CREATE TABLE IF NOT EXISTS "schedule" (
  "identifier"     SERIAL        NOT NULL PRIMARY KEY,
  "route"          SERIAL        NOT NULL,
  "departure_time" TIME          NOT NULL,
  "weekday"        NUMERIC(1, 0) NOT NULL,
  "discount"       NUMERIC(3, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS "flight" (
  "identifier"        SERIAL NOT NULL PRIMARY KEY,
  "plane"             SERIAL NOT NULL,
  "outbound_date"     DATE   NOT NULL,
  "outbound_schedule" SERIAL NOT NULL,
  "inbound_date"      DATE       NULL,
  "inbound_schedule"  INTEGER    NULL
);

CREATE TABLE IF NOT EXISTS "purchase" (
  "identifier"    SERIAL    NOT NULL PRIMARY KEY,
  "ticket_number" NUMERIC   NOT NULL,
  "timestamp"     TIMESTAMP          WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "flight"        SERIAL    NOT NULL,
  "user"          VARCHAR   NOT NULL
);

CREATE TABLE IF NOT EXISTS "ticket" (
  "identifier" SERIAL  NOT NULL PRIMARY KEY,
  "purchase"   SERIAL  NOT NULL,
  "row"        NUMERIC NOT NULL,
  "column"     NUMERIC NOT NULL
);

\echo '=> Creating constraints...'

ALTER TABLE "plane_type"
ADD CONSTRAINT "plane_type_year_check"
CHECK ( "year" BETWEEN 1901 AND 2099 );

ALTER TABLE "plane_type"
ADD CONSTRAINT "plane_type_rows_check"
CHECK ( "rows" BETWEEN 25 AND 30 );

ALTER TABLE "plane_type"
ADD CONSTRAINT "plane_type_columns_check"
CHECK ( "columns" IN (6, 9) );

ALTER TABLE "user"
ADD CONSTRAINT "user_authorization_check"
CHECK ( "authorization" IN ('user', 'admin') );

ALTER TABLE "schedule"
ADD CONSTRAINT "schedule_weekday_check"
CHECK ( "weekday" BETWEEN 0 AND 6 );

ALTER TABLE "schedule"
ADD CONSTRAINT "schedule_discount_check"
CHECK ( "discount" BETWEEN 0 AND 1 );

ALTER TABLE "flight"
ADD CONSTRAINT "flight_date_check"
CHECK ( "outbound_date" < "inbound_date" );

ALTER TABLE "flight"
ADD CONSTRAINT "flight_inbound_null_check"
CHECK ( "inbound_schedule" IS NOT NULL OR "inbound_date" IS NULL );

\echo '=> Creating foreign keys...'

ALTER TABLE "plane"
ADD CONSTRAINT "plane_plane_type_fk"
FOREIGN KEY ("type")
REFERENCES "plane_type" ("identifier")
ON DELETE CASCADE;

ALTER TABLE "schedule"
ADD CONSTRAINT "schedule_route_fk"
FOREIGN KEY ("route")
REFERENCES "route" ("identifier")
ON DELETE CASCADE;

ALTER TABLE "flight"
ADD CONSTRAINT "flight_plane_fk"
FOREIGN KEY ("plane")
REFERENCES "plane" ("identifier")
ON DELETE CASCADE;

ALTER TABLE "flight"
ADD CONSTRAINT "flight_outbound_schedule_fk"
FOREIGN KEY ("outbound_schedule")
REFERENCES "schedule" ("identifier")
ON DELETE CASCADE;

ALTER TABLE "flight"
ADD CONSTRAINT "flight_inbound_schedule_fk"
FOREIGN KEY ("inbound_schedule")
REFERENCES "schedule" ("identifier")
ON DELETE CASCADE;

ALTER TABLE "purchase"
ADD CONSTRAINT "purchase_flight_fk"
FOREIGN KEY ("flight")
REFERENCES "flight" ("identifier")
ON DELETE CASCADE;

ALTER TABLE "purchase"
ADD CONSTRAINT "purchase_user_fk"
FOREIGN KEY ("user")
REFERENCES "user" ("username")
ON DELETE CASCADE;

ALTER TABLE "ticket"
ADD CONSTRAINT "ticket_purchase_fk"
FOREIGN KEY ("purchase")
REFERENCES "purchase" ("identifier")
ON DELETE CASCADE;
