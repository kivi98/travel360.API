package com.travel360.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${travel360.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${travel360.cors.allowed-origins-prod:}")
    private String allowedOriginsProd;

    @Value("${travel360.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${travel360.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${travel360.cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${travel360.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${travel360.cors.max-age:3600}")
    private long maxAge;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins based on environment
        List<String> origins = getOriginsForEnvironment();
        configuration.setAllowedOrigins(origins);
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        
        // Set allowed headers
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        
        // Set exposed headers
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        
        // Set credentials
        configuration.setAllowCredentials(allowCredentials);
        
        // Set max age for preflight requests
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> getOriginsForEnvironment() {
        if ("prod".equals(activeProfile) || "production".equals(activeProfile)) {
            // Production environment - use production origins
            if (allowedOriginsProd != null && !allowedOriginsProd.trim().isEmpty()) {
                return Arrays.asList(allowedOriginsProd.split(","));
            }
        }
        
        // Development environment or fallback
        return Arrays.asList(allowedOrigins.split(","));
    }

    /**
     * Get current CORS configuration for debugging/monitoring
     */
    public CorsConfigurationInfo getCorsInfo() {
        return new CorsConfigurationInfo(
            getOriginsForEnvironment(),
            Arrays.asList(allowedMethods.split(",")),
            Arrays.asList(allowedHeaders.split(",")),
            Arrays.asList(exposedHeaders.split(",")),
            allowCredentials,
            maxAge,
            activeProfile
        );
    }

    /**
     * Inner class for CORS configuration information
     */
    public static class CorsConfigurationInfo {
        private final List<String> allowedOrigins;
        private final List<String> allowedMethods;
        private final List<String> allowedHeaders;
        private final List<String> exposedHeaders;
        private final boolean allowCredentials;
        private final long maxAge;
        private final String environment;

        public CorsConfigurationInfo(List<String> allowedOrigins, List<String> allowedMethods,
                                   List<String> allowedHeaders, List<String> exposedHeaders,
                                   boolean allowCredentials, long maxAge, String environment) {
            this.allowedOrigins = allowedOrigins;
            this.allowedMethods = allowedMethods;
            this.allowedHeaders = allowedHeaders;
            this.exposedHeaders = exposedHeaders;
            this.allowCredentials = allowCredentials;
            this.maxAge = maxAge;
            this.environment = environment;
        }

        // Getters
        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public List<String> getAllowedMethods() { return allowedMethods; }
        public List<String> getAllowedHeaders() { return allowedHeaders; }
        public List<String> getExposedHeaders() { return exposedHeaders; }
        public boolean isAllowCredentials() { return allowCredentials; }
        public long getMaxAge() { return maxAge; }
        public String getEnvironment() { return environment; }
    }
} 