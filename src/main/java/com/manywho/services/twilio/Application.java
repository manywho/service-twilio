package com.manywho.services.twilio;

import com.manywho.sdk.services.BaseApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationPath("/")
public class Application extends BaseApplication {
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public Application() {
        registerSdk()
                .packages("com.manywho.services.twilio")
                .register(new ApplicationBinder());
    }

    public static void main(String[] args) {
        startServer(new Application());
    }
}
