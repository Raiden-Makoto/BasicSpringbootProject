# Spring Boot Hello World Application

A simple "Hello World" Spring Boot application.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Running the Application

1. Build the project:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR file:
   ```bash
   java -jar target/demo-0.0.1-SNAPSHOT.jar
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

You should see "Hello World!" displayed in your browser.

## Project Structure

```
.
├── pom.xml
├── README.md
└── src
    └── main
        └── java
            └── com
                └── example
                    └── demo
                        ├── Application.java
                        └── HelloController.java
```

