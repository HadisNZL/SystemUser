#!/usr/bin/env bash

# Project operation helper for Docker Compose deployment.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

AUTH_SERVICE="auth-service"
SYSTEM_SERVICE="system-service"
FILE_SERVICE="file-service"
LOG_SERVICE="log-service"
GATEWAY_SERVICE="gateway-service"
APPLICATION_SERVICES="$AUTH_SERVICE $SYSTEM_SERVICE $FILE_SERVICE $LOG_SERVICE $GATEWAY_SERVICE"
MIDDLEWARE_SERVICES="mysql redis rabbitmq nacos"

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/app.sh middleware   Start MySQL, Redis, RabbitMQ and Nacos only
  ./scripts/app.sh build        Build all service jars and Docker images
  ./scripts/app.sh start        Build and start all services
  ./scripts/app.sh stop         Stop all compose services
  ./scripts/app.sh restart      Restart all compose services
  ./scripts/app.sh logs         Follow application service logs
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

build_services() {
  ./mvnw clean package -DskipTests
  docker compose build $APPLICATION_SERVICES
}

case "${1:-}" in
  middleware)
    require_docker
    docker compose up -d --remove-orphans $MIDDLEWARE_SERVICES
    ;;
  build)
    require_docker
    build_services
    ;;
  start)
    require_docker
    build_services
    docker compose up -d --remove-orphans
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
    docker compose logs -f $APPLICATION_SERVICES
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
