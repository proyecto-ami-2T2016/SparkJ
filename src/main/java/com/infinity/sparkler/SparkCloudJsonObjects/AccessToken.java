package com.infinity.sparkler.SparkCloudJsonObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken extends TokenBase {
    public String token;
    public Date expires_at;
    public String client;

    @Override
    public String getKey() {
        return token;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public String getClientName() {
        return client;
    }

    @Override
    public String toString() {
        return "[token=" + token + ",expiration=" + expires_at + ",client=" + client + "]";
    }


}
