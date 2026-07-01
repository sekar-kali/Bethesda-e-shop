#!/bin/bash
set -e

# Cree une base de donnees par service (isolation des donnees par microservice),
# a partir de la variable POSTGRES_MULTIPLE_DATABASES definie dans docker-compose.yml.

function create_database() {
	local database=$1
	echo "Creation de la base '$database'"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	echo "Bases de donnees multiples demandees : $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
		create_database $db
	done
fi
