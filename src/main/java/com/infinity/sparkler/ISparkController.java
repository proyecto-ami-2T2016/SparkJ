package com.infinity.sparkler;

public interface ISparkController
{
    public ISparkDevice getDevices();
    public String readVariable(String deviceId, String variableName);
    public String callFunction(String deviceId, String functionName, String argument);
}
