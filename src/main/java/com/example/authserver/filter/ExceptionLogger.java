package com.example.authserver.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created  on 7/24/15.
 */
@Provider
public class ExceptionLogger implements ExceptionMapper<Throwable> {

    private static final Logger logger = LogManager.getLogger(ExceptionLogger.class);

    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            return ((WebApplicationException)throwable).getResponse();
        } else {
            logger.error("Exception REST service call", throwable);
            return Response.status(500).build();
        }
    }

    public ExceptionLogger() {
        logger.debug("Registering ExceptionMapper");
    }
}
