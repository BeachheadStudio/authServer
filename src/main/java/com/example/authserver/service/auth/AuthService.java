package com.example.authserver.service.auth;

import com.example.authserver.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by SingleMalt on 4/1/16.
 */
public abstract class AuthService<T extends Auth> {
    public abstract boolean isFirstPartyAuthed(T auth);

    public static Auth detectAuthJson(String jsonInput) throws JsonParseException, IllegalArgumentException {
        Network network;
        JsonObject jObject = new JsonParser().parse(jsonInput).getAsJsonObject();

        if(jObject.has("network") && jObject.get("network") != null) {
            network = Network.valueOf(jObject.get("network").getAsString());

            switch (network) {
                case APPLE:
                    return new Gson().fromJson(jsonInput, AppleAuth.class);
                case GOOGLE:
                    return new Gson().fromJson(jsonInput, GoogleAuth.class);
                case AMAZON:
                    return new Gson().fromJson(jsonInput, AmazonAuth.class);
            }
        }

        throw new IllegalArgumentException("invalid json submitted");
    }
}
