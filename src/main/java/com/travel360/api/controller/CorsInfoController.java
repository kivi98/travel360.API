package com.travel360.api.controller;

import com.travel360.api.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cors")
@Tag(name = "CORS Debug", description = "CORS debugging and information endpoints")
public class CorsInfoController {

    @GetMapping("/test")
    @Operation(
        summary = "Test CORS configuration",
        description = "Test endpoint to verify CORS configuration is working properly"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "CORS test successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> testCors(HttpServletRequest request) {
        Map<String, Object> corsInfo = new HashMap<>();
        corsInfo.put("message", "CORS is working correctly!");
        corsInfo.put("timestamp", System.currentTimeMillis());
        corsInfo.put("origin", request.getHeader("Origin"));
        corsInfo.put("method", request.getMethod());
        corsInfo.put("remoteAddr", request.getRemoteAddr());
        corsInfo.put("userAgent", request.getHeader("User-Agent"));
        
        return ResponseEntity.ok(ApiResponse.success(corsInfo, "CORS configuration test completed"));
    }

    @GetMapping("/headers")
    @Operation(
        summary = "Get request headers",
        description = "Get all request headers for CORS debugging"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHeaders(HttpServletRequest request) {
        Map<String, Object> headerInfo = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        
        request.getHeaderNames().asIterator().forEachRemaining(name -> 
            headers.put(name, request.getHeader(name))
        );
        
        headerInfo.put("headers", headers);
        headerInfo.put("requestURI", request.getRequestURI());
        headerInfo.put("queryString", request.getQueryString());
        headerInfo.put("remoteHost", request.getRemoteHost());
        
        return ResponseEntity.ok(ApiResponse.success(headerInfo, "Request headers retrieved successfully"));
    }
} 