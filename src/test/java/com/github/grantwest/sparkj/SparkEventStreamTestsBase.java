package com.github.grantwest.sparkj;

import org.junit.Before;

import java.util.Arrays;

public class SparkEventStreamTestsBase extends SessionTestsBase {

    @Before
    public void SetupDevice() {
        connectSession();
    }

    private void connectSession() {
        expectTokenListRequest();
        respondWithTokenList(Arrays.asList(sparklerToken()));
        session.connect();
    }
}
