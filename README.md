# Bajaj Finserv Health - Qualifier 1

Spring Boot application for the Bajaj Finserv Health hiring qualifier task.

## How to Build

```bash
mvn clean package
```

## How to Run

```bash
java -jar target/webhook-solver-0.0.1-SNAPSHOT.jar
```

The app runs the full workflow automatically on startup — no manual trigger needed.

To disable the workflow for local testing or dry runs, set:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--app.workflow.enabled=false
```

You can also override it with an environment variable:

```bash
APP_WORKFLOW_ENABLED=false mvn spring-boot:run
```
