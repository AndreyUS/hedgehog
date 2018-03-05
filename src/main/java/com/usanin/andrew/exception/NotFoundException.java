package com.usanin.andrew.exception;

/**
 * Throws when entity is not found.
 */
public class NotFoundException extends HedgehogException {
    public NotFoundException(String message) {
        super(message, ErrorResponseCodes.NOT_FOUND, ErrorType.NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, ErrorResponseCodes.NOT_FOUND, ErrorType.NOT_FOUND);
    }
}
