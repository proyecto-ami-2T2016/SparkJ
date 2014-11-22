package com.infinity.sparkler;

import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.OAuthToken;
import com.infinity.sparkler.Tools.SparkCredentials;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SessionIntegrationTests {
    private SparkCloudSession session;

    @Before
    public void setup() throws IOException {
        SparkCredentials credentials = new SparkCredentials("credentials.txt");
        session = new SparkCloudSession(credentials.username, credentials.password);
    }

    @Test
    public void listTokensOnServer() {
        List tokens = session.listTokensOnServer();
        assertThat(tokens.isEmpty(), is(false));
    }

    @Test(timeout=5000)
    public void createAndDeleteToken() {
        OAuthToken newToken = session.createNewToken();
        assertThat(hasToken(listTokens(), newToken.access_token), is(true));
        session.deleteToken(newToken);
        assertThat(hasToken(listTokens(), newToken.access_token), is(false));
    }

    private Collection<AccessToken> listTokens() {
        Collection<AccessToken> tokens = session.listTokensOnServer();
        tokens.removeIf(t -> !t.client.equals(SparkCloudSession.clientName));
        return tokens;
    }

    private boolean hasToken(Collection<AccessToken> tokens, String id) {
        for(AccessToken t : tokens) {
            if(t.token.equals(id))
                return true;
        }
        return false;
    }
}
