package com.infinity.sparkler.SparkCloudJsonObjects;

import java.util.Date;

public class AccessToken {
    public String token;
    public Date expires_at;
    public String client;

    @Override
    public String toString() {
        return "[token=" + token + ",expiration=" + expires_at + ",client=" + client + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessToken that = (AccessToken) o;

        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (expires_at != null ? !expires_at.equals(that.expires_at) : that.expires_at != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (expires_at != null ? expires_at.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }
}
