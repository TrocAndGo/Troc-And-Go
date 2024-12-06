#!/usr/bin/env bash
cd "$(dirname "$0")"
source ../database/db.env

echo Starting PostgreSQL
sudo service postgresql start

./mvnw spring-boot:run
