#!/bin/bash
# Setup SQL Server trong Docker cho Sneakery project
# Usage: ./setup-sqlserver-docker.sh

set -e

PASSWORD="Sneakery@2024"
CONTAINER_NAME="sneakery-sqlserver"
IMAGE="mcr.microsoft.com/mssql/server:2022-latest"

echo "🐳 Setting up SQL Server in Docker..."
echo ""

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed!"
    echo "Please install Docker first: https://docs.docker.com/engine/install/"
    exit 1
fi

# Stop container nếu đã có
echo "🛑 Stopping existing container (if any)..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# Check if port 1433 is already in use by another container
PORT_CONFLICT=$(docker ps --format "{{.Names}}" | while read name; do
    docker port "$name" 2>/dev/null | grep -q ":1433" && echo "$name"
done | tr '\n' ' ')

if [ ! -z "$PORT_CONFLICT" ]; then
    echo "⚠️  Warning: Port 1433 is already in use by container(s): $PORT_CONFLICT"
    echo "   Stopping conflicting container(s)..."
    for container in $PORT_CONFLICT; do
        docker stop "$container" 2>/dev/null || true
        docker rm "$container" 2>/dev/null || true
    done
    sleep 2
fi

# Pull image nếu chưa có
echo "📥 Pulling SQL Server image..."
docker pull $IMAGE

# Chạy container
echo "🚀 Starting SQL Server container..."
docker run -e "ACCEPT_EULA=Y" \
           -e "MSSQL_SA_PASSWORD=$PASSWORD" \
           -p 0.0.0.0:1433:1433 \
           --name $CONTAINER_NAME \
           --restart unless-stopped \
           -d $IMAGE

# Đợi SQL Server khởi động
echo "⏳ Waiting for SQL Server to start (30 seconds)..."
sleep 30

# Kiểm tra
if docker ps | grep -q $CONTAINER_NAME; then
    echo ""
    echo "✅ SQL Server is running!"
    echo ""
    echo "=========================================="
    echo "📝 Connection info for Navicat (Windows):"
    echo "=========================================="
    echo "   Host: localhost"
    echo "   Port: 1433"
    echo "   Username: sa"
    echo "   Password: $PASSWORD"
    echo ""
    echo "=========================================="
    echo "📝 Connection info for Backend (WSL):"
    echo "=========================================="
    echo "   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=sneakery_db;encrypt=false;trustServerCertificate=true"
    echo "   spring.datasource.username=sa"
    echo "   spring.datasource.password=$PASSWORD"
    echo ""
    echo "=========================================="
    echo "🔍 Test connection:"
    echo "=========================================="
    echo "   From Windows PowerShell:"
    echo "   Test-NetConnection -ComputerName localhost -Port 1433"
    echo ""
    echo "   From WSL:"
    echo "   docker exec -it $CONTAINER_NAME /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P '$PASSWORD' -Q 'SELECT @@VERSION'"
    echo ""
else
    echo "❌ Failed to start SQL Server"
    echo ""
    echo "Checking logs..."
    docker logs $CONTAINER_NAME
    exit 1
fi




