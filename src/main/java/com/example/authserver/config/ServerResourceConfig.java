package com.example.authserver.config;

import com.example.authserver.filter.ExceptionLogger;
import com.example.authserver.util.GsonJerseyProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

/**
 * Created by kmiller on 10/2/14.
 */
public class ServerResourceConfig extends ResourceConfig {

    public ServerResourceConfig(String[] packages)
    {
        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        register(ExceptionLogger.class);
        register(GsonJerseyProvider.class);

        packages(true, packages);
    }

}
