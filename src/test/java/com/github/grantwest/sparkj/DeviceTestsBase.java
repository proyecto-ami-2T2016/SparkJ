package com.infinity.sparkj;

import org.junit.Before;

import java.util.Arrays;

public abstract class DeviceTestsBase extends SessionTestsBase {
    protected static final String deviceId = "1234567890";
    protected ISparkDevice device;

    @Before
    public void SetupDevice() {
        connectSession();
        device = new SparkDevice(deviceId, session);
    }

    private void connectSession() {
        expectTokenListRequest();
        respondWithTokenList(Arrays.asList(sparklerToken()));
        session.connect();
    }
}
