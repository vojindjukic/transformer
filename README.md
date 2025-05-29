# Transformer App

---

## Overview

A Spring Boot application for performing configurable text transformations through a chain of operations. It ensures execution with timeouts and provides daily reports for all transformation activities.

---

## Technologies

* Java 21
* Spring Boot 3.5.0
* Spring Data JPA (H2 in-memory by default)
* Gradle

---

### Build & Run

```bash
# Clone the repository
git clone https://github.com/vojindjukic/transformer
cd transformer-app

# Build the application
./gradlew build

# Run the application (starts on port 8080 by default)
java -jar build/libs/TransformerApp-0.0.1-SNAPSHOT.jar
```

---
### API Endpoints
API documentation is available at `http://localhost:8080/swagger-ui/index.html`

### Configuration
Customize behavior in `src/main/resources/application.properties`:

- **`maxNumOfTransformersPerExecution`**  
  Max transformers per request. Default: `10`
- **`transformation.timeout.millis`**  
  Timeout in milliseconds. Default: `3000`
- **`transformation.maxInputLengthChars`**  
  Max input string length. Default: `15000`
- **`spring.datasource.*`**  
  Database settings (default: in-memory H2).  
