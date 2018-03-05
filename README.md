
Introduction
---
The hedgehog application is a simple dropwizard application.
It provides two endpoints. The first endpoint loads the last trades from `https://liqui.io` and saves data to a database.
The second endpoint retrieves saved data from the database.

Note: The application uses `H2` database in both persistent and in-memory modes.
The persistent mode is used for running the application. The database file path is `.target/hedgehog`.
The in-memory mode is used for testing purposes.

How to start the hedgehog application
---

1. Run `mvn clean install` to build your application
1. Run migration with `java -jar target/hedgehog-project-1.0-SNAPSHOT.jar db migrate config.yml`
1. Start application with `java -jar target/hedgehog-project-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8180`

Health Check
---

To see your applications health enter url `http://localhost:8181/healthcheck`

Swagger
---

To see the application endpoints enter url `http://localhost:8180/swagger/`
