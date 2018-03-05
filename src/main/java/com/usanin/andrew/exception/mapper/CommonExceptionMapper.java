package com.usanin.andrew.exception.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.usanin.andrew.exception.ErrorResponse;
import com.usanin.andrew.exception.ErrorResponseCodes;
import com.usanin.andrew.exception.ErrorType;
import org.glassfish.jersey.server.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.ValidationException;

@Provider
public class CommonExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(Throwable exception) {

        logger.error("During request processing caused an error. Here is a message: '{}'", exception.getMessage(), exception);


        final ErrorResponse errorResponse;
        if (exception instanceof ValidationException
                || exception instanceof ParamException
                || exception instanceof JsonProcessingException) {
            errorResponse = new ErrorResponse(exception.getMessage(), ErrorType.VALIDATION, ErrorResponseCodes.BAD_REQUEST);
        } else {
            errorResponse = new ErrorResponse(exception.getMessage(), ErrorType.INTERNAL, ErrorResponseCodes.INTERNAL_SERVER_ERROR);
        }

        return Response.status(errorResponse.getCode())
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .entity(errorResponse)
                       .build();
    }
}
