package com.infinity.sparkler.SparkCloudJsonObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infinity.sparkler.SparkSession;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthToken extends TokenBase {
    public String access_token;
    public String token_type;
    public long expires_in;

    @Override
    public String getKey() {
        return access_token;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public String getClientName() {
        return SparkSession.clientName;
    }

    @Override
    public String toString() {
        return "[access_token=" + access_token + ",token_type=" + token_type + ",expires_in=" + expires_in + "]";
    }
}
