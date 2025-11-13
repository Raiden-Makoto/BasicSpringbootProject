# Spring Boot Lyrics Search Application

A Spring Boot application that searches for song lyrics using the Genius API.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Genius API Access Token (get one at https://genius.com/developers)

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

3. Configure your Genius API token:
   - Create a `.env` file in the project root (if it doesn't exist)
   - Add your Genius API access token:
     ```
     GENIUS_ACCESS_TOKEN=your_access_token_here
     ```
   - Get your access token from: https://genius.com/developers

4. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

You should see the search interface. Enter a song artist and title to search for lyrics!

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

