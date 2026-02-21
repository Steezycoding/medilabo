# Contributing and development environment setup

This document explains how to install and use the development environment for the Medilabo (RET2D) project. It is
intended for contributors who want to run services locally, run tests and prepare Pull Requests.

## Purpose

Provide clear, reproducible instructions to:

- install the prerequisites
- run the microservices individually or with Docker Compose
- run unit and integration tests

## 📋 Prerequisites

- **Java** 21+
- **Maven** (each microservice includes the `mvnw` / `mvnw.cmd` wrapper)
- **Node.js** 24+ with **npm**
- **Docker & Docker Compose**
    - Medical Note microservice integration tests use _Testcontainers_
      _Docker_ set up **is required** for running these tests.
- **Git**

## 🗂️ Repository layout

Key folders in the repository:

- `frontend/` : Angular SPA
- `gateway/` : API Gateway
- `patient-microservice/` : Patient microservice (must be linked to MySQL database)
- `medical-note-microservice/` : Medical Note microservice (must be linked to MongoDB database)
- `risk-evaluator-microservice/` : Risk evaluator (depends on _patient_ and _medical-note_ microservices)

## ▶️ Running services locally (development)

For local development and debugging you can run each microservice with the included Maven wrapper.

Examples (bash):

```bash
    # API Gateway
    cd gateway
    ./mvnw spring-boot:run
    
    # Patient microservice
    cd ../patient-microservice
    ./mvnw spring-boot:run
    
    # Medical Note microservice
    cd ../medical-note-microservice
    ./mvnw spring-boot:run
    
    # Risk evaluator microservice
    cd ../risk-evaluator-microservice
    ./mvnw spring-boot:run
```

As microservices are protected from external requests, if you need to request a microservice directly (e.g. for testing)
you may need to set the active Spring profile to `dev` to bypass security:

```
    cd <microservice-folder>
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Frontend (Angular):

```bash
    cd frontend
    npm install
    npm start      # or: npm run start
    # run frontend tests
    npm test
```

### 🔌 Ports and configuration

Each service exposes its port in its configuration (typically `application.yml`) or via Docker. If needed, set or
override environment variables for database connections, JWT secrets, and other configuration values.

All services serve on http://localhost with the following default ports:

| Service                     | Port |
|:----------------------------|------|
| Frontend                    | 8080 |
| API Gateway                 | 9001 |
| Patient microservice        | 9101 |
| Medical Note microservice   | 9102 |
| Risk evaluator microservice | 9103 |

The default credentials for authentication are:

- Username: `user`
- Password: `user`

## 🗃️ Local databases

### Manual setup

**Patient microservice**  
Needs to be plugged to a **MySQL** database

**Medical Note microservice**  
Needs to be plugged to a **MongoDB** database

Please refer to the `application.yml` of each microservice for the expected database connection parameters (e.g. host,
port, username, password).
Or change these parameters if needed.

### Docker setup

To facilitate development and testing, you can run the required databases using "Databases" part of
the [Docker Compose configuration](../docker-compose.yml) included in the project.

From the project root, execute:

```bash
    docker compose up -d patient-ms-db medical-note-ms-db
```

When using Docker Compose these DB containers are typically provisioned and initialized automatically.

## ⚙️ Environment variables

Make sure any required environment variables (e.g. DB URLs, secrets) are set before starting services.

An [.env](../.env) file is available at the project root and is used by default by Docker Compose.

You can also set environment variables directly in your shell, IDE run configuration or CI/CD.

## 🧪 Running tests

**Backend (using Maven wrapper):**

```bash   
    # Run tests for a specific module (example)
    cd patient-microservice
    ./mvnw verify
```

**Frontend:**

```bash
    cd frontend
    npm test
```

**Integration tests**

For integration tests that depend on external resources (especially databases), ensure Docker is running or the required
services
are available.

## 🚨 Troubleshooting

- **Logs**: use `docker compose logs -f` or the console output from `./mvnw spring-boot:run`.
- **Port conflicts**: verify the required ports are not already in use.
- **Missing environment variables**: check `.env` file and `application.yml` from each microservice.

