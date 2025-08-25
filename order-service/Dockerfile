FROM eclipse-temurin:17-jdk-alpine

# Install curl
RUN apk add --no-cache curl

WORKDIR /app

COPY target/order-service-*.jar app.jar
COPY wait-for-service-discovery.sh wait-for-service-discovery.sh

RUN chmod +x wait-for-service-discovery.sh

ENTRYPOINT ["./wait-for-service-discovery.sh"]
