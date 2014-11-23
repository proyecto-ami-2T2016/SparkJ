package com.infinity.sparkler;

import com.infinity.sparkler.SparkCloudJsonObjects.IToken;
import com.infinity.sparkler.Tools.SparkCredentials;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SessionIntegrationTests {
    private SparkSession session;

    @Before
    public void setup() throws IOException {
        SparkCredentials credentials = new SparkCredentials("credentials.txt");
        session = new SparkSession(credentials.username, credentials.password);
    }

    @Test
    public void listTokensOnServer() {
        Collection<IToken> tokens = session.listTokensOnServer();
        assertThat(tokens.isEmpty(), is(false));
    }

    @Ignore
    @Test(timeout=10000)
    public void createAndDeleteToken() {
        IToken newToken = session.createNewToken();
        assertThat(hasToken(listTokens(), newToken.getKey()), is(true));
        session.deleteToken(newToken);
        assertThat(hasToken(listTokens(), newToken.getKey()), is(false));
    }

    private Collection<IToken> listTokens() {
        Collection<IToken> tokens = session.listTokensOnServer();
        tokens.removeIf(t -> !t.getClientName().equals(SparkSession.clientName));
        return tokens;
    }

    private boolean hasToken(Collection<IToken> tokens, String id) {
        for(IToken t : tokens) {
            if(t.getKey().equals(id))
                return true;
        }
        return false;
    }
}
