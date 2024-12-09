#!/usr/bin/env bash

echo Initialising PostgreSQL Database
./database/init.sh

echo Initialising Spring Boot Backend
./backend/init.sh

echo Initialising Angular Frontend
./frontend/init.sh
