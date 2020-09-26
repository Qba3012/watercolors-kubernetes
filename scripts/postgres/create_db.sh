#!/bin/bash
set -e

POSTGRES="psql --username ${POSTGRES_USER}"

echo "Creating database"

$POSTGRES <<EOSQL
CREATE DATABASE watercolors OWNER ${POSTGRES_USER};
EOSQL
