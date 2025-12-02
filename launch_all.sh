#!/bin/bash

# Exit on error
set -e

echo "Building project..."
mvn clean package -DskipTests

echo "Generating classpath..."
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
CP=$(cat cp.txt):target/classes

# Function to kill all child processes on exit
cleanup() {
    echo "Stopping servers..."
    # Kill all background jobs
    kill $(jobs -p) 2>/dev/null || true
}
trap cleanup EXIT INT TERM

echo "Starting API Gateway (Port 8080)..."
java -cp "$CP" fr.unice.polytech.APIGateWay.ApiGateWayServer &

echo "Starting Restaurant Service (Port 8081)..."
java -cp "$CP" fr.unice.polytech.services.RestaurantService &

echo "Starting Student Account Service (Port 8082)..."
java -cp "$CP" fr.unice.polytech.services.StudentAccountService &

echo "Starting Order Service (Port 8083)..."
java -cp "$CP" fr.unice.polytech.services.OrderService &

echo "Starting Frontend (Angular)..."
(cd front/SophiaTech-Eats && npm start) &

echo "All servers started. Press Ctrl+C to stop."
wait
