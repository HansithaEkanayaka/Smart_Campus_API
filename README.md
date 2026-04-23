# Smart Campus API

**Module:** 5COSC022W – Client-Server Architectures
**Institution:** University of Westminster
**Tech Stack:** JAX-RS (Jersey 2.x), Apache Tomcat 9, Java 11
**Base URL:**

```
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## Overview

This project is a RESTful API designed to manage a Smart Campus system. It handles:

* Room management
* Sensor registration and monitoring
* Historical sensor readings

All data is stored **in-memory** using thread-safe collections (`ConcurrentHashMap`, `ArrayList`). No database is used.

---

## API Resources

| Resource  | Endpoint                        | Description                 |
| --------- | ------------------------------- | --------------------------- |
| Discovery | `GET /api/v1`                   | API metadata and navigation |
| Rooms     | `/api/v1/rooms`                 | Manage campus rooms         |
| Sensors   | `/api/v1/sensors`               | Manage IoT sensors          |
| Readings  | `/api/v1/sensors/{id}/readings` | Sensor history              |

---

## Project Structure

```
SmartCampusAPI/
├── pom.xml
└── src/main/
    ├── java/com/smartcampus/
    │   ├── SmartCampusApplication.java
    │   ├── model/
    │   ├── dao/
    │   ├── resource/
    │   ├── exception/
    │   └── filter/
    └── webapp/WEB-INF/web.xml
```

---

## Setup & Execution

### Prerequisites

* Java JDK 11+
* Maven 3.6+
* Apache Tomcat 9
* NetBeans (recommended)

---

### Build

```bash
mvn clean package
```

Output:

```
target/SmartCampusAPI.war
```

---

### Deploy

**Option 1 — NetBeans**

* Add Tomcat server
* Right-click project → Run

**Option 2 — Manual**

```bash
copy target\SmartCampusAPI.war <TOMCAT_HOME>\webapps\
<TOMCAT_HOME>\bin\startup.bat
```

---

### Verify

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

---

## API Endpoints

### Rooms

| Method | Endpoint      | Description       |
| ------ | ------------- | ----------------- |
| GET    | `/rooms`      | List rooms        |
| POST   | `/rooms`      | Create room       |
| GET    | `/rooms/{id}` | Get room          |
| PUT    | `/rooms/{id}` | Update            |
| DELETE | `/rooms/{id}` | Delete (if empty) |

---

### Sensors

| Method | Endpoint        | Description             |
| ------ | --------------- | ----------------------- |
| GET    | `/sensors`      | List (filter: `?type=`) |
| POST   | `/sensors`      | Create                  |
| GET    | `/sensors/{id}` | Get                     |
| PUT    | `/sensors/{id}` | Update                  |
| DELETE | `/sensors/{id}` | Delete                  |

---

### Sensor Readings

| Method | Endpoint                       | Description    |
| ------ | ------------------------------ | -------------- |
| GET    | `/sensors/{id}/readings`       | History        |
| POST   | `/sensors/{id}/readings`       | Add reading    |
| GET    | `/sensors/{id}/readings/{rid}` | Single reading |

---

## HTTP Status Codes

| Code | Meaning           |
| ---- | ----------------- |
| 200  | Success           |
| 201  | Created           |
| 204  | Deleted           |
| 400  | Bad request       |
| 403  | Forbidden         |
| 404  | Not found         |
| 409  | Conflict          |
| 415  | Unsupported type  |
| 422  | Invalid reference |
| 500  | Server error      |

---

## Example Requests

### Create Room

```bash
curl -X POST /rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library","capacity":50}'
```

---

### Create Sensor

```bash
curl -X POST /sensors \
-d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","roomId":"LIB-301"}'
```

---

### Add Reading

```bash
curl -X POST /sensors/CO2-001/readings \
-d '{"value":425.3}'
```

---

## Key Design Concepts

### JAX-RS Lifecycle

* Resources are **request-scoped**
* Shared data handled via **static DAO + ConcurrentHashMap**

---

### HATEOAS

* Responses include navigation links
* API becomes self-discoverable
* Reduces client-server coupling

---

### Filtering Design

* Uses query params (`?type=CO2`)
* Supports optional and multiple filters
* Follows REST conventions

---

### Sub-Resource Pattern

* `/sensors/{id}/readings` handled separately
* Improves modularity and maintainability

---

### DELETE Behavior

* First call → `204`
* Second call → `404`
* State unchanged → acceptable REST behavior

---

### Security Considerations

* Stack traces **not exposed**
* Errors sanitized via ExceptionMapper
* Prevents:

  * Information leakage
  * Attack surface exposure

---

### Logging Strategy

* Implemented via **JAX-RS Filters**
* Benefits:

  * Centralized logging
  * Cleaner business logic
  * Easier maintenance

---

## Conclusion

This API demonstrates:

* RESTful design best practices
* Thread-safe in-memory data handling
* Clean architecture with separation of concerns
* Production-style error handling and logging
