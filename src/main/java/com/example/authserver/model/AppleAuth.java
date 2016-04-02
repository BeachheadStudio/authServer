package com.example.authserver.model;

/**
 * Created by kmiller on 4/1/16.
 */
public class AppleAuth extends Auth {
    public String bundleID;
    public String publicKeyUrl;
    public String timestamp;
    public String signature;
    public String salt;
}
