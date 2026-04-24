# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W Client-Server Architectures — University of Westminster  
**Technology:** JAX-RS (Jersey 2.x) · Apache Tomcat 9 · Java 11 · Maven  
**Base URL:** `http://localhost:8080/SmartCampusAPI/api/v1`

---

## API Overview

A RESTful API for managing the university's Smart Campus infrastructure — rooms and IoT sensors. All data is stored in-memory using `ConcurrentHashMap` and `ArrayList`. No database is used.

### Resource Collections

| Resource | Base Path | Description |
|---|---|---|
| Discovery | `GET /api/v1` | API metadata and navigable resource links |
| Rooms | `/api/v1/rooms` | Campus room management |
| Sensors | `/api/v1/sensors` | IoT sensor registration and management |
| Readings | `/api/v1/sensors/{id}/readings` | Historical sensor readings (sub-resource) |

### Architecture Layers

```
HTTP Client
    |
JAX-RS Resource (RoomResource / SensorResource / SensorReadingResource)
    |
DAO Layer (RoomDAO / SensorDAO / SensorReadingDAO)
    |
In-Memory Store (static ConcurrentHashMap)
```

Cross-cutting concerns are handled by:
- `ApiLoggingFilter` — logs every inbound request and outbound response
- `ExceptionMapper` classes — translate Java exceptions into structured JSON HTTP responses

---

## Project Structure

```
SmartCampusAPI/
pom.xml
README.md
src/main/
    java/com/smartcampus/
        SmartCampusApplication.java
        model/
            Room.java
            Sensor.java
            SensorReading.java
            ErrorMessage.java
        dao/
            RoomDAO.java
            SensorDAO.java
            SensorReadingDAO.java
        resource/
            DiscoveryResource.java
            RoomResource.java
            SensorResource.java
            SensorReadingResource.java
        exception/
            RoomNotEmptyException.java
            RoomNotEmptyExceptionMapper.java
            LinkedResourceNotFoundException.java
            LinkedResourceNotFoundExceptionMapper.java
            SensorUnavailableException.java
            SensorUnavailableExceptionMapper.java
            ResourceNotFoundException.java
            ResourceNotFoundExceptionMapper.java
            GenericExceptionMapper.java
        filter/
            ApiLoggingFilter.java
    webapp/WEB-INF/
        web.xml
```

---

## Build & Run Instructions

### Prerequisites
- Java JDK 11 or higher
- Apache Maven 3.6+
- Apache Tomcat 9.x

### Step 1 — Open in NetBeans
`File -> Open Project -> select the SmartCampusAPI folder`

### Step 2 — Build
Right-click the project -> **Clean and Build**

Or from a terminal:
```bash
mvn clean package
```
This produces `target/SmartCampusAPI.war`.

### Step 3 — Add Tomcat Server in NetBeans
1. Go to `Services -> Servers`
2. Right-click -> `Add Server` -> choose **Apache Tomcat**
3. Set the server location to your Tomcat 9 directory
4. Click Finish

### Step 4 — Run
Right-click project -> **Run**

Or deploy manually:
```bash
copy target\SmartCampusAPI.war C:\path\to\tomcat9\webapps\
C:\path\to\tomcat9\bin\startup.bat
```

### Step 5 — Verify
```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```
Expected: JSON discovery response with version, contact, and resource links.

---

## API Endpoint Reference

### Discovery
| Method | Path | Response |
|---|---|---|
| GET | `/api/v1` | API metadata and resource links |

### Rooms
| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/v1/rooms` | List all rooms | 200 |
| POST | `/api/v1/rooms` | Create a new room | 201 |
| GET | `/api/v1/rooms/{roomId}` | Get room by ID | 200 |
| DELETE | `/api/v1/rooms/{roomId}` | Delete room (blocked if sensors present) | 204 |

### Sensors
| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/v1/sensors` | List all sensors (optional `?type=` filter) | 200 |
| POST | `/api/v1/sensors` | Register a new sensor | 201 |
| GET | `/api/v1/sensors/{sensorId}` | Get sensor by ID | 200 |
| DELETE | `/api/v1/sensors/{sensorId}` | Remove a sensor | 204 |

### Sensor Readings (Sub-Resource)
| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get full reading history | 200 |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a new reading | 201 |
| GET | `/api/v1/sensors/{sensorId}/readings/{readingId}` | Get a specific reading | 200 |

### HTTP Status Codes
| Code | Meaning |
|---|---|
| 200 | OK |
| 201 | Created (includes Location header) |
| 204 | No Content (DELETE success) |
| 400 | Bad Request — missing required fields |
| 403 | Forbidden — sensor not in ACTIVE state |
| 404 | Not Found — resource does not exist |
| 409 | Conflict — room has sensors / duplicate ID |
| 415 | Unsupported Media Type — wrong Content-Type |
| 422 | Unprocessable Entity — referenced roomId does not exist |
| 500 | Internal Server Error |

### Standard Error Response
All errors return a consistent JSON structure:
```json
{
  "status": 409,
  "code": "ERR_ROOM_NOT_EMPTY",
  "message": "Room 'LIB-301' cannot be deleted because it still has sensors assigned to it."
}
```

---

## Sample curl Commands

### 1. Discovery endpoint
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1 -H "Accept: application/json"
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":50}"
```

### 3. Get all Rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Accept: application/json"
```

### 4. Get a specific Room
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 -H "Accept: application/json"
```

### 5. Create a Sensor linked to a Room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":412.5,\"roomId\":\"LIB-301\"}"
```

### 6. Get all Sensors (unfiltered)
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Accept: application/json"
```

### 7. Filter Sensors by type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2" -H "Accept: application/json"
```

### 8. Post a Sensor Reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":425.3}"
```

### 9. Get Sensor Reading History
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings -H "Accept: application/json"
```

### 10. Attempt to Delete a Room with Active Sensors (expects 409 Conflict)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 -H "Accept: application/json"
```

### 11. Create Sensor with non-existent roomId (expects 422)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-999\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.1,\"roomId\":\"GHOST-ROOM\"}"
```

### 12. Register a MAINTENANCE sensor and attempt a reading (expects 403)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"BROKEN-001\",\"type\":\"CO2\",\"status\":\"MAINTENANCE\",\"currentValue\":0.0,\"roomId\":\"LIB-301\"}"

curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/BROKEN-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":430.0}"
```

### 13. Get a Room that does not exist (expects 404)
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/DOES-NOT-EXIST -H "Accept: application/json"
```

### 14. Delete a Sensor
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001 -H "Accept: application/json"
```

### 15. Delete a Room after all sensors removed (expects 204)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 -H "Accept: application/json"
```
