# Medical Note Microservice - Medilabo

## 📌 Overview

This microservice stores and retrieves medical notes associated with medical notes data (CRUD) and exposes a REST API.  
It is backed by a MongoDB database for persistence.

## 🧰 Tech Stack

- Language: **Java** 21
- Framework: **Spring Boot** (Web, Security), **Spring Cloud OpenFeign** for inter-service calls
- Build: **Maven** (MVN Wrapper included)
- Miscellaneous: **Lombok** for boilerplate reduction, **Jackson** for JSON processing
- Testing: **JUnit** 5

## Architecture

```mermaid
C4Component
    title Components - Medical Notes Microservice
    Container_Ext(gateway, "API Gateway", "Spring Cloud Gateway", "Routes traffic.")

    Container_Boundary(notesMsvc, "MedicalNote Microservice") {
        Component(notesController, "MedicalNoteController", "Spring MVC REST Controller", "Exposes medical notes endpoints.")
        Component(notesDto, "MedicalNoteDTO", "DTO", "Represents medical note for API requests/responses.")
        Component(notesService, "MedicalNoteService", "Service", "Business logic for medical notes management.")
        Component(notesEntity, "MedicalNote", "Entity", "Represents medical note data from repository.")
        Component(notesRepository, "MedicalNoteRepository", "Spring Data MongoDB Repository", "CRUD operations on medical notes data.")
    }

    Container_Boundary(meidcalNoteDbContainer, "MedicalNote Database") {
        ComponentDb(medicalNoteDb, "MongoDB Database", "Stores medical note data")
    }

    BiRel(gateway, notesController, "Routes requests", "HTTP/REST")
    Rel(notesController, notesService, "Delegates medical notes management", "In-process")
    Rel(notesService, notesRepository, "CRUD operations", "In-process")
    BiRel(notesRepository, medicalNoteDb, "Reads/writes medical notes data", "JDBC")
    Rel(notesService, notesDto, "Converts to/from DTO", "In-process")
    Rel(notesDto, notesController, "Returns medical notes data", "In-process")
    Rel(notesEntity, notesService, "Receives medical notes data", "In-process")
    Rel(notesRepository, notesEntity, "Returns medical notes data", "In-process")
    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

## ▶️ Running app

### Local (development)

Please refer to the _Running services locally (development)_ from [CONTRIBUTING.md](../docs/CONTRIBUTING.md) guide for
running the microservice locally.

### Docker

From the **repository root**, you can build and run the microservice using Docker Compose:

```bash
  docker compose up -d medical-note-microservice
```
