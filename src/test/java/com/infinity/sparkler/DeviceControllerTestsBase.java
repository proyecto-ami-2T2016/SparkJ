package com.infinity.sparkler;

import org.junit.Before;

import java.util.Arrays;

public abstract class DeviceControllerTestsBase extends SessionTestsBase {
    protected static final String deviceId = "1234567890";
    protected ISparkController controller;

    @Before
    public void before() {
        connectSession();
        controller = new SparkController(session);
    }

    private void connectSession() {
        expectTokenListRequest();
        respondWithTokenList(Arrays.asList(sparklerToken()));
        //session.connect();
    }
}
