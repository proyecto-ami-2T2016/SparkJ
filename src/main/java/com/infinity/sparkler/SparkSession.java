package com.infinity.sparkler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.IToken;
import com.infinity.sparkler.SparkCloudJsonObjects.OAuthToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;

import java.util.Collection;
import java.util.List;

public class SparkSession implements AutoCloseable {

    public static final String clientName = "sparkler-java-client";
    private static final String defaultBaseUrl = "https://api.spark.io";

    protected String baseUrl;
    private IToken token;
    private String username;
    private String password;

    public SparkSession(String username, String password) {
        this(username, password, defaultBaseUrl);
    }

    public SparkSession(String username, String password, String baseUrl) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
    }

    protected boolean connect() {
        token = getTokenFromServer();
        return token != null;
    }

    private IToken getTokenFromServer() {
        Collection<IToken> tokens = listTokensOnServer();
        tokens.removeIf(t -> !t.getClientName().equals(clientName));
        tokens.removeIf(IToken::isExpired);
        if(!tokens.isEmpty()) {
            return tokens.iterator().next();
        } else {
            return createNewToken();
        }
    }

    protected Collection<IToken> listTokensOnServer() {
        HttpRequest req = Unirest.get(baseUrl + "/v1/access_tokens")
            .header("accept", "application/json")
            .basicAuth(username, password);
        HttpResponse<String> res = SparkRestApi.sendRequest(req);
        return (Collection<IToken>) SparkRestApi.jsonToObject(res.getBody(), new TypeReference<List<AccessToken>>() {});
    }

    protected IToken createNewToken() {
        MultipartBody req = Unirest.post(baseUrl + "/oauth/token")
            .header("accept", "application/json")
            .header("content-type", "application/x-www-form-urlencoded")
            .field("grant_type", "password")
            .field("username", username)
            .field("password", password)
            .field("client_id", clientName)
            .field("client_secret", clientName);
        HttpResponse<String> res = SparkRestApi.sendRequest(req);
        return (IToken) SparkRestApi.jsonToObject(res.getBody(), new TypeReference<OAuthToken>() {});
    }

    protected boolean deleteToken(IToken token) {
        HttpRequestWithBody req = Unirest.delete(baseUrl + "/v1/access_tokens/" + token.getKey())
                .header("accept", "application/json")
                .basicAuth(username, password);
        HttpResponse<String> res = SparkRestApi.sendRequest(req.getHttpRequest());
        return res.getBody().contains("true");
    }

    @Override
    public void close() throws Exception {
        Unirest.shutdown();
    }

    protected String getTokenKey() {
        return token.getKey();
    }

    protected boolean connected() {
        return token != null && !token.isExpired();
    }

    protected void checkToken() {
        if(!connected()) {
            connect();
        }
    }
}
