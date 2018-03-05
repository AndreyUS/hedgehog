package com.usanin.andrew.exception;


public class BusinessException extends HedgehogException {

    public BusinessException(String message) {
        super(message, ErrorResponseCodes.HTTP_UNPROCESSABLE_ENTITY, ErrorType.BUSINESS);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, ErrorResponseCodes.HTTP_UNPROCESSABLE_ENTITY, ErrorType.BUSINESS);
    }
}
