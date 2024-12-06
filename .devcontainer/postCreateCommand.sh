#!/usr/bin/env bash

echo Initialising PostgreSQL Database
chmod u+x ./database/init.sh
./database/init.sh

echo Initialising Spring Boot Backend
chmod u+x ./backend/init.sh
./backend/init.sh

echo Initialising Angular Frontend
chmod u+x ./frontend/init.sh
./frontend/init.sh
