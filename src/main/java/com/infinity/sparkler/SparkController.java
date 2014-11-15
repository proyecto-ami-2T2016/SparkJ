package com.infinity.sparkler;

public class SparkController implements ISparkController {

    private SparkCloudSession session;

    public SparkController(SparkCloudSession session) {
        this.session = session;
    }


    @Override
    public ISparkDevice getDevices() {
        return null;
    }

    @Override
    public String readVariable(String deviceId, String variableName) {
        return null;
    }

    @Override
    public String callFunction(String deviceId, String functionName, String argument) {
        return null;
    }
}
