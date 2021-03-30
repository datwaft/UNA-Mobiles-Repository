\echo '=> Connecting to database...'

\c airline

\echo '=> Populating tables...'

INSERT INTO "user" (
  "username",
  "password",
  "name",
  "lastname",
  "email",
  "address",
  "workphone",
  "mobilephone"
) values (
  'user1',
  'password',
  'Pepito',
  'Maximov',
  'user1@mail.com',
  'Lorem ipsum dolor sit amet, consectetur.',
  '85674587',
  '87654534'
), (
  'user2',
  'password',
  'Juanito',
  'Calaquero',
  'user2@mail.com',
  'Lorem ipsum dolor sit amet, consectetur.',
  '82672590',
  '86624597'
);

INSERT INTO "plane_type" (
  "year",
  "model",
  "brand",
  "rows",
  "columns"
) values (
  1999,
  'GFA-69',
  'Kawasaki',
  30,
  9
), (
  2002,
  'PT-420',
  'Miyamoto',
  27,
  6
), (
  1980,
  'RND-32',
  'Marea',
  23,
  6
), (
  2008,
  'RL-666',
  'Crisis',
  25,
  9
);

INSERT INTO "route" (
  "origin",
  "destination",
  "duration",
  "price"
) values (
  'Costa Rica',
  'China',
  '17:00:00',
  1200
), (
  'China',
  'Japan',
  '03:00:00',
  2500
), (
  'Japan',
  'China',
  '02:00:00',
  3200
), (
  'China',
  'Costa Rica',
  '17:00:00',
  4300
), (
  'Spain',
  'USA',
  '08:30:00',
  120
);

INSERT INTO "plane" (
  "name",
  "type"
) values (
  'Bill Gates',
  1
), (
  'Johnson',
  2
), (
  'Pepitos',
  3
), (
  'Life',
  4
);

INSERT INTO "schedule" (
  "route",
  "departure_time",
  "weekday",
  "discount"
) values (
  1,
  '22:30',
  0,
  0.30
), (
  4,
  '10:30',
  3,
  0.40
), (
  2,
  '07:00',
  2,
  0
);

INSERT INTO "flight" (
  "plane",
  "outbound_date",
  "outbound_schedule",
  "inbound_date",
  "inbound_schedule"
) values (
  1,
  '2020-03-22',
  1,
  null,
  null
), (
  2,
  '2020-05-12',
  1,
  '2020-06-12',
  2
), (
  3,
  '2020-09-10',
  3,
  null,
  null
);

INSERT INTO "purchase" (
  "ticket_number",
  "flight",
  "user"
) values (
  5,
  1,
  'user1'
);
