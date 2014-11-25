package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.grantwest.sparkj.Messages.ExpectedField;
import com.github.grantwest.sparkj.Messages.ExpectedRequest;
import com.github.grantwest.sparkj.Messages.TestResponse;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.AccessToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.IToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.OAuthToken;
import com.github.grantwest.sparkj.SparkCloudSim.SparkCloudSim;
import org.junit.After;
import org.junit.Before;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public abstract class SessionTestsBase {
    private static final String authentication = "Basic dXNlcm5hbWUxOnBhc3N3b3JkMQ==";
    private static final String username = "username1";
    private static final String password = "password1";
    protected SparkSession session;
    protected SparkCloudSim sim;
    protected ObjectMapper om;

    @Before
    public void before() {
        sim  = new SparkCloudSim();
        sim.startSpark();
        session = new SparkSession(username, password, "http://localhost:4567");
        om = new ObjectMapper();
    }

    @After
    public void after() throws Exception {
        sim.close();
    }

    //region Expectations
    protected void expectTokenListRequest() {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "get";
        req.path = "/v1/access_tokens";
        req.basicAuthentication = authentication;
        sim.expectRequest(req);
    }

    protected void expectNewTokenRequest() {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "post";
        req.path = "/oauth/token";
        req.add(new ExpectedField("grant_type", "password"));
        req.add(new ExpectedField("username", username));
        req.add(new ExpectedField("password", password));
        req.add(new ExpectedField("client_id", SparkSession.clientName));
        req.add(new ExpectedField("client_secret", SparkSession.clientName));

        sim.expectRequest(req);
    }

    protected void expectDeleteTokenRequest(AccessToken token) {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "delete";
        req.path = "/v1/access_tokens/" + token.token;
        req.basicAuthentication = authentication;
        sim.expectRequest(req);
    }
    //endregion

    //region Responses
    protected void respondWithTokenList(List<IToken> tokens) {
        TestResponse res = new TestResponse();
        try {
            res.body = om.writeValueAsString(tokens);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sim.sendResponse(res);
    }

    protected void respondWithToken(IToken token) {
        TestResponse res = new TestResponse();
        try {
            res.body = om.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sim.sendResponse(res);
    }

    protected void respondWithStatusCode(int statusCode) {
        TestResponse response = new TestResponse();
        response.changeResponse((res) -> res.status(statusCode));
        sim.sendResponse(response);
    }

    protected void respondWithJson(String json) {
        TestResponse res = new TestResponse();
        res.body = json;
        sim.sendResponse(res);
    }
    //endregion

    //region Tokens
    protected OAuthToken oAuthToken() {
        OAuthToken token = new OAuthToken();
        token.access_token = "12345";
        token.token_type = "bearer";
        token.expires_in = 7776000;
        return token;
    }

    protected AccessToken sparklerToken() {
        AccessToken at = new AccessToken();
        at.client = "sparkler-java-client";
        at.token = "12345";
        at.expires_at = Date.from(Instant.now().plusSeconds(3600));
        return at;
    }

    private AccessToken expiredSparklerToken() {
        AccessToken at = new AccessToken();
        at.client = "sparkler-java-client";
        at.token = "12345678910";
        at.expires_at = Date.from(Instant.now().minusSeconds(3600));
        return at;
    }

    protected AccessToken randomToken() {
        AccessToken at = new AccessToken();
        at.client = "randomToken";
        at.token = "asdf";
        at.expires_at = Date.from(Instant.now().plusSeconds(3600));
        return at;
    }

    protected AccessToken randomExpiredToken() {
        AccessToken at = new AccessToken();
        at.client = "randomExpiredToken";
        at.token = "asdf";
        at.expires_at = Date.from(Instant.now().minusSeconds(3600));
        return at;
    }
    //endregion
}
