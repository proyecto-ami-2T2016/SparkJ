package com.infinity.sparkler;

public class SparkDevice implements ISparkDevice {

    private SparkCloudSession session;
    private String deviceId;

    public SparkDevice(String id, String username, String password) {
        session = new SparkCloudSession(username, password);
        deviceId = id;
    }

    public SparkDevice(String id, SparkCloudSession session) {
        deviceId = id;
        this.session = session;
    }

    @Override
    public String readVariable(String variableName) {
        return null;
    }

    @Override
    public String callFunction(String functionName, String argument) {
        return null;
    }
}
