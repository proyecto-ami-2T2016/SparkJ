package com.infinity.sparkler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.OAuthToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SparkCloudSession implements AutoCloseable {

    public static final String clientName = "sparkler-java-client";
    private static final String defaultBaseUrl = "https://api.spark.io";

    protected String baseUrl;
    private AccessToken accessToken;
    private String username;
    private String password;
    private ObjectMapper objectMapper;

    public SparkCloudSession(String username, String password) {
        this.username = username;
        this.password = password;
        baseUrl = defaultBaseUrl;
        objectMapper = new ObjectMapper();
    }

    public SparkCloudSession(String username, String password, String baseUrl) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
        objectMapper = new ObjectMapper();
    }

    public boolean connect() {
        accessToken = getTokenFromServer(0);
        return accessToken != null;
    }

    private AccessToken getTokenFromServer(int attempt) {
        List<AccessToken> tokens = listTokensOnServer();
        tokens.removeIf(t -> !t.client.equals(clientName));
        tokens.removeIf(t -> t.expires_at.before(new Date()));
        if(!tokens.isEmpty()) {
            return tokens.get(0);
        } else if(attempt > 0) {
            createNewToken();
            return getTokenFromServer(attempt+1);
        }
        return null;
    }

    protected List<AccessToken> listTokensOnServer() {
        try {
            HttpResponse<String> res = Unirest.get(baseUrl + "/v1/access_tokens")
                .header("accept", "application/json")
                .basicAuth(username, password)
                .asString();
            return objectMapper.readValue(res.getBody(), new TypeReference<List<AccessToken>>() {});
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected OAuthToken createNewToken() {
        try {
            HttpResponse<String> res = Unirest.post(baseUrl + "/oauth/token")
                .header("accept", "application/json")
                .header("content-type", "application/x-www-form-urlencoded")
                .field("grant_type", "password")
                .field("username", username)
                .field("password", password)
                .field("client_id", clientName)
                .field("client_secret", clientName)
                .asString();
            return objectMapper.readValue(res.getBody(), new TypeReference<OAuthToken>() {});
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected boolean deleteToken(AccessToken token) {
        try {
            HttpResponse<String> res = Unirest.delete(baseUrl + "/v1/access_tokens/" + token.token)
                    .header("accept", "application/json")
                    .basicAuth(username, password)
                    .asString();
            return res.getBody().contains("true");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected String getTokenKey() {
        return accessToken.token;
    }

    @Override
    public void close() throws Exception {
        Unirest.shutdown();
    }

    public class UsernameOrPasswordIncorrect extends RuntimeException {}

}
