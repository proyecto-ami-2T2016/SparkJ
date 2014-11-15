package com.infinity.sparkler.Messages;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;

import spark.Request;

public class ExpectedRequest {
    public String type;
    public String path;
    public String getAuthentication;
    public String postAuthentication;
    public String token;

    public ExpectedRequest() {
        type = null;
        path = null;
        getAuthentication = null;
        postAuthentication = null;
        token = null;
    }

    public void AssertMatch(Request req, String requestType) {
        if(type != null)
            assertThat(requestType, is(type));
        if(path != null)
            assertThat(req.pathInfo(), is(path));
        if(getAuthentication != null) {
            assertThat(req.headers(), hasItem("Authorization"));
            assertThat(req.headers("Authorization"), is(getAuthentication));
        }
    }
}
