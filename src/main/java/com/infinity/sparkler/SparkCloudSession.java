package com.infinity.sparkler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.IToken;
import com.infinity.sparkler.SparkCloudJsonObjects.OAuthToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class SparkCloudSession implements AutoCloseable {

    public static final String clientName = "sparkler-java-client";
    private static final String defaultBaseUrl = "https://api.spark.io";

    protected String baseUrl;
    private IToken token;
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
        token = getTokenFromServer(0);
        return token != null;
    }

    private IToken getTokenFromServer(int attempt) {
        Collection<IToken> tokens = listTokensOnServer();
        tokens.removeIf(t -> !t.getClientName().equals(clientName));
        tokens.removeIf(IToken::isExpired);
        if(!tokens.isEmpty()) {
            return tokens.iterator().next();
        } else if(attempt > 0) {
            createNewToken();
            return getTokenFromServer(attempt + 1);
        }
        return null;
    }

    protected Collection<IToken> listTokensOnServer() {
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

    protected IToken createNewToken() {
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

    protected boolean deleteToken(IToken token) {
        try {
            HttpResponse<String> res = Unirest.delete(baseUrl + "/v1/access_tokens/" + token.getKey())
                    .header("accept", "application/json")
                    .basicAuth(username, password)
                    .asString();
            return res.getBody().contains("true");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        Unirest.shutdown();
    }

    public String getTokenKey() {
        return token.getKey();
    }

    public boolean connected() {
        return token != null && !token.isExpired();
    }

    public class UsernameOrPasswordIncorrect extends RuntimeException {}

}
