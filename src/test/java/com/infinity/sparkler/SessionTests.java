package com.infinity.sparkler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import com.infinity.sparkler.SparkCloudJsonObjects.IToken;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SessionTests extends SessionTestsBase {

    @Test
    public void getsListOfTokensFromServer() {
        expectTokenListRequest();
        List<IToken> tokenList = Arrays.asList(sparklerToken(), randomToken(), randomExpiredToken());
        respondWithTokenList(tokenList);

        assertThat(session.listTokensOnServer(), is(tokenList));
    }

    @Test(expected = SparkRestApi.UsernameOrPasswordIncorrectException.class)
    public void status401WhenGettingTokenListThrowsWrongUsernamePassword() {
        expectTokenListRequest();
        respondWithStatusCode(401);

        assertThat(session.listTokensOnServer(), is(nullValue()));
    }

    @Test(expected = SparkRestApi.NetworkConnectionErrorException.class)
    public void status500WhenGettingTokenListThrowsNetworkConnectionError() {
        expectTokenListRequest();
        respondWithStatusCode(500);

        assertThat(session.listTokensOnServer(), is(nullValue()));
    }

    @Test
    public void createsNewToken() {
        expectNewTokenRequest();
        respondWithToken(oAuthToken());

        assertThat(session.createNewToken(), is(oAuthToken()));
    }

    @Test
    public void deletesToken() {
        expectDeleteTokenRequest(sparklerToken());
        respondWithJson("{ \"ok\": true }");

        assertThat(session.deleteToken(sparklerToken()), is(true));
    }

    @Test
    public void deletesTokenReturnsFalseWhenFailed() {
        expectDeleteTokenRequest(sparklerToken());
        respondWithJson("{ \"ok\": false }");

        assertThat(session.deleteToken(sparklerToken()), is(false));
    }
}
