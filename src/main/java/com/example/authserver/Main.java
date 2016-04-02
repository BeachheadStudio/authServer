package com.example.authserver;

import com.example.authserver.config.ServerResourceConfig;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.jersey2.grizzly2.swagger.demo package
        String[] packages = {"com.example.authserver", "com.wordnik.swagger.jersey.listing"};

        final ResourceConfig grc = new ServerResourceConfig(packages);

        BeanConfig config = new BeanConfig();
        config.setResourcePackage("com.example.authserver");
        config.setVersion("1.0.0");
        config.setScan(true);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), grc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "swagger-ui/");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/docs");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("exiting...");
                server.shutdownNow();
            }
        }, "shutdownHook"));

        try {
            server.start();
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Server start failed:", e);
        }
    }
}

