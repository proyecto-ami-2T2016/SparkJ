package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.AccessToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.IToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.OAuthToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.SparkEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SparkSession implements AutoCloseable {

    public static final String clientName = "sparkj-java-client";
    private static final String defaultBaseUrl = "https://api.particle.io";

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
        Optional<IToken> token = listTokensOnServer().stream()
                .filter(t -> t.getClientName().equals(clientName))
                .filter(t -> !t.isExpired())
                .findFirst();
        return token.orElseGet(this::createNewToken);
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

    protected boolean checkToken() {
        return token != null && !token.isExpired();
    }

    protected boolean connected() {
        return checkToken();
    }

    protected void connectIfNotConnected(){
        if(!connected()) connect();
    }
}
