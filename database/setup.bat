@echo off

set PGPASSWORD=losiram01

psql -U postgres -f "create_database.sql"
echo.
psql -U postgres -f "create_tables.sql"
echo.
psql -U postgres -f "populate_tables.sql"
echo.
psql -U postgres -f "create_views.sql"
echo.
psql -U postgres -f "create_procedures.sql"
