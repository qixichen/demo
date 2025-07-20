# Service Agent by SAP

A simple Spring Boot service for mocking API requests and managing jobs.

---

## Demo
[Watch the Demo Video](https://go.screenpal.com/watch/cTiYj5nIVNV)

---

## 🧰 Features

- **Mock API**: Echoes back HTTP method, headers, and body for GET / POST / PUT / DELETE
- **Job Management**: Create, retrieve, and update jobs
- **Swagger UI**: Interactive API documentation using Springdoc OpenAPI (Swagger 3)

---

## 🛠 Tech Stack

- Java 23+
- Spring Boot 3.x
- Spring Web
- Springdoc OpenAPI (Swagger 3)
- Maven

---

## 📁 Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/              # Java source code
│   │   └── resources/         # Config files (application.properties)
│   └── test/                  # Unit tests
│
├── public/                    # Static frontend files (served under src/main/resources/static)
│   ├── index.html             # Main HTML file
│   ├── styles.css             # Custom CSS styles
│   └── app.js                 # Frontend JavaScript logic
│
└── README.md                  # Project documentation

```

---

## 🚀 Quick Start

```bash
Clone the project
git clone https://github.com/your-username/service-agent.git cd service-agent
Build and run
mvn clean install mvn spring-boot:run
```
The service will be available at:  
👉 [http://localhost:8080](http://localhost:8080)

---

## 🌐 API Endpoints

| Method | URL              | Description                     |
|--------|------------------|---------------------------------|
| POST   | `/mock/api/echo` | Echo back POST request          |
| GET    | `/mock/api/echo` | Echo back GET request           |
| PUT    | `/mock/api/echo` | Echo back PUT request           |
| DELETE | `/mock/api/echo` | Echo back DELETE request        |
| POST   | `/jobs`          | Create a new job                |
| GET    | `/jobs`          | Get all jobs                    |
| PUT    | `/jobs/{id}`     | Update job by ID                |

---

## 📚 API Documentation

Swagger UI is available at:

👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

