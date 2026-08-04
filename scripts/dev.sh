#!/usr/bin/env bash

# Local development helper: start middleware, admin-system, auth-service and gateway-service.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

RUN_DIR="$ROOT_DIR/.run"
LOG_DIR="$ROOT_DIR/logs/dev"

ADMIN_PID_FILE="$RUN_DIR/admin-system.pid"
AUTH_PID_FILE="$RUN_DIR/auth-service.pid"
GATEWAY_PID_FILE="$RUN_DIR/gateway-service.pid"
ADMIN_LOG="$LOG_DIR/admin-system.log"
AUTH_LOG="$LOG_DIR/auth-service.log"
GATEWAY_LOG="$LOG_DIR/gateway-service.log"

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/dev.sh start     Start middleware, admin-system, auth-service and gateway-service
  ./scripts/dev.sh stop      Stop admin-system, auth-service and gateway-service
  ./scripts/dev.sh restart   Restart local Spring Boot services
  ./scripts/dev.sh status    Show local service status
  ./scripts/dev.sh logs      Follow admin-system, auth-service and gateway-service logs

Notes:
  - Middleware still uses Docker Compose.
  - admin-system runs on 8080, auth-service runs on 9101, gateway-service runs on 9000.
USAGE
}

prepare_dirs() {
  mkdir -p "$RUN_DIR" "$LOG_DIR"
}

is_running() {
  local pid_file="$1"
  [[ -f "$pid_file" ]] && ps -p "$(cat "$pid_file")" >/dev/null 2>&1
}

is_port_used() {
  local port="$1"
  command -v lsof >/dev/null 2>&1 && lsof -iTCP:"$port" -sTCP:LISTEN -t >/dev/null 2>&1
}

start_admin() {
  if is_running "$ADMIN_PID_FILE"; then
    echo "admin-system is already running, pid=$(cat "$ADMIN_PID_FILE")"
    return
  fi

  if is_port_used 8080; then
    echo "admin-system port 8080 is already in use. If IDEA started it, keep using that process."
    return
  fi

  echo "Starting admin-system on 8080..."
  SPRING_PROFILES_ACTIVE=dev nohup ./mvnw spring-boot:run > "$ADMIN_LOG" 2>&1 &
  echo $! > "$ADMIN_PID_FILE"
  echo "admin-system pid=$(cat "$ADMIN_PID_FILE"), log=$ADMIN_LOG"
}

start_auth() {
  if is_running "$AUTH_PID_FILE"; then
    echo "auth-service is already running, pid=$(cat "$AUTH_PID_FILE")"
    return
  fi

  if is_port_used 9101; then
    echo "auth-service port 9101 is already in use. If IDEA started it, keep using that process."
    return
  fi

  echo "Starting auth-service on 9101..."
  SPRING_PROFILES_ACTIVE=dev nohup ./mvnw -f auth-service/pom.xml spring-boot:run > "$AUTH_LOG" 2>&1 &
  echo $! > "$AUTH_PID_FILE"
  echo "auth-service pid=$(cat "$AUTH_PID_FILE"), log=$AUTH_LOG"
}

start_gateway() {
  if is_running "$GATEWAY_PID_FILE"; then
    echo "gateway-service is already running, pid=$(cat "$GATEWAY_PID_FILE")"
    return
  fi

  if is_port_used 9000; then
    echo "gateway-service port 9000 is already in use. If IDEA started it, keep using that process."
    return
  fi

  echo "Starting gateway-service on 9000..."
  SPRING_PROFILES_ACTIVE=dev nohup ./mvnw -f gateway-service/pom.xml spring-boot:run > "$GATEWAY_LOG" 2>&1 &
  echo $! > "$GATEWAY_PID_FILE"
  echo "gateway-service pid=$(cat "$GATEWAY_PID_FILE"), log=$GATEWAY_LOG"
}

stop_by_pid_file() {
  local name="$1"
  local pid_file="$2"

  if ! [[ -f "$pid_file" ]]; then
    echo "$name is not running"
    return
  fi

  local pid
  pid="$(cat "$pid_file")"
  if ! ps -p "$pid" >/dev/null 2>&1; then
    echo "$name pid file is stale, removing"
    rm -f "$pid_file"
    return
  fi

  echo "Stopping $name, pid=$pid..."
  kill "$pid"
  for _ in {1..20}; do
    if ! ps -p "$pid" >/dev/null 2>&1; then
      rm -f "$pid_file"
      echo "$name stopped"
      return
    fi
    sleep 0.5
  done

  echo "$name is still stopping, check pid=$pid if needed"
}

status_by_pid_file() {
  local name="$1"
  local pid_file="$2"
  local port="$3"

  if is_running "$pid_file"; then
    echo "$name running, pid=$(cat "$pid_file")"
  elif is_port_used "$port"; then
    echo "$name port $port is in use, probably started outside dev.sh"
  else
    echo "$name stopped"
  fi
}

start_all() {
  prepare_dirs
  ./scripts/app.sh middleware
  start_admin
  start_auth
  start_gateway
}

stop_all() {
  stop_by_pid_file "gateway-service" "$GATEWAY_PID_FILE"
  stop_by_pid_file "auth-service" "$AUTH_PID_FILE"
  stop_by_pid_file "admin-system" "$ADMIN_PID_FILE"
}

case "${1:-}" in
  start)
    start_all
    ;;
  stop)
    stop_all
    ;;
  restart)
    stop_all
    start_all
    ;;
  status)
    status_by_pid_file "admin-system" "$ADMIN_PID_FILE" 8080
    status_by_pid_file "auth-service" "$AUTH_PID_FILE" 9101
    status_by_pid_file "gateway-service" "$GATEWAY_PID_FILE" 9000
    ;;
  logs)
    prepare_dirs
    touch "$ADMIN_LOG" "$AUTH_LOG" "$GATEWAY_LOG"
    tail -f "$ADMIN_LOG" "$AUTH_LOG" "$GATEWAY_LOG"
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
