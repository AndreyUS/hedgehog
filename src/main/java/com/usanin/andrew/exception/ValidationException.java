package com.usanin.andrew.exception;

/**
 * Throws when passed data is not valid.
 */
public class ValidationException extends HedgehogException {

    public ValidationException(String message) {
        super(message, ErrorResponseCodes.BAD_REQUEST, ErrorType.VALIDATION);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, ErrorResponseCodes.BAD_REQUEST, ErrorType.VALIDATION);
    }
}
