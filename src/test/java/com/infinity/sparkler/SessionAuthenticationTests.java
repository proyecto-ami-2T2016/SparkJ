package com.infinity.sparkler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.Messages.ExpectedRequest;
import com.infinity.sparkler.Messages.TestResponse;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudSim.SparkCloudSim;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SessionAuthenticationTests {
    private SparkCloudSim sim;
    private SparkCloudSession session;
    private ObjectMapper om;
    private static final String authentication = "Basic dXNlcm5hbWUxOnBhc3N3b3JkMQ==";
    private static final String username = "username1";
    private static final String password = "password1";

    @Before
    public void before() {
        sim  = new SparkCloudSim();
        sim.startSpark();
        session = new SparkCloudSession(username, password, "http://localhost:4567");
        om = new ObjectMapper();
    }

    @After
    public void after() {
        sim.stopSpark();
    }

    @Test
    public void getsListOfTokensFromServer() {
        ExpectTokenListRequest();
        List<AccessToken> tokenList = Arrays.asList(sparklerToken(), randomToken(), randomExpiredToken());
        RespondWithTokenList(tokenList);

        assertThat(session.ListTokensOnServer(), is(tokenList));
    }

    @Test(expected = SparkCloudSession.UsernameOrPasswordIncorrect.class)
    public void error401WhenGettingTokenListThrowsWrongUsernamePassword() {
        ExpectTokenListRequest();
        RespondWithStatusCode(401);

        session.ListTokensOnServer();
    }

    @Test
    public void getsNewTokenFromServer() {
        ExpectNewTokenRequest();
        RespondWithToken(sparklerToken());

        assertThat(session.getNewTokenFromServer(), is(sparklerToken()));
    }

    @Test
    public void asdf() {
        SparkCloudSession s = new SparkCloudSession("", "");
        AccessToken t = s.getNewTokenFromServer();
        System.out.println(t);
    }

    private void RespondWithStatusCode(int statusCode) {
        TestResponse response = new TestResponse();
        response.changeResponse((res) -> res.status(statusCode));
        sim.SendResponse(response);
    }

    private void RespondWithTokenList(List<AccessToken> tokens) {
        TestResponse res = new TestResponse();
        try {
            res.body = om.writeValueAsString(tokens);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sim.SendResponse(res);
    }

    private void RespondWithToken(AccessToken token) {
        TestResponse res = new TestResponse();
        try {
            res.body = om.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sim.SendResponse(res);
    }

    private void ExpectTokenListRequest() {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "get";
        req.path = "/v1/access_tokens";
        req.getAuthentication = authentication;
        sim.ExpectRequest(req);
    }

    private void ExpectNewTokenRequest() {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "post";
        req.path = "/oauth/token";

        sim.ExpectRequest(req);
    }

    private AccessToken sparklerToken() {
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

    private AccessToken randomToken() {
        AccessToken at = new AccessToken();
        at.client = "randomToken";
        at.token = "asdf";
        at.expires_at = Date.from(Instant.now().plusSeconds(3600));
        return at;
    }

    private AccessToken randomExpiredToken() {
        AccessToken at = new AccessToken();
        at.client = "randomExpiredToken";
        at.token = "asdf";
        at.expires_at = Date.from(Instant.now().minusSeconds(3600));
        return at;
    }
    @Test
    @Ignore
    public void thisIsIgnored() {
    }
}
