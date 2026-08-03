#!/usr/bin/env bash

# Project operation helper for local Docker Compose deployment.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

APP_SERVICE="admin-system"
GATEWAY_SERVICE="gateway-service"
MIDDLEWARE_SERVICES="mysql redis rabbitmq nacos"

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/app.sh middleware   Start MySQL, Redis, RabbitMQ and Nacos only
  ./scripts/app.sh build        Build app/gateway jars and Docker images
  ./scripts/app.sh start        Start app, gateway and middleware
  ./scripts/app.sh stop         Stop all compose services
  ./scripts/app.sh restart      Restart app and middleware
  ./scripts/app.sh logs         Follow app logs
  ./scripts/app.sh ps           Show compose service status

Notes:
  - Do not use "docker compose down -v" unless you want to remove data volumes.
  - Run this script from any directory; it will switch to project root.
USAGE
}

require_docker() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "Docker is not installed or not in PATH." >&2
    exit 1
  fi
}

build_app() {
  ./mvnw clean package -DskipTests
  docker compose build "$APP_SERVICE"
}

build_gateway() {
  ./mvnw -f gateway-service/pom.xml clean package -DskipTests
  docker compose build "$GATEWAY_SERVICE"
}

case "${1:-}" in
  middleware)
    require_docker
    docker compose up -d $MIDDLEWARE_SERVICES
    ;;
  build)
    require_docker
    build_app
    build_gateway
    ;;
  start)
    require_docker
    build_app
    build_gateway
    docker compose up -d
    ;;
  stop)
    require_docker
    docker compose stop
    ;;
  restart)
    require_docker
    docker compose restart
    ;;
  logs)
    require_docker
    docker compose logs -f "$APP_SERVICE"
    ;;
  ps)
    require_docker
    docker compose ps
    ;;
  -h|--help|help|"")
    usage
    ;;
  *)
    echo "Unknown command: $1" >&2
    usage
    exit 1
    ;;
esac
