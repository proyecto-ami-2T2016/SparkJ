package com.github.grantwest.sparkj;

import com.github.grantwest.sparkj.Messages.ExpectedRequest;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.IToken;
import org.junit.Before;
import org.junit.Test;

public class SparkEventStreamTest extends SparkEventStreamTestsBase {
    private SparkEventStream eventStream;

    @Before
    public void setup() {

    }

    @Test
    public void test() {
        expectRequest(sparkjToken());
        eventStream = new SparkEventStream(session, null);
    }

    protected void expectRequest(IToken token) {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "get";
        req.path = "/v1/devices/events";
        req.basicAuthentication = "Bearer " + token.getKey();
        sim.expectRequest(req);
    }
}
