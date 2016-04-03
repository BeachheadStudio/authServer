package com.example.authserver.service.auth;

import com.example.authserver.model.AmazonAuth;
import com.example.authserver.model.AmazonOauth;
import com.example.authserver.util.PropertiesHelper;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * Created by SingleMalt on 4/1/16.
 */
public class AmazonAuthService extends AuthService<AmazonAuth> {
    private static final Logger logger = LogManager.getLogger(AmazonAuthService.class);
    private static final String AMAZON_OAUTH_URL = "https://api.amazon.com/auth/o2/tokeninfo";
    private static final String CLIENT_ID;

    static {
        CLIENT_ID = PropertiesHelper.getProperties().getProperty("amazon.oauth.app_id");
    }

    @Override
    public boolean isFirstPartyAuthed(AmazonAuth auth) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(AMAZON_OAUTH_URL)
                .queryParam("access_token", auth.token)
                .request()
                .get();
        if (response == null || response.getStatus() != 200) {
            logger.error("AmazonAuthServiceImpl invalid oauth token: code {}", response == null ? null : response.getStatus());
            return false;
        } else {
            String responseBody = response.readEntity(String.class);
            AmazonOauth amzOauth = new Gson().fromJson(responseBody, AmazonOauth.class);

            if(!CLIENT_ID.equals(amzOauth.app_id)) {
                logger.warn("Not the same Amazon Oauth application");
                return false;
            }

            if(!auth.playerId.equals(amzOauth.user_id)) {
                logger.warn("Not the same Amazon user id");
                return false;
            }

            return true;
        }
    }
}
