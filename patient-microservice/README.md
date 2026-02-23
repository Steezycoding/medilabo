# Patient Microservice - Medilabo

## 📌 Overview

This microservice manages patient personal data (CRUD) and exposes a REST API.  
It is backed by a MySQL database for persistence.

## 🧰 Tech Stack

- Language: **Java** 21
- Framework: **Spring Boot** (Web, Data JPA, Security)
- Database: **MySQL**
- Build: **Maven** (MVN Wrapper included)
- Miscellaneous: **Lombok** for boilerplate reduction, **Jackson** for JSON processing
- Testing: **JUnit** 5, **H2**

## Architecture

```mermaid
C4Component
    title Components - Patient Microservice
    Container_Ext(gateway, "API Gateway", "Spring Cloud Gateway", "Routes traffic.")

    Container_Boundary(patientMsvc, "Patient Microservice") {
        Component(patientController, "PatientController", "Spring MVC REST Controller", "Exposes patient endpoints.")
        Component(patientDto, "PatientDTO", "DTO", "Represents patient data for API requests/responses.")
        Component(patientService, "PatientService", "Service", "Business logic for patient management.")
        Component(patientEntity, "Patient", "Entity", "Represents patient data from repository.")
        Component(patientRepository, "PatientRepository", "Spring Data JPA Repository", "CRUD operations on patient data.")
    }

    Container_Boundary(patientDbContainer, "Patient Database") {
        ComponentDb(patientDb, "MySQL Database", "Stores patient data")
    }

    BiRel(gateway, patientController, "Routes requests", "HTTP/REST")
    Rel(patientController, patientService, "Delegates patient management", "In-process")
    Rel(patientService, patientRepository, "CRUD operations", "In-process")
    BiRel(patientRepository, patientDb, "Reads/writes patient data", "JDBC")
    Rel(patientService, patientDto, "Converts to/from DTO", "In-process")
    Rel(patientDto, patientController, "Returns patient data", "In-process")
    Rel(patientEntity, patientService, "Receives patient data", "In-process")
    Rel(patientRepository, patientEntity, "Returns patient data", "In-process")
    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

## ▶️ Running app

### Local (development)

Please refer to the _Running services locally (development)_ from [CONTRIBUTING.md](../docs/CONTRIBUTING.md) guide for
running the microservice locally.

### Docker

From the **repository root**, you can build and run the microservice using Docker Compose:

```bash
  docker compose up -d patient-microservice
```
