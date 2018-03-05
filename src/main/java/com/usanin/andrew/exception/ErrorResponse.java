package com.usanin.andrew.exception;


public class ErrorResponse {

    private String message;
    private ErrorType errorType;
    private int code;

    //Need for jackson
    public ErrorResponse() {
    }

    public ErrorResponse(String message, ErrorType errorType, int code) {
        this.message = message;
        this.errorType = errorType;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public int getCode() {
        return code;
    }
}
