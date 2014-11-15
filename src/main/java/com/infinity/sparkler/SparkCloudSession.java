package com.infinity.sparkler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SparkCloudSession {

    //private static final String clientName = "SparklerSession";
    private static final String clientName = "sparkler-java-client";
    private static final String defaultBaseUrl = "https://api.spark.io";

    private String baseUrl;
    private String username;
    private String password;
    private AccessToken accessToken;

    public SparkCloudSession(String username, String password) {
        this.username = username;
        this.password = password;
        baseUrl = defaultBaseUrl;
    }

    public SparkCloudSession(String username, String password, String baseUrl) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
    }

    private void connect() {
        LoadAccessToken();
    }

    private void LoadAccessToken() {
        if(!LoadExistingAccessToken() && !LoadNewAccessToken()) {
            throw new FailedToGetAccessToken();
        }
    }

    private boolean LoadExistingAccessToken() {
        for(AccessToken at : ListTokensOnServer())
            if(at.client.equals(clientName))
            {
                accessToken = at;
                return true;
            }
        return false;
    }

    private boolean LoadNewAccessToken() {
        return false;
    }

    public List<AccessToken> ListTokensOnServer() {
        HttpURLConnection conn = httpGetConn("GET", "/v1/access_tokens");
        AddAuthToHttp(conn);
        List<AccessToken> list = (List<AccessToken>) ResponseAsObject(conn, new TypeReference<List<AccessToken>>() {});
        conn.disconnect();
        return list;
    }

    public AccessToken getNewTokenFromServer() {
        HttpURLConnection conn = httpGetConn("POST", "/oauth/token");
        conn.

        AccessToken token = (AccessToken) ResponseAsObject(conn, new TypeReference<AccessToken>() {});
        conn.disconnect();
        return token;
    }

    private HttpURLConnection httpGetConn(String type, String url) {
        try {
            URL javaUrl = new URL(baseUrl + url);
            HttpURLConnection conn = (HttpURLConnection) javaUrl.openConnection();
            conn.setRequestMethod(type);
            if(type.equals("GET")) {
                conn.setRequestProperty("Accept", "application/json");
            } else if(type.equals("POST")) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnknownConnectionIssue();
        }
    }

    private Object ResponseAsObject(HttpURLConnection conn, TypeReference typeReference)  {
        try {
            String json = ResponseAsString(conn);
            ObjectMapper om = new ObjectMapper();
            return om.readValue(json, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FailedToParseHttpResponse();
        }
    }

    private String ResponseAsString(HttpURLConnection conn) {
        try {
            switch(conn.getResponseCode()) {
                case 401:
                    throw new UsernameOrPasswordIncorrect();
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = "";
                    String line;
                    while ((line = br.readLine()) != null) {
                        response += line + "\n";
                    }
                    return response;
                default:
                    throw new UnknownConnectionIssue();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FailedToParseHttpResponse();
        }
    }

    private void AddAuthToHttp(HttpURLConnection conn) {
        BASE64Encoder enc = new sun.misc.BASE64Encoder();
        String usernamePassword = username + ":" + password;
        String encodedAuthorization = enc.encode(usernamePassword.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
    }

    public class FailedToGetAccessToken extends RuntimeException {}
    public class FailedToParseHttpResponse extends RuntimeException {}
    public class UsernameOrPasswordIncorrect extends RuntimeException {}
    public class UnknownConnectionIssue extends RuntimeException {}
}
