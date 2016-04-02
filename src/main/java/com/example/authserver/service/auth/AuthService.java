package com.example.authserver.service.auth;

import com.example.authserver.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by kmiller on 4/1/16.
 */
abstract class AuthService<T extends Auth> {
    protected final Gson gson = new Gson();

    protected abstract boolean isFirstPartyAuthed(T auth);

    public Auth detectAuthJson(String jsonInput) throws JsonParseException, IllegalArgumentException {
        Network network;
        JsonObject jObject = new JsonParser().parse(jsonInput).getAsJsonObject();

        if(jObject.has("network") && jObject.get("network") != null) {
            network = Network.valueOf(jObject.get("network").getAsString());

            switch (network) {
                case APPLE:
                    return gson.fromJson(jsonInput, AppleAuth.class);
                case GOOGLE:
                    return gson.fromJson(jsonInput, GoogleAuth.class);
                case AMAZON:
                    return gson.fromJson(jsonInput, AmazonAuth.class);
            }
        }

        throw new IllegalArgumentException("invalid json submitted");
    }
}
