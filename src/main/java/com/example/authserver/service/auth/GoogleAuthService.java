package com.example.authserver.service.auth;

import com.example.authserver.model.GoogleAuth;
import com.example.authserver.model.GoogleOauth;
import com.example.authserver.util.PropertiesHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * Created by kmiller on 4/1/16.
 */
public class GoogleAuthService extends AuthService<GoogleAuth> {
    private static final Logger logger = LogManager.getLogger(GoogleAuthService.class);

    protected static final Client CLIENT;
    protected static final String GOOGLE_URL;
    protected static final String ANDROID_CLIENT_ID;
    protected static final String CLIENT_ID;
    protected static final String CLIENT_SECRET;
    protected static final String PACKAGE_NAME;

    static {
        CLIENT = ClientBuilder.newClient(new ClientConfig());
        GOOGLE_URL = PropertiesHelper.getProperties().getProperty("google.oauth.url");
        ANDROID_CLIENT_ID = PropertiesHelper.getProperties().getProperty("google.android.client.id");
        CLIENT_ID = PropertiesHelper.getProperties().getProperty("google.client.id");
        CLIENT_SECRET = PropertiesHelper.getProperties().getProperty("google.client.secret");
        PACKAGE_NAME = PropertiesHelper.getProperties().getProperty("google.package.name");
    }

    @Override
    protected boolean isFirstPartyAuthed(GoogleAuth auth) {
        try {
            Response response = CLIENT.target(GOOGLE_URL)
                    .queryParam("access_token", auth.token)
                    .request()
                    .get();

            String oauthResponse = response.readEntity(String.class);
            GoogleOauth googleOauth = gson.fromJson(oauthResponse, GoogleOauth.class);

            if(googleOauth == null) {
                logger.warn("Not a valid token");
                return false;
            }

            if(!googleOauth.azp.equals(ANDROID_CLIENT_ID)) {
                logger.warn("Google: oauthResponse {} checkJson {]", oauthResponse, auth);
                logger.warn("Invalid client access token: bad azp");
                return false;
            }

            if(!googleOauth.aud.equals(ANDROID_CLIENT_ID)) {
                logger.warn("Google: oauthResponse {} checkJson {]", oauthResponse, auth);
                logger.warn("Invalid client access token: bad aud");
                return false;
            }

            if(!googleOauth.sub.equals(auth.firstPartyPlayerID)) {
                logger.warn("Google: oauthResponse {} checkJson {]", oauthResponse, auth);
                logger.warn("Invalid client access token: bad player id");
                return false;
            }

        } catch (Exception e) {
            logger.error("Error talking to google", e);
        }

        return true;
    }
}
