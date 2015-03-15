package com.github.grantwest.sparkj;

import com.github.grantwest.sparkj.SparkCloudJsonObjects.IToken;
import com.github.grantwest.sparkj.Tools.SparkCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SparkEventStreamIntegrationTest {
    private SparkSession session;
    private SparkDevice device;
    private SparkEventStream eventStream;
    private int eventsReceived;

    @Before
    public void setup() throws IOException {
        SparkCredentials credentials = new SparkCredentials("C:\\credentials.txt");
        session = new SparkSession(credentials.username, credentials.password);
        device = new SparkDevice(credentials.deviceId, session);
        eventsReceived = 0;
        session.connect();
    }

    @After
    public void teardown() throws Exception {
        eventStream.close();
    }

    @Test
    public void privateSessionEventStreamCallsHandlerOnEvent() throws InterruptedException {
        String expectedData = String.valueOf(new Random().nextInt());
        eventStream = new SparkEventStream(session, e -> {
            eventsReceived++;
            assertThat(e.name, is("TestEvent"));
            assertThat(e.data, is(expectedData));
        });

        Thread.sleep(3000);
        device.callFunction("PubEvent", expectedData);
        Thread.sleep(3000);

        assertThat(eventsReceived, is(1));
    }


}
