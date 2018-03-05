package com.usanin.andrew.exception;

/**
 * Base class for all project exception
 */
public class HedgehogException extends RuntimeException {

    private final int code;
    private final ErrorType errorType;

    public HedgehogException(String message, int code, ErrorType errorType) {
        super(message);
        this.code = code;
        this.errorType = errorType;
    }

    public HedgehogException(String message, Throwable cause, int code, ErrorType errorType) {
        super(message, cause);
        this.code = code;
        this.errorType = errorType;
    }

    public int getCode() {
        return code;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
