package com.github.grantwest.sparkj;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import com.github.grantwest.sparkj.SparkCloudJsonObjects.IToken;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

public class SessionTest extends SessionTestsBase {

    @Test
    public void getsListOfTokensFromServer() {
        expectTokenListRequest();
        List<IToken> tokenList = Arrays.asList(sparkjToken(), randomToken(), randomExpiredToken());
        respondWithTokenList(tokenList);

        assertThat(session.listTokensOnServer(), is(tokenList));
    }

    @Test(expected = SparkRestApi.UsernameOrPasswordIncorrectException.class)
    public void status401WhenGettingTokenListThrowsWrongUsernamePassword() {
        expectTokenListRequest();
        respondWithStatusCode(401);

        assertThat(session.listTokensOnServer(), is(nullValue()));
    }

    @Test(expected = SparkRestApi.SparkCloudNotAvailableException.class)
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
        expectDeleteTokenRequest(sparkjToken());
        respondWithJson("{ \"ok\": true }");

        assertThat(session.deleteToken(sparkjToken()), is(true));
    }

    @Test
    public void deletesTokenReturnsFalseWhenFailed() {
        expectDeleteTokenRequest(sparkjToken());
        respondWithJson("{ \"ok\": false }");

        assertThat(session.deleteToken(sparkjToken()), is(false));
    }
}
