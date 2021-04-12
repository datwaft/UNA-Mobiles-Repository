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
    coalesce(sum(p.ticket_number), 0) AS passenger_amount,
    (pt.columns * pt.rows) AS passenger_total,
    r1.price - (r1.price * s1.discount) +
      coalesce(r2.price - (r2.price * s2.discount), 0) AS ticket_price
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
      coalesce(r2.price - (r2.price * s2.discount), 0) *
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

CREATE VIEW "purchase_count_per_month_in_last_year" AS
  SELECT
    to_number(to_char(i, 'YYYY'), '9999') AS "year",
    to_number(to_char(i, 'MM'), '99') AS "month",
    count("identifier") AS "count"
  FROM generate_series(now() - INTERVAL '1 year', now(), '1 month') AS i
  LEFT JOIN "purchase"
    ON to_char(i, 'YYYY') = to_char("timestamp", 'YYYY') AND
      to_char(i, 'MM') = to_char("timestamp", 'MM')
  GROUP BY 1, 2
  ORDER BY 1 DESC, 2 DESC;

CREATE VIEW "revenue_per_purchase" AS
  SELECT
    p.identifier AS "purchase",
    p.timestamp AS "timestamp",
    sum(
      r1.price - (r1.price * s1.discount) +
      coalesce(r2.price - (r2.price * s2.discount), 0)
    ) AS "revenue"
  FROM purchase p
  INNER JOIN flight f
    ON p.flight = f.identifier
  INNER JOIN schedule s1
    ON f.outbound_schedule = s1.identifier
  INNER JOIN route r1
    ON s1.route = r1.identifier
  LEFT JOIN schedule s2
    ON f.inbound_schedule = s2.identifier
  LEFT JOIN route r2
    ON s2.route = r2.identifier
  GROUP BY 1
  ORDER BY 1 ASC;

CREATE VIEW "revenue_per_month_in_last_year" AS
  SELECT
    to_number(to_char(i, 'YYYY'), '9999') AS "year",
    to_number(to_char(i, 'MM'), '99') AS "month",
    coalesce(sum("revenue"), 0) AS "revenue"
  FROM generate_series(now() - INTERVAL '1 year', now(), '1 month') AS i
  LEFT JOIN "revenue_per_purchase"
    ON to_char(i, 'YYYY') = to_char("timestamp", 'YYYY') AND
      to_char(i, 'MM') = to_char("timestamp", 'MM')
  GROUP BY 1, 2
  ORDER BY 1 DESC, 2 DESC;

CREATE VIEW "clients_per_plane" AS
  SELECT
    pl.identifier AS "identifier",
    pl.name AS "name",
    u.username AS "client"
  FROM plane pl
  INNER JOIN flight f
    ON f.plane = pl.identifier
  INNER JOIN purchase p
    ON p.flight = f.identifier
  INNER JOIN "user" u
    ON p.user = u.username
  GROUP BY 1, 3
  ORDER BY 1 ASC;

CREATE VIEW "top_5_routes_per_ticket_number" AS
  SELECT
    r.identifier AS "identifier",
    r.origin AS "origin",
    r.destination AS "destination",
    coalesce(sum(p.ticket_number), 0) AS "ticket_number"
  FROM route r
  LEFT JOIN schedule s
    ON s.route = r.identifier
  LEFT JOIN flight f
    ON f.outbound_schedule = s.identifier OR
      f.inbound_schedule = s.identifier
  LEFT JOIN purchase p
    ON p.flight = f.identifier
  GROUP BY 1
  ORDER BY 4 DESC, 1 ASC
  FETCH FIRST 5 ROWS ONLY;
