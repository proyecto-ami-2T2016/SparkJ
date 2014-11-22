package com.infinity.sparkler.IntegrationTests;

import com.infinity.sparkler.SparkDevice;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SparkDeviceIntegrationTests {
    private SparkCredentials credentials;
    private SparkDevice device;

    @Before
    public void setup() throws IOException {
        credentials = new SparkCredentials("credentials.txt");
        device = new SparkDevice(credentials.deviceId, credentials.username, credentials.password);
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
}
