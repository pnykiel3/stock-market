@echo off
set PORT=%1
if "%PORT%"=="" set PORT=8080
echo Starting Stock Market on port %PORT%...
set PORT=%PORT% && docker compose up --build -d
echo Application available at http://localhost:%PORT%
