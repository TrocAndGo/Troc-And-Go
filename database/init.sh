#!/usr/bin/env bash
cd "$(dirname "$0")"
source db.env

echo Starting PostgreSQL
sudo service postgresql start

echo Creating Database and its owner
psql -h $db_host -p $db_port -U postgres <<EOF
DROP DATABASE IF EXISTS $db_name;
DROP USER IF EXISTS $db_user;

CREATE USER $db_user WITH PASSWORD '$db_pw';
CREATE DATABASE $db_name OWNER $db_user;
EOF
