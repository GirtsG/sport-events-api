# sport-events-api

A CRUD REST API for managing sport events with business rules and validations.

## Features

- Create a sport event
- Retrieve sport events with optional filters by status and sport type
- Retrieve a sport event by ID
- Change the status of a sport event with validation rules

## Technology Stack

- **Backend**: Java, Spring Boot
- **Database**: R2DBC with H2
- **Testing**: JUnit 5, Mockito, Reactor Test
- **Build Tool**: Maven

## Getting Started

### Prerequisites
- JDK 21\+
- Maven 3.8\+

### Running the Application
1. Clone the repository:
   ```
   git clone https://github.com/GirtsG/sport-events-api.git
   cd sport-events-api
   ```

2. Start the application:
   ```
   ./mvnw spring-boot:run
   ```

3. API Root: http://localhost:8080/api/

### H2 Console Access
The application uses an in-memory H2 database. You can access the H2 console for debugging purposes:

- Navigate to http://localhost:8080/h2-console
- JDBC URL: ```jdbc:h2:mem:sporteventsdb```
- Username: ```sa```
- Password: _(leave empty)_

### Testing API with HTTP Client
The project includes ```api-tests.http```, which contains REST API examples for quick testing. You can run it using IntelliJ IDEA's HTTP client:

1. Open the file: ```src/test/resources/api-tests.http```
2. Run individual requests or the whole file directly from IntelliJ.

### API Endpoints

#### Sport Types
- **Create Sport Type**:  
  ```POST /api/sport-types```  
  Request Body:  
  ```json
  {
  "name": "Basketball"
  }
  ```

- **Get All Sport Types**:  
  ```GET /api/sport-types```

- **Get Sport Type by ID**:  
  ```GET /api/sport-types/{id}```

#### Sport Events
- **Create Event**:  
  ```POST /api/events```  
  Request Body:  
  ```json
  {
  "name": "Champions League Final",
  "sportTypeId": 1,
  "startTime": "2024-11-20T18:00:00"
  }
  ```

- **Get All Events**:  
  ```GET /api/events?status=ACTIVE&sportTypeId=1```

- **Get Event by ID**:  
  ```GET /api/events/{id}```

- **Change Event Status**:  
  ```PATCH /api/events/{id}/status?newStatus=ACTIVE```

### Tests
Run tests with:
```./mvnw test```
