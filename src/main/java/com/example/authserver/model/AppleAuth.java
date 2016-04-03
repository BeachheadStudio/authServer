package com.example.authserver.model;

/**
 * Created by SingleMalt on 4/1/16.
 */
public class AppleAuth extends Auth {
    public String bundleId;
    public String publicKeyUrl;
    public String timestamp;
    public String signature;
    public String salt;
}
