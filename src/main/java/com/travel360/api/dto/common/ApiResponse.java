package com.travel360.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;
    
    @Schema(description = "The response data")
    private T data;
    
    @Schema(description = "Optional success or info message", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "List of error messages if any", example = "[\"Invalid input\", \"Missing required field\"]")
    private List<String> errors;

    @Schema(description = "Pagination information", example = "{\"page\": 1, \"size\": 10, \"total\": 100}")
    private Pagination pagination;

    // Private constructor to enforce use of static methods
    private ApiResponse() {}

    // Success response with data
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    // Success response with data and message
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    // Success response with pagination data
    public static <T> ApiResponse<T> success(T data, Pagination pagination) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.pagination = pagination;
        return response;
    }

    // Success response with only message (no data)
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        return response;
    }

    // Error response with single error message
    public static <T> ApiResponse<T> error(String errorMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.errors = List.of(errorMessage);
        return response;
    }

    // Error response with multiple error messages
    public static <T> ApiResponse<T> error(List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.errors = errors;
        return response;
    }

    // Error response with error message and optional message
    public static <T> ApiResponse<T> error(String errorMessage, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.errors = List.of(errorMessage);
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", pagination=" + pagination +
                '}';
    }
} 