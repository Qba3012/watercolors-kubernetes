#!/bin/bash
set -e

POSTGRES="psql --username ${POSTGRES_USER}"

echo "Creating database: catalogue"

$POSTGRES <<EOSQL
    create database catalogue owner ${POSTGRES_USER} encoding = 'UTF8';

EOSQL