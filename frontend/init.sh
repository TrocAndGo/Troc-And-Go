#!/usr/bin/env bash

echo Installing Angular Cli
npm install -g @angular/cli@18

echo Installing frontend dependencies
cd frontend
npm install --loglevel info

echo 'source <(ng completion script)' >> ~/.bashrc
