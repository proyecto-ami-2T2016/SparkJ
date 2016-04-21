package com.github.grantwest.sparkj;

import com.github.grantwest.sparkj.SparkCloudJsonObjects.SparkEvent;
import com.github.grantwest.sparkj.Tools.SparkCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class DeviceIntegrationTest {
    private SparkDevice device;
    private SparkEventStream eventStream;

    @Before
    public void setup() throws IOException {
        SparkCredentials credentials = new SparkCredentials("/Users/grant/credentials.txt");
        device = new SparkDevice(credentials.deviceId, credentials.username, credentials.password);
    }

    @After
    public void after() {
        if(eventStream != null) eventStream.close();
    }

    @Test
    public void callFunction() {
        assertThat(device.callFunction("TestFunc1", ""), is(10));
    }

    @Test
    public void readIntVariable() {
        assertThat(device.readVariable("TestVarInt"), is("10"));
    }

    @Test
    public void readStrVariable() {
        assertThat(device.readVariable("TestVarStr"), is("hello"));
    }

    @Test
    public void readDblVariable() {
        assertThat(device.readVariable("TestVarDbl"), is("1.5"));
    }

    @Test
    public void receiveEventFromDevice() throws InterruptedException {
        final SparkEvent[] event = {null};
        eventStream = device.eventStream(e -> event[0] = e);
        device.callFunction("PubEvent", "args here");
        Instant start = Instant.now();
        while(event[0] == null && Duration.between(start, Instant.now()).getSeconds() < 10) {Thread.sleep(1);}
        assertThat(event[0].name, is("TestEvent"));
        assertThat(event[0].data, is("args here"));
        assertThat(event[0].coreid, is(device.deviceId));
        assertThat(event[0].ttl, is(60));
        assertThat(Duration.between(event[0].timestamp, Instant.now()).getSeconds(), lessThan(500L));
    }

    /*
    @Test(expected = SparkRestApi.InvalidVariableOrFunctionException.class, timeout = 3000)
    public void fakeFunctionThrowsInvalidVariableOrFunction() {
        device.callFunction("DoesNotExist", "");
    }

    @Test(expected = SparkRestApi.InvalidVariableOrFunctionException.class, timeout = 3000)
    public void fakeVariableThrowsInvalidVariableOrFunction() {
        device.readVariable("DoesNotExist");
    }
    */
}
