# Travel360 Airline Ticket Booking System API

This project is a RESTful API backend for an Airline Ticket Booking System, developed using Spring Boot, Spring Security, and PostgreSQL.

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 14 or higher

## Setup Instructions

### Database Setup

1. Install PostgreSQL if not already installed
2. Create a database named `travel360db`
```sql
CREATE DATABASE travel360db;
```
3. Update the database connection settings in `src/main/resources/application.properties` if needed

### Building and Running the Application

1. Clone the repository
```bash
git clone https://github.com/yourusername/Travel360.API.git
cd Travel360.API
```

2. Build the application
```bash
mvn clean package
```

3. Run the application
```bash
java -jar target/api-0.0.1-SNAPSHOT.jar
```
Alternatively, use Maven to run:
```bash
mvn spring-boot:run
```

4. The API will be available at `http://localhost:8080/api`

## API Documentation

### Authentication

- `POST /api/auth/login` - Authenticate user and get JWT token
- `POST /api/auth/register` - Register a new customer
- `POST /api/auth/register/operator` - Register a new operator (Admin only)
- `POST /api/auth/register/admin` - Register a new administrator (Admin only)

### User Management

- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID (Admin only)
- `PUT /api/users/{id}` - Update user (Admin only)
- `DELETE /api/users/{id}` - Deactivate user (Admin only)

### Flight Management

- `GET /api/flights` - Get all flights
- `GET /api/flights/{id}` - Get flight by ID
- `POST /api/flights` - Create a new flight (Operator/Admin only)
- `PUT /api/flights/{id}` - Update flight (Operator/Admin only)
- `DELETE /api/flights/{id}` - Delete flight (Admin only)
- `GET /api/flights/search` - Search for available flights

### Booking Management

- `POST /api/bookings` - Create a new booking
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/reference/{reference}` - Get booking by reference number
- `GET /api/bookings/user` - Get current user's bookings
- `PUT /api/bookings/{id}/status` - Update booking status (Operator/Admin only)
- `DELETE /api/bookings/{id}` - Cancel booking
- `GET /api/bookings/flight/{flightId}` - Get bookings by flight (Operator/Admin only)
- `GET /api/bookings/documents/{id}/confirmation` - Generate booking confirmation
- `GET /api/bookings/documents/flight/{id}/manifest` - Generate passenger manifest (Operator/Admin only)

## Security

The API uses JWT (JSON Web Tokens) for authentication. To access protected endpoints, include the JWT token in the Authorization header:

```
Authorization: Bearer your_jwt_token
```

## Project Structure

- `src/main/java/com/travel360/api/model` - Entity models
- `src/main/java/com/travel360/api/repository` - Data repositories
- `src/main/java/com/travel360/api/service` - Business logic services
- `src/main/java/com/travel360/api/controller` - REST controllers
- `src/main/java/com/travel360/api/dto` - Data Transfer Objects
- `src/main/java/com/travel360/api/security` - Security configuration
- `src/main/java/com/travel360/api/config` - Application configuration
- `src/main/resources` - Application properties and static resources 