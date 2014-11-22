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
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;

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
            HttpRequest req = Unirest.get(baseUrl + "/v1/access_tokens")
                .header("accept", "application/json")
                .basicAuth(username, password);
            HttpResponse<String> res = SendRequest(req);
            return objectMapper.readValue(res.getBody(), new TypeReference<List<AccessToken>>() {});
        } catch (JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected IToken createNewToken() {
        try {
            MultipartBody req = Unirest.post(baseUrl + "/oauth/token")
                .header("accept", "application/json")
                .header("content-type", "application/x-www-form-urlencoded")
                .field("grant_type", "password")
                .field("username", username)
                .field("password", password)
                .field("client_id", clientName)
                .field("client_secret", clientName);
            HttpResponse<String> res = SendRequest(req);
            return objectMapper.readValue(res.getBody(), new TypeReference<OAuthToken>() {});
        } catch ( JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected boolean deleteToken(IToken token) {
        HttpRequestWithBody req = Unirest.delete(baseUrl + "/v1/access_tokens/" + token.getKey())
                .header("accept", "application/json")
                .basicAuth(username, password);
        HttpResponse<String> res = SendRequest(req);
        return res.getBody().contains("true");
    }

    private HttpResponse<String> SendRequest(com.mashape.unirest.request.BaseRequest req) {
        try {
            HttpResponse<String> res = req.asString();
            switch(res.getCode()) {
                case 200:
                    return res;
                case 400:
                    throw new InvalidVariableOrFunctionException();
                case 401:
                    throw new UsernameOrPasswordIncorrectException();
                case 403:
                    throw new NotAuthorizedForThisCoreException();
                case 404:
                    throw new CoreNotConnectedToCloudException();
                case 408:
                    throw new SparkCloudConnectionTimeoutException();
                case 500:
                    throw new NetworkConnectionErrorException();
                default:
                    throw new UnknownNetworkConnectionErrorException();
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
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

    public class InvalidVariableOrFunctionException extends RuntimeException {}
    public class UsernameOrPasswordIncorrectException extends RuntimeException {}
    public class NotAuthorizedForThisCoreException extends RuntimeException {}
    public class CoreNotConnectedToCloudException extends RuntimeException {}
    public class SparkCloudConnectionTimeoutException extends RuntimeException {}
    public class NetworkConnectionErrorException extends RuntimeException {}
    public class UnknownNetworkConnectionErrorException extends RuntimeException {}

}
