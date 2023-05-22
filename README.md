# Store Application

This project is a simple store application developed with Spring Boot, PostgreSQL and Docker.

## Prerequisites

- JDK 17 or later (If you want to run the application directly on your local machine without Docker)
- Docker
- Docker Compose

## Getting Started

1. Clone the repository: `git clone https://github.com/Fatush13/SimpleStore.git`
2. Navigate to the project directory: `cd store`
3. Build the project: `./mvnw package`
4. Start the services: `docker-compose --profile app up`
5. Run Liquibase migrations: `./mvnw liquibase:update`
6. Access the application: `http://localhost:8080`

## Using the Application

You can access the following services:

- Store service: `http://localhost:8080`
- PostgreSQL database: `localhost:5432`
- PgAdmin: `http://localhost:5050`
  - email: 'test@test.com'
  - password: test

## Application Profiles

You can use Docker Compose profiles to control which services to start:

- Database and PgAdmin: `docker-compose --profile database up`
- Store Service, Database and PgAdmin: `docker-compose --profile app up`

## Database Migration

We use Liquibase for database migration.

You can run migrations with the following command: `./mvnw liquibase:update`

## Swagger Documentation

You can access Swagger UI at: `http://localhost:8080/store/swagger-ui`
