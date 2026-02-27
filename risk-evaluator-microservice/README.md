# Risk Evaluator Microservice - Medilabo

## 📌 Overview

The Risk Evaluator microservice calculates patient risk scores by aggregating patient personal data and medical notes.  
It fetches patient data and medical notes from the other microservices and computes risk levels based on triggers.  
It exposes HTTP endpoints for on-demand evaluation.

## 🧰 Tech Stack

- Language: **Java** 21
- Framework: **Spring Boot** (Web, Security), **Spring Cloud OpenFeign** for inter-service calls
- Build: **Maven** (MVN Wrapper included)
- Miscellaneous: **Lombok** for boilerplate reduction, **Jackson** for JSON processing
- Testing: **JUnit** 5

## 🏗️ Architecture

```mermaid
C4Component
    title Components - RiskEvaluator Microservice
    Container_Ext(gateway, "API Gateway", "Spring Cloud Gateway", "Routes traffic.")

    Container_Boundary(riskSvc, "RiskEvaluator Microservice") {
        Component(patientClient, "PatientMicroserviceProxy", "Feign HTTP Client", "Fetches patient data from Patient Service.")
        Component(riskController, "RiskEvaluatorController", "Spring MVC REST Controller", "Exposes risk evaluator endpoints.")
        Component(notesClient, "MedicalNoteMicroserviceProxy", "Feign HTTP Client", "Fetches notes from MedicalNotes Service.")
        Component(patientBean, "PatientBean", "Bean/DTO", "")
        Component(riskService, "RiskEvaluatorService", "Service", "Calculates risk level using rules/thresholds.")
        Component(notesBean, "MedicalNoteBean", "Bean/DTO", "")
        Component(triggerTerms, "TriggerTerm", "Enum", "Triggers, keywords.")
    }

    BiRel(gateway, riskController, "Routes requests", "HTTP/REST")
    BiRel(riskController, riskService, "Delegates evaluation", "In-process")
    Rel(riskService, patientClient, "Fetches patient data", "In-process")
    BiRel(patientClient, gateway, "Calls Patient Microservice", "HTTP/REST")
    Rel(patientClient, patientBean, "Returns patient data", "In-process")
    Rel(patientBean, riskService, "Returns patient DTO", "In-process")
    Rel(riskService, notesClient, "Fetches medical notes", "In-process")
    BiRel(notesClient, gateway, "Calls MedicalNotes Microservice", "HTTP/REST")
    Rel(notesClient, notesBean, "Returns medical notes", "In-process")
    Rel(notesBean, riskService, "Returns medical notes DTO", "In-process")
    Rel(riskService, triggerTerms, "Gets all trigger words from fetched notes", "In-process")
    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

## ▶️ Running app

### Local (development)

Please refer to the _Running services locally (development)_ from [CONTRIBUTING.md](../docs/CONTRIBUTING.md) guide for
running the microservice locally.

### Docker

From the **repository root**, you can build and run the microservice using Docker Compose:

```bash
  docker compose up -d risk-evaluator-microservice
```

## 🧪 Testing app

To run tests for this microservice, you can use the following command from the **module root**:

```bash
  ./mvnw clean verify site
```

This will execute all tests and generate a test report and project information in the `target/site` directory.  
You can open the `index.html` file in that directory to view these information.
