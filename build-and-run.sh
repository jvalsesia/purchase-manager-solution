#!/bin/bash

set -e

echo "Building Spring Boot Docker images..."

echo "Building purchase-authorization-ms..."
cd purchase-authorization-ms
./mvnw spring-boot:build-image
cd ..

echo "Building purchase-converter-ms..."
cd purchase-converter-ms
./mvnw spring-boot:build-image
cd ..

echo "Starting services with Docker Compose..."
docker compose -f compose.yaml up
