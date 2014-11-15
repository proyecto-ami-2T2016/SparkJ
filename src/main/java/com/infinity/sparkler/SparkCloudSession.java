package com.infinity.sparkler;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.OAuthToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.List;

public class SparkCloudSession implements AutoCloseable{

    public static final String clientName = "sparkler-java-client";
    private static final String defaultBaseUrl = "https://api.spark.io";

    private String baseUrl;
    private String username;
    private String password;
    private AccessToken accessToken;
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

    public List<AccessToken> listTokensOnServer() {
        try {
            HttpResponse<JsonNode> res = Unirest.get(baseUrl + "/v1/access_tokens")
                .header("accept", "application/json")
                .basicAuth(username, password)
                .asJson();
            return objectMapper.readValue(res.getBody().toString(), new TypeReference<List<AccessToken>>() {});
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OAuthToken createNewToken() {
        try {
            HttpResponse<JsonNode> res = Unirest.post(baseUrl + "/oauth/token")
                .header("accept", "application/json")
                .header("content-type", "application/x-www-form-urlencoded")
                .field("grant_type", "password")
                .field("username", username)
                .field("password", password)
                .field("client_id", clientName)
                .field("client_secret", clientName)
                .asJson();
            return objectMapper.readValue(res.getBody().toString(), new TypeReference<OAuthToken>() {});
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new OAuthToken();
    }

    public boolean deleteToken(AccessToken token) {
        try {
            HttpResponse<JsonNode> res = Unirest.delete(baseUrl + "/v1/access_tokens/" + token.token)
                    .header("accept", "application/json")
                    .basicAuth(username, password)
                    .asJson();
            return true;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        Unirest.shutdown();
    }


    public class FailedToGetAccessToken extends RuntimeException {}
    public class FailedToParseHttpResponse extends RuntimeException {}
    public class UsernameOrPasswordIncorrect extends RuntimeException {}
    public class UnknownConnectionIssue extends RuntimeException {}
}
