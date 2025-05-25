# Travel360 API - CORS Configuration Guide

## 🔒 Security Overview

The CORS (Cross-Origin Resource Sharing) configuration has been completely overhauled to replace the **insecure** wildcard (`*`) configuration with proper environment-specific origin whitelisting.

### ⚠️ Previous Security Issue
```java
// DANGEROUS - Allows ALL origins
configuration.setAllowedOrigins(Arrays.asList("*"));
```

### ✅ New Secure Configuration
```java
// SECURE - Specific origins only
configuration.setAllowedOrigins(getOriginsForEnvironment());
```

## 📁 Configuration Structure

```
src/main/
├── java/com/travel360/api/config/
│   ├── CorsConfig.java              # Main CORS configuration
│   └── SecurityConfig.java         # Security with CORS integration
├── java/com/travel360/api/controller/
│   └── CorsInfoController.java     # CORS debugging endpoint
└── resources/
    ├── application.properties       # Base configuration
    ├── application-dev.properties   # Development overrides
    └── application-prod.properties  # Production overrides
```

## 🛠️ Configuration Properties

### Base Configuration (`application.properties`)
```properties
# CORS Configuration
travel360.cors.allowed-origins=http://localhost:3000,http://localhost:3001,http://localhost:4200,http://127.0.0.1:3000
travel360.cors.allowed-origins-prod=https://app.travel360.com,https://admin.travel360.com
travel360.cors.allowed-methods=GET,POST,PUT,PATCH,DELETE,OPTIONS
travel360.cors.allowed-headers=authorization,content-type,x-auth-token,x-requested-with,accept,origin
travel360.cors.exposed-headers=x-auth-token,location
travel360.cors.allow-credentials=true
travel360.cors.max-age=3600
```

### Development Environment (`application-dev.properties`)
```properties
# Development - Local frontend URLs
travel360.cors.allowed-origins=http://localhost:3000,http://localhost:3001,http://localhost:4200,http://localhost:8080,http://127.0.0.1:3000,http://127.0.0.1:4200
travel360.cors.allow-credentials=true
```

### Production Environment (`application-prod.properties`)
```properties
# Production - Actual domain names only
travel360.cors.allowed-origins-prod=https://app.travel360.com,https://admin.travel360.com,https://www.travel360.com
travel360.cors.allow-credentials=true
travel360.cors.max-age=7200
```

## 🚀 Running Different Environments

### Development Mode (Default)
```bash
mvn spring-boot:run
# OR
java -jar target/app.jar
```

### Development Mode (Explicit)
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
# OR
java -jar target/app.jar --spring.profiles.active=dev
```

### Production Mode
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
# OR
java -jar target/app.jar --spring.profiles.active=prod
```

## 🔧 Customizing CORS for Your Frontend

### React (Create React App) - Port 3000
```properties
travel360.cors.allowed-origins=http://localhost:3000
```

### Angular Development Server - Port 4200
```properties
travel360.cors.allowed-origins=http://localhost:4200
```

### Vue.js Development Server - Port 8080
```properties
travel360.cors.allowed-origins=http://localhost:8080
```

### Multiple Frontend Apps
```properties
travel360.cors.allowed-origins=http://localhost:3000,http://localhost:4200,http://localhost:8080
```

### Custom Development Ports
```properties
travel360.cors.allowed-origins=http://localhost:3001,http://localhost:5000,http://127.0.0.1:3000
```

## 🌐 Production Setup

### 1. Update Production Origins
Edit `application-prod.properties`:
```properties
# Replace with your actual production domains
travel360.cors.allowed-origins-prod=https://yourdomain.com,https://app.yourdomain.com,https://admin.yourdomain.com
```

### 2. Deploy with Production Profile
```bash
java -jar travel360-api.jar --spring.profiles.active=prod
```

### 3. Environment Variables (Recommended for Production)
```bash
export SPRING_PROFILES_ACTIVE=prod
export TRAVEL360_CORS_ALLOWED_ORIGINS_PROD=https://yourdomain.com,https://app.yourdomain.com
java -jar travel360-api.jar
```

## 🐛 Debugging CORS Issues

### 1. Check Current Configuration
Use the CORS info endpoint (requires OPERATOR/ADMINISTRATOR role):
```bash
# Get JWT token first
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Check CORS configuration
curl -X GET http://localhost:8080/api/system/cors-info \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Browser Network Tab
Check for preflight OPTIONS requests and responses:
- Look for `Access-Control-Allow-Origin` header
- Verify `Access-Control-Allow-Methods` includes your HTTP method
- Check `Access-Control-Allow-Headers` for required headers

### 3. Common CORS Error Messages

**Error**: "Access to fetch at 'http://localhost:8080/api/...' from origin 'http://localhost:3000' has been blocked by CORS policy"

**Solution**: Add `http://localhost:3000` to allowed origins.

**Error**: "Request header 'authorization' is not allowed by Access-Control-Allow-Headers"

**Solution**: Add `authorization` to `travel360.cors.allowed-headers`.

### 4. Enable CORS Debug Logging
Add to your properties file:
```properties
logging.level.org.springframework.web.cors=DEBUG
```

## 🔐 Security Best Practices

### ✅ DO
- **Use specific origins only** - Never use `*` in production
- **Use HTTPS in production** - Always use secure protocols
- **Limit allowed headers** - Only include necessary headers
- **Set appropriate max-age** - Balance between performance and security
- **Enable credentials only when needed** - Required for JWT authentication
- **Different configurations per environment** - Stricter rules for production

### ❌ DON'T
- **Never use `*` for allowed origins** in production
- **Don't expose internal URLs** in production origins
- **Don't use HTTP in production** - Always use HTTPS
- **Don't allow unnecessary headers** - Minimize attack surface
- **Don't set overly long max-age** - Consider security vs. performance

## 🧪 Testing CORS Configuration

### Frontend JavaScript Test
```javascript
// Test CORS from browser console
fetch('http://localhost:8080/api/airports', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  },
})
.then(response => response.json())
.then(data => console.log('CORS working:', data))
.catch(error => console.error('CORS error:', error));

// Test with authentication
fetch('http://localhost:8080/api/flights', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer YOUR_JWT_TOKEN'
  },
})
.then(response => response.json())
.then(data => console.log('Authenticated CORS working:', data))
.catch(error => console.error('Authenticated CORS error:', error));
```

### Command Line Test (Simulating Browser)
```bash
# Test preflight request
curl -X OPTIONS http://localhost:8080/api/flights \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: authorization,content-type" \
  -v

# Test actual request
curl -X GET http://localhost:8080/api/airports \
  -H "Origin: http://localhost:3000" \
  -v
```

## 🔄 Configuration Updates Without Restart

For development, you can use Spring Boot DevTools or Spring Cloud Config to update CORS configuration without restarting the application. However, for security reasons, production deployments should require restarts for configuration changes.

## 📞 Support

If you encounter CORS issues:

1. **Check the browser console** for specific error messages
2. **Verify your frontend URL** is in the allowed origins list
3. **Use the CORS info endpoint** to debug current configuration
4. **Check the application logs** with CORS debug logging enabled
5. **Test with curl** to isolate frontend vs. backend issues

## 🚨 Emergency Debugging (Development Only)

**NEVER USE IN PRODUCTION** - For extreme debugging cases in development only:

```properties
# EMERGENCY BYPASS - DEVELOPMENT ONLY
travel360.cors.allowed-origins=*
logging.level.org.springframework.web.cors=TRACE
```

Remember to revert this immediately after debugging! 