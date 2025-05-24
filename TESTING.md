# Testing Guide for Travel360 API

This document provides guidance on how to test the Travel360 Airline Ticket Booking System API.

## Types of Tests

The project includes several types of tests:

1. **Unit Tests** - Test individual components in isolation by mocking dependencies
2. **Integration Tests** - Test the interaction between components
3. **API Tests** - Test the REST endpoints by making HTTP requests

## Test Configuration

The tests use an H2 in-memory database instead of PostgreSQL. This configuration is defined in `src/test/resources/application-test.properties`.

To run tests with this configuration, use the `@ActiveProfiles("test")` annotation on your test classes.

## Running Tests

### Using Maven

To run all tests:

```bash
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=AuthControllerTest
```

### Using an IDE

Most IDEs (IntelliJ IDEA, Eclipse, VS Code) allow you to run tests by right-clicking on a test class or method and selecting "Run" or "Debug".

## Testing Endpoints

### Authentication

The following tests cover authentication endpoints:

- `AuthControllerTest` - Unit tests for the Auth Controller
- `AuthControllerIntegrationTest` - Integration tests for authentication flow

### Booking

- `BookingControllerTest` - Tests booking creation, retrieval, and management

### Flights

- `FlightControllerTest` - Tests flight search and management

## Test Structure

Each test class follows this structure:

1. **Setup** - Configure test data and mock dependencies
2. **Test Methods** - Each test method focuses on a specific scenario
3. **Helper Methods** - Methods to create test data or verify results

## Test Coverage

The tests cover:

- Successful responses
- Error handling
- Authentication and authorization
- Business logic validation

## Adding New Tests

When adding new features, follow these guidelines for testing:

1. Create unit tests for the controller methods
2. Add integration tests for critical user flows
3. Test both successful and error scenarios
4. Test authorization rules where applicable

## Mocking Dependencies

For unit tests, dependencies are mocked using Mockito:

```java
@MockBean
private UserService userService;
```

## Testing Security

Security tests use Spring Security's test utilities:

```java
@WithMockUser(roles = "ADMINISTRATOR")
public void testAdminOnlyEndpoint() {
    // Test code here
}
```

## Testing Controllers

Controller tests use Spring's MockMvc to simulate HTTP requests:

```java
mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
``` 