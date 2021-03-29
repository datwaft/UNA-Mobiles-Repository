\echo '=> Disabling connections to database...'

REVOKE CONNECT ON DATABASE airline FROM public;

\echo '=> Terminating all conections...'

SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'airline';

\echo '=> Dropping database...'

drop database airline;

\echo '=> Creating database...'

create database airline;

\echo '=> Enabling connections to database...'

GRANT CONNECT ON DATABASE airline TO public;
