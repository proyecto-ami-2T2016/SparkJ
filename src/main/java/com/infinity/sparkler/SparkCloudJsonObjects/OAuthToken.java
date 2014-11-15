package com.infinity.sparkler.SparkCloudJsonObjects;

public class OAuthToken implements IToken{
    public String access_token;
    public String token_type;
    public long expires_in;

    @Override
    public String toString() {
        return "[access_token=" + access_token + ",token_type=" + token_type + ",expires_in=" + expires_in + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuthToken that = (OAuthToken) o;

        if (expires_in != that.expires_in) return false;
        if (access_token != null ? !access_token.equals(that.access_token) : that.access_token != null) return false;
        if (token_type != null ? !token_type.equals(that.token_type) : that.token_type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = access_token != null ? access_token.hashCode() : 0;
        result = 31 * result + (token_type != null ? token_type.hashCode() : 0);
        result = 31 * result + (int) (expires_in ^ (expires_in >>> 32));
        return result;
    }
}
