#!/usr/bin/env bash

echo Adding Container to Git Safe Directories
git config --global --add safe.directory $(pwd)

echo Starting PostgreSQL
sudo service postgresql start
