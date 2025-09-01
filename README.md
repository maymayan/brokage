# Brokerage Spring Boot Application

This project is a **Brokerage Firm Application** built with:

- Spring Boot 3
- Java 17
- Gradle
- H2 In-Memory Database
- JWT Authentication
- Swagger UI for API testing

---

## Example Users

When the application starts, two example users are created automatically:

| Username | Password | Role  | Initial TRY Balance |
|----------|----------|-------|---------------------|
| janedoe  | 123      | admin | 10,000              |
| johndoe  | 123      | user  | 10,000              |

---

## Prerequisites

- Java 17
- Gradle
- Docker (optional)
- Web browser or Postman for testing

---

## 1. Build the Application with Gradle

Run the following command in the project root:

```bash
./gradlew clean build
```

- The `.jar` file will be generated in the `build/libs/` directory (e.g., `brokerage-app-0.0.1-SNAPSHOT.jar`).

---

## 2. Run Locally

```bash
java -jar build/libs/brokerage-app-0.0.1-SNAPSHOT.jar
```

- The application runs on **localhost:8080**.

---

## 3. Run with Docker

1. Build the Docker image:

```bash
docker build -t brokerage-app .
```

2. Run the container:

```bash
docker run -p 8080:8080 brokerage-app
```

- The application will now be accessible at `http://localhost:8080`.

---

## 4. Swagger UI

- URL: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Usage:

1. Use the `/auth/login` endpoint to obtain a JWT token.  
   Example request body:

```json
{
  "username": "janedoe",
  "password": "123"
}
```

2. Copy the token from the response.
3. Click **Authorize** (top-right) in Swagger UI and paste:

```
Bearer <JWT_TOKEN>
```

4. Now you can call other endpoints with proper authentication.

---

## 5. H2 Database Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `mma`
- Password: `111`

---

## 6. Notes

- Note: App uses in-memory database so the data exists only while the application is running.
- Java 17 and Gradle are required to build and run the project.
