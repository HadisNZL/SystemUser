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

port_pid() {
  local port="$1"
  command -v lsof >/dev/null 2>&1 && lsof -iTCP:"$port" -sTCP:LISTEN -t | head -n 1
}

write_port_pid() {
  local port="$1"
  local pid_file="$2"
  local pid
  pid="$(port_pid "$port")"
  if [[ -n "$pid" ]]; then
    echo "$pid" > "$pid_file"
  fi
}

wait_for_port() {
  local name="$1"
  local port="$2"
  local pid_file="$3"
  local launcher_pid="$4"
  local log_file="$5"

  for _ in {1..90}; do
    if is_port_used "$port"; then
      write_port_pid "$port" "$pid_file"
      echo "$name started, pid=$(cat "$pid_file")"
      return
    fi
    if ! ps -p "$launcher_pid" >/dev/null 2>&1; then
      echo "$name failed to start, check log=$log_file" >&2
      tail -n 80 "$log_file" >&2
      return 1
    fi
    sleep 1
  done

  echo "$name did not listen on port $port, check logs." >&2
  return 1
}

wait_for_tcp() {
  local name="$1"
  local port="$2"

  for _ in {1..90}; do
    if is_port_used "$port"; then
      echo "$name is ready on port $port"
      return
    fi
    sleep 1
  done

  echo "$name did not listen on port $port, check Docker logs." >&2
  return 1
}

wait_for_health() {
  local name="$1"
  local url="$2"

  if ! command -v curl >/dev/null 2>&1; then
    return
  fi

  for _ in {1..60}; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "$name health check passed"
      return
    fi
    sleep 1
  done

  echo "$name health check not ready, continue anyway. url=$url"
}

start_admin() {
  if is_running "$ADMIN_PID_FILE"; then
    echo "admin-system is already running, pid=$(cat "$ADMIN_PID_FILE")"
    return
  fi

  if is_port_used 8080; then
    [[ -f "$ADMIN_PID_FILE" ]] && write_port_pid 8080 "$ADMIN_PID_FILE"
    echo "admin-system port 8080 is already in use, pid=$(port_pid 8080)"
    return
  fi

  echo "Starting admin-system on 8080..."
  SPRING_PROFILES_ACTIVE=dev nohup ./mvnw spring-boot:run > "$ADMIN_LOG" 2>&1 &
  local launcher_pid=$!
  echo "admin-system log=$ADMIN_LOG"
  wait_for_port "admin-system" 8080 "$ADMIN_PID_FILE" "$launcher_pid" "$ADMIN_LOG"
  wait_for_health "admin-system" "http://localhost:8080/actuator/health"
}

start_auth() {
  if is_running "$AUTH_PID_FILE"; then
    echo "auth-service is already running, pid=$(cat "$AUTH_PID_FILE")"
    return
  fi

  if is_port_used 9101; then
    [[ -f "$AUTH_PID_FILE" ]] && write_port_pid 9101 "$AUTH_PID_FILE"
    echo "auth-service port 9101 is already in use, pid=$(port_pid 9101)"
    return
  fi

  echo "Starting auth-service on 9101..."
  SPRING_PROFILES_ACTIVE=dev nohup ./mvnw -f auth-service/pom.xml spring-boot:run > "$AUTH_LOG" 2>&1 &
  local launcher_pid=$!
  echo "auth-service log=$AUTH_LOG"
  wait_for_port "auth-service" 9101 "$AUTH_PID_FILE" "$launcher_pid" "$AUTH_LOG"
  wait_for_health "auth-service" "http://localhost:9101/actuator/health"
}

start_gateway() {
  if is_running "$GATEWAY_PID_FILE"; then
    echo "gateway-service is already running, pid=$(cat "$GATEWAY_PID_FILE")"
    return
  fi

  if is_port_used 9000; then
    [[ -f "$GATEWAY_PID_FILE" ]] && write_port_pid 9000 "$GATEWAY_PID_FILE"
    echo "gateway-service port 9000 is already in use, pid=$(port_pid 9000)"
    return
  fi

  echo "Starting gateway-service on 9000..."
  SPRING_PROFILES_ACTIVE=dev nohup ./mvnw -f gateway-service/pom.xml spring-boot:run > "$GATEWAY_LOG" 2>&1 &
  local launcher_pid=$!
  echo "gateway-service log=$GATEWAY_LOG"
  wait_for_port "gateway-service" 9000 "$GATEWAY_PID_FILE" "$launcher_pid" "$GATEWAY_LOG"
  wait_for_health "gateway-service" "http://localhost:9000/actuator/health"
}

stop_by_pid_file() {
  local name="$1"
  local pid_file="$2"
  local port="$3"

  if ! [[ -f "$pid_file" ]]; then
    if is_port_used "$port"; then
      echo "$name port $port is in use, but no pid file found. Stop it manually if it was started outside dev.sh."
    else
      echo "$name is not running"
    fi
    return
  fi

  local pid
  pid="$(cat "$pid_file")"
  if ! ps -p "$pid" >/dev/null 2>&1; then
    if is_port_used "$port"; then
      echo "$name pid file is stale, port $port is still in use by pid=$(port_pid "$port"). Stop it manually if needed."
    else
      echo "$name pid file is stale, removing"
    fi
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
    [[ -f "$pid_file" ]] && write_port_pid "$port" "$pid_file"
    echo "$name running on port $port, pid=$(port_pid "$port")"
  else
    echo "$name stopped"
  fi
}

wait_for_middleware() {
  wait_for_tcp "MySQL" 3306
  wait_for_tcp "Redis" 6379
  wait_for_tcp "RabbitMQ" 5672
  wait_for_tcp "Nacos HTTP" 8848
  wait_for_tcp "Nacos gRPC" 9848
  echo "Waiting a few seconds for Nacos service registry to finish initializing..."
  sleep 8
}

start_all() {
  prepare_dirs
  ./scripts/app.sh middleware
  wait_for_middleware
  start_admin
  start_auth
  start_gateway
}

stop_all() {
  stop_by_pid_file "gateway-service" "$GATEWAY_PID_FILE" 9000
  stop_by_pid_file "auth-service" "$AUTH_PID_FILE" 9101
  stop_by_pid_file "admin-system" "$ADMIN_PID_FILE" 8080
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
