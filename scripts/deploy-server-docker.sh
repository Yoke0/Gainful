#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR/.."

# Read actual version from gradle.properties (fallback: latest)
VERSION=$(grep '^VERSION_NAME=' "$PROJECT_DIR/gradle.properties" | cut -d'=' -f2)
APP_VERSION="${VERSION:-latest}"
export APP_VERSION

echo "=== Gainful Server Docker Deploy ==="
echo "Image tag: gainful-server:${APP_VERSION}"

# --- 1. Build fat jar (Dockerfile depends on this artifact) ---
echo ""
echo ">>> Building server fat jar (:server:shadowJar)..."
cd "$PROJECT_DIR"
./gradlew :server:shadowJar

if [ ! -f "server/build/libs/server.jar" ]; then
    echo "ERROR: server/build/libs/server.jar not found after build"
    exit 1
fi

# --- 2. Build image & start services (postgres + server) ---
echo ""
echo ">>> Building image and starting services (docker compose up -d --build)..."
docker compose up -d --build

# --- 3. Wait for server to be ready ---
echo ""
echo ">>> Waiting for server to be ready..."
SERVER_PORT=${SERVER_PORT:-8081}
for i in $(seq 1 30); do
    if curl -s -o /dev/null "http://localhost:${SERVER_PORT}/swagger"; then
        echo ">>> Server is up at http://localhost:${SERVER_PORT} (attempt $i)"
        break
    fi
    if [ "$i" -eq 30 ]; then
        echo "ERROR: Server did not become ready in time. Check logs: docker compose logs server"
        exit 1
    fi
    sleep 2
done

# --- 4. Summary ---
echo ""
echo "=== Deploy Complete ==="
docker compose ps
echo ""
echo "Server:    http://localhost:${SERVER_PORT}"
echo "Swagger:   http://localhost:${SERVER_PORT}/swagger"
echo "Postgres:  localhost:5433"
echo ""
echo "Logs:      docker compose logs -f server"
echo "Stop:      docker compose down"
