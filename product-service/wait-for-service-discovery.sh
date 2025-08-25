#!/bin/sh

echo "Waiting for service discovery at service-discovery:8761..."
until curl -s http://service-discovery:8761/actuator/health | grep '"status":"UP"' > /dev/null; do
  echo "Service discovery not ready, waiting 3 seconds..."
  sleep 3
done

echo "All dependencies are up. Starting user-service..."
exec java -jar app.jar
