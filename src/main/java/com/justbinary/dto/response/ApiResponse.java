package com.justbinary.dto.response;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int statusCode;
    private LocalDateTime timestamp;
    private String error;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // ─── Success with data ───────────────────────────────────────
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setStatusCode(200);
        response.setMessage("Success");
        return response;
    }

    // ─── Success with custom message ─────────────────────────────
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setStatusCode(200);
        response.setMessage(message);
        return response;
    }

    // ─── Success with custom status code ─────────────────────────
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setStatusCode(statusCode);
        response.setMessage(message);
        return response;
    }

    // ─── Failure with message ─────────────────────────────────────
    public static <T> ApiResponse<T> failure(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setStatusCode(400);
        return response;
    }

    // ─── Failure with error detail ────────────────────────────────
    public static <T> ApiResponse<T> failure(String message, String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setError(error);
        response.setStatusCode(400);
        return response;
    }

    // ─── Failure with custom status code ─────────────────────────
    public static <T> ApiResponse<T> failure(String message, String error, int statusCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setError(error);
        response.setStatusCode(statusCode);
        return response;
    }

    // ─── Getters & Setters ────────────────────────────────────────

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}