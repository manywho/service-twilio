package com.manywho.services.twilio;

import com.manywho.sdk.services.providers.ExceptionMapperProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    private static final Logger LOGGER = LogManager.getLogger("com.manywho.services.twilio", new ParameterizedMessageFactory());

    @Inject
    private ExceptionMapperProvider exceptionMapperProvider;

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error("An exception occurred", exception);

        return exceptionMapperProvider.toResponse(exception);
    }
}
