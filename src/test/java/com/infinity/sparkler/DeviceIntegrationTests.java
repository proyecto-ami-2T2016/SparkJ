package com.infinity.sparkler;

import com.infinity.sparkler.Tools.SparkCredentials;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceIntegrationTests {
    private SparkDevice device;

    @Before
    public void setup() throws IOException {
        SparkCredentials credentials = new SparkCredentials("credentials.txt");
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

    @Test(expected = SparkRestApi.InvalidVariableOrFunctionException.class, timeout = 3000)
    public void fakeFunctionThrowsInvalidVariableOrFunction() {
        device.callFunction("DoesNotExist", "");
    }

    @Test(expected = SparkRestApi.InvalidVariableOrFunctionException.class, timeout = 3000)
    public void fakeVariableThrowsInvalidVariableOrFunction() {
        device.readVariable("DoesNotExist");
    }
}
