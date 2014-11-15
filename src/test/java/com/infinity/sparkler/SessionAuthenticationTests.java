package com.infinity.sparkler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.Messages.ExpectedField;
import com.infinity.sparkler.Messages.ExpectedRequest;
import com.infinity.sparkler.Messages.TestResponse;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.IToken;
import com.infinity.sparkler.SparkCloudJsonObjects.OAuthToken;
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
    public void after() throws Exception {
        sim.close();
    }

    @Test
    public void getsListOfTokensFromServer() {
        ExpectTokenListRequest();
        List<IToken> tokenList = Arrays.asList(sparklerToken(), randomToken(), randomExpiredToken());
        RespondWithTokenList(tokenList);

        assertThat(session.listTokensOnServer(), is(tokenList));
    }

    @Test(expected = SparkCloudSession.UsernameOrPasswordIncorrect.class)
    public void error401WhenGettingTokenListThrowsWrongUsernamePassword() {
        ExpectTokenListRequest();
        RespondWithStatusCode(401);

        session.listTokensOnServer();
    }

    @Test
    public void getsNewTokenFromServer() {
        ExpectNewTokenRequest();
        RespondWithToken(oAuthToken());

        assertThat(session.createNewToken(), is(oAuthToken()));
    }

    @Test
    public void asdf() {
        SparkCloudSession s = new SparkCloudSession("", "");
        OAuthToken t = s.createNewToken();
        System.out.println(t);
    }

    @Test
    public void asdf2() {
        SparkCloudSession s = new SparkCloudSession("", "");
        List<AccessToken> t = s.listTokensOnServer();
        t.forEach(System.out::println);
    }

    private void RespondWithStatusCode(int statusCode) {
        TestResponse response = new TestResponse();
        response.changeResponse((res) -> res.status(statusCode));
        sim.sendResponse(response);
    }

    private void RespondWithTokenList(List<IToken> tokens) {
        TestResponse res = new TestResponse();
        try {
            res.body = om.writeValueAsString(tokens);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sim.sendResponse(res);
    }

    private void RespondWithToken(IToken token) {
        TestResponse res = new TestResponse();
        try {
            res.body = om.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        sim.sendResponse(res);
    }

    private void ExpectTokenListRequest() {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "get";
        req.path = "/v1/access_tokens";
        req.basicAuthentication = authentication;
        sim.expectRequest(req);
    }

    private void ExpectNewTokenRequest() {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "post";
        req.path = "/oauth/token";
        req.add(new ExpectedField("grant_type", "password"));
        req.add(new ExpectedField("username", username));
        req.add(new ExpectedField("password", password));
        req.add(new ExpectedField("client_id", SparkCloudSession.clientName));
        req.add(new ExpectedField("client_secret", SparkCloudSession.clientName));

        sim.expectRequest(req);
    }

    private OAuthToken oAuthToken() {
        OAuthToken token = new OAuthToken();
        token.access_token = "12345";
        token.token_type = "bearer";
        token.expires_in = 7776000;
        return token;
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
