package com.usanin.andrew.exception.mapper;


import com.usanin.andrew.exception.ErrorResponse;
import com.usanin.andrew.exception.HedgehogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class HedgehogExceptionMapper implements ExceptionMapper<HedgehogException> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(HedgehogException exception) {
        logger.error("During request processing caused an error. Here is a message: '{}'", exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getErrorType(), exception.getCode());
        return Response.status(exception.getCode())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(errorResponse)
                .build();
    }
}
