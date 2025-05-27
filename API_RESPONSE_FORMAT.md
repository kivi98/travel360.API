# Travel360.API - Standardized Response Format

## Overview
All API endpoints in the Travel360.API now return responses in a standardized format that matches the frontend's expected interface. This ensures consistent data handling across the entire application.

## Response Interface

### TypeScript Interface (Frontend)
```typescript
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errors?: string[];
}
```

### Java Class (Backend)
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private List<String> errors;
    // ... methods
}
```

## Response Examples

### ✅ Success Response with Data
```json
{
  "success": true,
  "data": {
    "id": 1,
    "flightNumber": "TV101",
    "departureTime": "2025-06-01T10:00:00"
  },
  "message": "Flight retrieved successfully"
}
```

### ✅ Success Response with Message Only
```json
{
  "success": true,
  "message": "Flight deleted successfully"
}
```

### ❌ Error Response (Validation)
```json
{
  "success": false,
  "errors": [
    "seatClass: must not be null",
    "originAirportId: must not be null",
    "destinationAirportId: must not be null"
  ]
}
```

### ❌ Error Response (Business Logic)
```json
{
  "success": false,
  "message": "Authentication failed",
  "errors": [
    "Invalid username or password"
  ]
}
```

### ❌ Error Response (Not Found)
```json
{
  "success": false,
  "errors": [
    "Flight not found with ID: 123"
  ]
}
```

## Updated Controllers

All controllers have been updated to use the standardized `ApiResponse<T>` format:

### 1. AuthController
- **POST** `/api/auth/login` → `ApiResponse<LoginResponse>`
- **POST** `/api/auth/register` → `ApiResponse<User>`
- **POST** `/api/auth/register/operator` → `ApiResponse<User>`
- **POST** `/api/auth/register/admin` → `ApiResponse<User>`

### 2. FlightController
- **GET** `/api/flights` → `ApiResponse<List<FlightDto>>`
- **GET** `/api/flights/{id}` → `ApiResponse<Flight>`
- **POST** `/api/flights` → `ApiResponse<Flight>`
- **PUT** `/api/flights/{id}` → `ApiResponse<Flight>`
- **DELETE** `/api/flights/{id}` → `ApiResponse<Void>`
- **POST** `/api/flights/search` → `ApiResponse<List<FlightDto>>`
- **POST** `/api/flights/search/connecting` → `ApiResponse<List<List<FlightDto>>>`
- **PUT** `/api/flights/{id}/status` → `ApiResponse<Flight>`
- **GET** `/api/flights/status/{status}` → `ApiResponse<List<Flight>>`

### 3. BookingController
- **POST** `/api/bookings` → `ApiResponse<BookingResponse>`
- **GET** `/api/bookings/{id}` → `ApiResponse<BookingResponse>`
- **GET** `/api/bookings/user` → `ApiResponse<List<BookingResponse>>`
- **GET** `/api/bookings` → `ApiResponse<List<BookingResponse>>`
- **PUT** `/api/bookings/{id}/status` → `ApiResponse<BookingResponse>`
- **DELETE** `/api/bookings/{id}` → `ApiResponse<Void>`

### 4. UserController
- **GET** `/api/users` → `ApiResponse<List<User>>`
- **GET** `/api/users/{id}` → `ApiResponse<User>`
- **PUT** `/api/users/{id}` → `ApiResponse<User>`
- **DELETE** `/api/users/{id}` → `ApiResponse<Void>`

### 5. CorsInfoController (Debug)
- **GET** `/api/cors/test` → `ApiResponse<Map<String, Object>>`
- **GET** `/api/cors/headers` → `ApiResponse<Map<String, Object>>`

## Global Exception Handling

The `GlobalExceptionHandler` automatically converts all exceptions into the standardized format:

### Validation Errors
- **MethodArgumentNotValidException** → 400 with field validation errors
- **IllegalArgumentException** → 400 with custom error message

### Authentication/Authorization Errors
- **BadCredentialsException** → 401 "Invalid username or password"
- **AuthenticationException** → 401 "Authentication required"
- **AccessDeniedException** → 403 "Access denied"

### Server Errors
- **RuntimeException** → 500 "An unexpected error occurred"
- **Exception** → 500 "An unexpected error occurred"

## Implementation Details

### ApiResponse Static Methods
```java
// Success responses
ApiResponse.success(data)                    // With data only
ApiResponse.success(data, message)           // With data and message
ApiResponse.success(message)                 // Message only (no data)

// Error responses
ApiResponse.error(errorMessage)              // Single error
ApiResponse.error(List<String> errors)       // Multiple errors
ApiResponse.error(errorMessage, message)     // Error with context message
```

### Controller Pattern
```java
@PostMapping
public ResponseEntity<ApiResponse<SomeDto>> createSomething(@RequestBody SomeRequest request) {
    try {
        SomeDto result = service.createSomething(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Created successfully"));
    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage(), "Creation failed"));
    }
}
```

## Frontend Integration

### Success Response Handling
```typescript
interface FlightSearchResponse {
  success: boolean;
  data: FlightDto[];
  message?: string;
}

// Handle response
if (response.success) {
  setFlights(response.data);
  showSuccessMessage(response.message);
} else {
  showErrors(response.errors);
}
```

### Error Response Handling
```typescript
// All error responses follow the same format
if (!response.success && response.errors) {
  response.errors.forEach(error => showErrorToast(error));
}
```

## Migration Notes

### What Changed
1. **Response Format**: All endpoints now return `ApiResponse<T>` wrapper
2. **Error Handling**: Consistent error format across all endpoints
3. **Success Messages**: Descriptive success messages included
4. **Swagger Documentation**: Updated to reflect new response schemas

### Frontend Updates Required
- Update all API service calls to handle new response format
- Extract data from `response.data` instead of direct response
- Handle error arrays from `response.errors`
- Display success messages from `response.message`

### Testing
✅ **Validation Errors**: Properly formatted with field-specific messages
✅ **CORS Support**: All responses include proper CORS headers
✅ **Swagger UI**: Updated documentation reflects new format
✅ **Exception Handling**: Global handler converts all errors consistently

## Benefits

1. **Consistency**: All API responses follow the same structure
2. **Type Safety**: Frontend can rely on consistent response interface
3. **Error Handling**: Standardized error format simplifies frontend error handling
4. **Success Messages**: Better user experience with descriptive messages
5. **Documentation**: Clear API documentation with response examples
6. **Maintainability**: Easier to add new endpoints following the established pattern

## Example Usage

### Login Request
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "username": "admin",
    "email": "admin@travel360.com",
    "roles": ["ROLE_ADMINISTRATOR"]
  },
  "message": "Login successful"
}
```

### Flight Search Request
```bash
curl -X POST "http://localhost:8080/api/flights/search" \
  -H "Content-Type: application/json" \
  -d '{"originAirportId":1,"destinationAirportId":2,"departureDate":"2025-06-01","seatClass":"ECONOMY","passengers":1}'
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "flightNumber": "TV101",
      "departureTime": "2025-06-01T10:00:00",
      "arrivalTime": "2025-06-01T13:30:00",
      "price": 299.99
    }
  ],
  "message": "Found 1 matching flights"
}
``` 