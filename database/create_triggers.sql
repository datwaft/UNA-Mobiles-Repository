\echo '=> Connecting to database...'

\c airline

\echo '=> Creating triggers...'

\echo '-> Creating trigger for ticket number...'

CREATE OR REPLACE FUNCTION "purchase_ticket_number_trigger_insert" ()
RETURNS TRIGGER
AS $$
  DECLARE
    total_ticket_number NUMERIC;
    max_ticket_number   NUMERIC;
  BEGIN
    SELECT passenger_amount, passenger_total
    INTO total_ticket_number, max_ticket_number
    FROM view_flight
    WHERE identifier = NEW.flight;

    IF total_ticket_number + NEW.ticket_number > max_ticket_number THEN
      RAISE EXCEPTION 'Ticket number of purchase exceeds maximum ticket number';
    END IF;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "purchase_ticket_number_trigger_insert"
  BEFORE INSERT
  ON "purchase"
  FOR EACH ROW
    EXECUTE PROCEDURE "purchase_ticket_number_trigger_insert"();

CREATE OR REPLACE FUNCTION "purchase_ticket_number_trigger_update" ()
RETURNS TRIGGER
AS $$
  DECLARE
    total_ticket_number NUMERIC;
    max_ticket_number   NUMERIC;
  BEGIN
    SELECT passenger_amount, passenger_total
    INTO total_ticket_number, max_ticket_number
    FROM view_flight
    WHERE identifier = NEW.flight;

    IF total_ticket_number - OLD.ticket_number + NEW.ticket_number > max_ticket_number THEN
      RAISE EXCEPTION 'Ticket number of purchase exceeds maximum ticket number';
    END IF;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "purchase_ticket_number_trigger_update"
  BEFORE UPDATE
  ON "purchase"
  FOR EACH ROW
    EXECUTE PROCEDURE "purchase_ticket_number_trigger_update"();

\echo '-> Creating trigger for ticket row and column...'

CREATE OR REPLACE FUNCTION "ticket_row_column_trigger" ()
RETURNS TRIGGER
AS $$
  DECLARE
    max_row    NUMERIC;
    max_column NUMERIC;
  BEGIN
    SELECT pt.rows, pt.columns
    INTO max_row, max_column
    FROM plane_type pt
    INNER JOIN plane p
      ON p.type = pt.identifier
    INNER JOIN flight f
      ON f.plane = p.identifier
    INNER JOIN purchase pu
      ON pu.flight = f.identifier
    WHERE
      pu.identifier = NEW.purchase;

    IF NEW.row NOT BETWEEN 1 AND max_row THEN
      RAISE EXCEPTION 'Ticket row not between 1 and maximum value';
    END IF;
    IF NEW.column NOT BETWEEN 1 AND max_column THEN
      RAISE EXCEPTION 'Ticket column not between 1 and maximum value';
    END IF;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "ticket_row_column_trigger_insert"
  BEFORE INSERT
  ON "ticket"
  FOR EACH ROW
    EXECUTE PROCEDURE "ticket_row_column_trigger"();

CREATE TRIGGER "ticket_row_column_trigger_update"
  BEFORE UPDATE
  ON "ticket"
  FOR EACH ROW
    EXECUTE PROCEDURE "ticket_row_column_trigger"();

\echo '-> Creating trigger for flight dates...'

CREATE OR REPLACE FUNCTION "flight_date_trigger" ()
RETURNS TRIGGER
AS $$
  DECLARE
    weekday_outbound_date NUMERIC;
    weekday_inbound_date  NUMERIC;
    weekday_outbound      NUMERIC;
    weekday_inbound       NUMERIC;
  BEGIN
    SELECT weekday
    INTO weekday_outbound
    FROM schedule
    WHERE identifier = NEW.outbound_schedule;

    SELECT weekday
    INTO weekday_inbound
    FROM schedule
    WHERE identifier = NEW.inbound_schedule;

    SELECT EXTRACT(ISODOW FROM NEW.outbound_date) - 1
    INTO weekday_outbound_date;

    SELECT EXTRACT(ISODOW FROM NEW.inbound_date) - 1
    INTO weekday_inbound_date;

    IF weekday_outbound <> weekday_outbound_date THEN
      RAISE EXCEPTION 'Weekday of outbound date "%" does not follow the weekday of outbound schedule "%"', weekday_outbound_date, weekday_outbound;
    END IF;
    IF weekday_inbound <> weekday_inbound_date THEN
      RAISE EXCEPTION 'Weekday of inbound date "%" does not follow the weekday of inbound schedule "%"', weekday_inbound_date, weekday_inbound;
    END IF;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "flight_date_trigger_insert"
  BEFORE INSERT
  ON "flight"
  FOR EACH ROW
    EXECUTE PROCEDURE "flight_date_trigger"();

CREATE TRIGGER "flight_date_trigger_update"
  BEFORE UPDATE
  ON "flight"
  FOR EACH ROW
    EXECUTE PROCEDURE "flight_date_trigger"();

\echo '-> Creating trigger for flight origin and destination...'

CREATE OR REPLACE FUNCTION "flight_origin_destination_trigger" ()
RETURNS TRIGGER
AS $$
  DECLARE
    outbound_origin      VARCHAR;
    outbound_destination VARCHAR;
    inbound_origin       VARCHAR;
    inbound_destination  VARCHAR;
  BEGIN
    IF NEW.inbound_schedule IS NULL THEN
      RETURN NEW;
    END IF;

    SELECT r1.origin, r1.destination, r2.origin, r2.destination
    INTO outbound_origin, outbound_destination, inbound_origin, inbound_destination
    FROM route r1, route r2, schedule s1, schedule s2
    WHERE
      r1.identifier = s1.route AND
      r2.identifier = s2.route AND
      NEW.outbound_schedule = s1.identifier AND
      NEW.inbound_schedule = s2.identifier;

    IF outbound_origin <> inbound_destination THEN
      RAISE EXCEPTION 'Outbound origin is different from inbound destination';
    END IF;
    IF inbound_origin <> outbound_destination THEN
      RAISE EXCEPTION 'Outbound destination is different from inbound origin';
    END IF;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "flight_origin_destination_trigger_insert"
  BEFORE INSERT
  ON "flight"
  FOR EACH ROW
    EXECUTE PROCEDURE "flight_origin_destination_trigger"();

CREATE TRIGGER "flight_origin_destination_trigger_update"
  BEFORE UPDATE
  ON "flight"
  FOR EACH ROW
    EXECUTE PROCEDURE "flight_origin_destination_trigger"();
