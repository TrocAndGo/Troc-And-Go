#!/usr/bin/env bash

echo Adding Container to Git Safe Directories
git config --global --add safe.directory ${containerWorkspaceFolder}

echo Starting PostgreSQL
sudo service postgresql start
