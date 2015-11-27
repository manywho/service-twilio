package com.manywho.services.twilio;

import com.manywho.sdk.services.BaseApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationPath("/")
public class Application extends BaseApplication {
    public Application() {
        registerSdk()
                .packages("com.manywho.services.twilio")
                .register(new ApplicationBinder());
    }

    public static void main(String[] args) {
        startServer(new Application(), "api/twilio/2");
    }
}