# Travel360 API - OpenAPI 3 Documentation Setup

## Overview

This project has been configured with SpringDoc OpenAPI 3 (version 2.8.8) to provide comprehensive API documentation and interactive testing capabilities through Swagger UI.

## Configuration Details

### Dependencies Added
- `springdoc-openapi-starter-webmvc-ui` (version 2.3.0) - Compatible version for Spring Boot 3.2.3

### Key Features Configured
- **JWT Authentication Support**: Bearer token authentication scheme configured
- **Interactive API Testing**: Swagger UI with "Try it out" functionality enabled
- **Comprehensive Documentation**: Detailed API descriptions, parameters, and response schemas
- **Security Integration**: Proper integration with Spring Security configuration
- **Organized Endpoints**: APIs grouped by functionality with descriptive tags

## Accessing the Documentation

### Swagger UI
- **URL**: `http://localhost:8080/api/swagger-ui.html`
- **Features**: Interactive API testing, request/response examples, authentication support

### OpenAPI JSON Specification
- **URL**: `http://localhost:8080/api/v3/api-docs`
- **Format**: JSON format OpenAPI 3.1 specification

### OpenAPI YAML Specification
- **URL**: `http://localhost:8080/api/v3/api-docs.yaml`
- **Format**: YAML format OpenAPI 3.1 specification

## API Groups

The API endpoints are organized into the following groups:

1. **Authentication** (`/api/auth/**`)
   - User login and registration
   - Role-based registration (operator, admin)
   - Public endpoints (no authentication required)

2. **Flight Management** (`/api/flights/**`)
   - Flight search (public)
   - Flight CRUD operations (authenticated)
   - Flight status management

3. **Booking Management** (`/api/bookings/**`)
   - Create and manage bookings
   - Generate booking documents
   - Booking search and filtering

4. **User Management** (`/api/users/**`)
   - User administration (admin only)
   - User profile management

## Authentication in Swagger UI

To test authenticated endpoints:

1. First, use the `/api/auth/login` endpoint to obtain a JWT token
2. Copy the token from the response
3. Click the "Authorize" button in Swagger UI
4. Enter `Bearer <your-token>` in the authorization field
5. Click "Authorize" to apply the token to all requests

## Configuration Properties

The following properties have been configured in `application.properties`:

```properties
# SpringDoc OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.try-it-out-enabled=true
springdoc.swagger-ui.operations-sorter=alpha
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.display-operation-id=false
springdoc.swagger-ui.default-models-expand-depth=1
springdoc.swagger-ui.default-model-expand-depth=1
springdoc.swagger-ui.doc-expansion=none
springdoc.swagger-ui.disable-swagger-default-url=true
```

## Security Configuration

The following endpoints are publicly accessible (no authentication required):
- `/api/auth/**` - Authentication endpoints
- `/api/airports` - Airport information
- `/api/flights/search` - Flight search
- `/api/flights/search/connecting` - Connecting flight search
- `/v3/api-docs/**` - OpenAPI documentation
- `/swagger-ui/**` - Swagger UI resources
- `/swagger-ui.html` - Swagger UI interface

## Development Notes

### Adding Documentation to New Endpoints

When adding new endpoints, use these annotations for comprehensive documentation:

```java
@Tag(name = "Group Name", description = "Group description")
public class YourController {

    @Operation(
        summary = "Brief description",
        description = "Detailed description",
        security = @SecurityRequirement(name = "bearerAuth") // For authenticated endpoints
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(schema = @Schema(implementation = YourDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/your-endpoint")
    public ResponseEntity<YourDto> yourMethod(
        @Parameter(description = "Parameter description") @RequestParam String param) {
        // Implementation
    }
}
```

### Best Practices

1. **Use descriptive summaries and descriptions** for all operations
2. **Document all parameters** with clear descriptions
3. **Specify response schemas** for successful responses
4. **Include error responses** (400, 401, 403, 404, 500)
5. **Group related endpoints** using `@Tag` annotations
6. **Use security requirements** for protected endpoints

## Troubleshooting

### Common Issues

1. **Swagger UI not loading**: Check that the application is running on the correct port (8080)
2. **Authentication not working**: Ensure JWT token is properly formatted with "Bearer " prefix
3. **Endpoints not showing**: Verify controller classes are in the component scan path
4. **CORS issues**: CORS is configured to allow all origins in development

### Logs

Enable debug logging for SpringDoc:
```properties
logging.level.org.springdoc=DEBUG
```

## Version Information

- **SpringDoc OpenAPI**: 2.3.0 (Compatible with Spring Boot 3.2.3)
- **Spring Boot**: 3.2.3
- **OpenAPI Specification**: 3.1
- **Swagger UI**: 5.x (bundled with SpringDoc)

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3 Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/) 