package com.github.grantwest.sparkj.Messages;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

import com.github.grantwest.sparkj.SparkCloudJsonObjects.AccessToken;
import spark.Request;
import java.util.LinkedList;
import java.util.List;

public class ExpectedRequest {
    public String type;
    public String path;
    public String basicAuthentication;
    public List<ExpectedField> expectedFields;
    public String token;

    public ExpectedRequest() {
        type = null;
        path = null;
        basicAuthentication = null;
        expectedFields = new LinkedList<>();
        token = null;
    }

    public void add(ExpectedField ef) {
        expectedFields.add(ef);
    }

    public void set(AccessToken token) {
        this.token = token.token;
    }

    public void AssertMatch(Request req, String requestType) {
        if(type != null)
            assertThat(requestType, is(type));
        if(path != null)
            assertThat(req.pathInfo(), is(path));
        if(basicAuthentication != null) {
            assertThat(req.headers(), hasItem("Authorization"));
            assertThat(req.headers("Authorization"), is(basicAuthentication));
        }
        for (ExpectedField ef : expectedFields) {
            assertThat(req.queryParams(), hasItem(ef.key));
            assertThat(req.queryParams(ef.key), is(ef.value));
        }
        if(token != null) {
            assertThat(req.queryParams(), hasItem("access_token"));
            assertThat(req.queryParams("access_token"), is(token));
        }
    }
}
