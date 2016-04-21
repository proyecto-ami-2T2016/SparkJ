package com.github.grantwest.sparkj;

import org.junit.Before;
import java.util.Arrays;

public abstract class DeviceTestsBase extends SessionTestsBase {
    protected static final String deviceId = "1234567890";
    protected SparkDevice device;

    @Before
    public void SetupDevice() {
        connectSession();
        device = new SparkDevice(deviceId, session);
    }

    private void connectSession() {
        expectTokenListRequest();
        respondWithTokenList(Arrays.asList(sparkjToken()));
        session.connect();
    }
}
