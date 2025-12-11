# Medilabo - Gateway

## Description

- Spring Boot Gateway that proxies requests to microservices and provides security

## Stack

- Java 21
- Spring Cloud Gateway
- Spring Security

## Important Info

- default port: `9001`
- incoming requests (from frontend to gateway) must be prefixed with predicate `/api/<service endpoint>/**`:
    - e.g. `/api/patients/**`
- outgoing requests (from gateway to microservices) are automatically proxied to
  `<microserviceUrl>:<port>/<service endpoint>` with `StripPrefix=1`
    - e.g. `/api/patients/**` (incoming) is converted to `/patients/**` (outgoing)
- CORS protection enabled. Adjust allowed clients/methods in `application.yml`.
- default user credentials: `user` : `user`
- exposed management endpoints: `health`

Configuration (file)

- `gateway/src/main/resources/application.yml` contains ports, routes and CORS/security settings.

Run backend (gateway)

1. Change directory to `gateway`.
2. Build: `./mvnw clean package`
3. Run: `./mvnw spring-boot:run`
4. Access: `http://localhost:9001/api/patients/...` — proxied to `http://localhost:9101`

Tests

- run `./mvnw test` (unitary tests)
- run `./mvnw verify` (unitary + integration tests)

Notes

- Ensure the microservice are running on port before using.
- Adjust `application.yml` to modify CORS origins, credentials, or routes.
- Do not expose default credentials in production.

Useful file

- `gateway/src/main/java/com/medilabo/gateway/GatewayApplication.java` — Spring Boot entry point.
