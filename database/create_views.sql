\echo '=> Connecting to database...'

\c airline

\echo '=> Creating views...'

CREATE VIEW "view_flight" AS
  SELECT
    f.identifier AS identifier,
    r1.origin AS origin,
    r1.destination AS destination,
    f.outbound_date AS outbound_date,
    f.inbound_date AS inbound_date,
    COALESCE(SUM(p.ticket_number), 0) AS passenger_amount,
    (pt.columns * pt.rows) AS passenger_total,
    r1.price - (r1.price * s1.discount) +
      COALESCE(r2.price - (r2.price * s2.discount), 0) AS ticket_price
  FROM
    flight f
  INNER JOIN schedule s1
    ON s1.identifier = f.outbound_schedule
  LEFT JOIN schedule s2
    ON s2.identifier = f.inbound_schedule
  INNER JOIN route r1
    ON r1.identifier = s1.route
  LEFT JOIN route r2
    ON r2.identifier = s2.route
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
    r1.price,
    r2.price,
    s1.discount,
    s2.discount,
    f.outbound_date,
    f.inbound_date,
    pt.columns,
    pt.rows;

CREATE VIEW "view_schedule" AS
  SELECT
    s.identifier AS identifier,
    r.origin AS origin,
    r.destination AS destination,
    s.departure_time AS departure_time,
    s.weekday AS weekday,
    r.duration AS duration,
    r.price AS price,
    s.discount AS discount
  FROM
    schedule s
  INNER JOIN route r
    ON s.route = r.identifier;

CREATE VIEW "view_purchase" AS
  SELECT
    p.identifier AS identifier,
    p.user AS "user",
    p.timestamp AS "timestamp",
    r1.origin AS origin,
    r1.destination AS destination,
    f.outbound_date AS outbound_date,
    f.inbound_date AS inbound_date,
    p.ticket_number AS ticket_amount,
    r1.price - (r1.price * s1.discount) +
      COALESCE(r2.price - (r2.price * s2.discount), 0) *
      p.ticket_number AS total_cost,
    count(t) <> 0 AS has_been_reserved,
    pt.rows AS plane_rows,
    pt.columns AS plane_columns,
    p.flight AS flight
  FROM
    purchase p
  INNER JOIN flight f
    ON f.identifier = p.flight
  INNER JOIN schedule s1
    ON s1.identifier = f.outbound_schedule
  LEFT JOIN schedule s2
    ON s2.identifier = f.inbound_schedule
  INNER JOIN route r1
    ON r1.identifier = s1.route
  LEFT JOIN route r2
    ON r2.identifier = s2.route
  FULL OUTER JOIN ticket t
    ON t.purchase = p.identifier
  INNER JOIN plane pl
    ON f.plane = pl.identifier
  INNER JOIN plane_type pt
    ON pl.type = pt.identifier
  GROUP BY
    p.identifier,
    p.user,
    p.timestamp,
    r1.origin,
    r1.destination,
    r1.price,
    r2.price,
    s1.discount,
    s2.discount,
    f.outbound_date,
    f.inbound_date,
    p.ticket_number,
    pt.rows,
    pt.columns,
    p.flight;

CREATE VIEW "view_ticket" AS
  SELECT
    t.identifier AS identifier,
    t.row AS "row",
    t.column AS "column",
    p.flight AS flight
  FROM
    ticket t
  INNER JOIN purchase p
    ON t.purchase = p.identifier;
