#!/bin/bash
PORT=${1:-8080}
echo "Starting Stock Market on port $PORT..."
PORT=$PORT docker compose up --build -d
echo "Application available at http://localhost:$PORT"
