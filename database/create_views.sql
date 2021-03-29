\echo '=> Connecting to database...'

\c airline

\echo '=> Creating views...'

CREATE VIEW "view_flight" AS
  SELECT
    f.identifier as identifier,
    r1.origin as origin,
    r1.destination as destination,
    f.outbound_date as outbound_date,
    f.inbound_date as inbound_date,
    COALESCE(SUM(p.ticket_number), 0) as passenger_amount,
    (pt.columns * pt.rows) as passenger_total
  FROM
    flight f
  INNER JOIN schedule s1
    ON s1.identifier = f.outbound_schedule
  INNER JOIN route r1
    ON r1.identifier = s1.route
  INNER JOIN plane pl
    ON pl.identifier = f.plane
  INNER JOIN plane_type pt
    ON pt.identifier = pl.type
  FULL OUTER JOIN purchase p
    ON p.flight = f.identifier
  GROUP BY
    f.identifier,
    r1.origin,
    r1.destination,
    f.outbound_date,
    f.inbound_date,
    pt.columns,
    pt.rows;
