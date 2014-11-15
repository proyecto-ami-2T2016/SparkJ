package com.infinity.sparkler;

public interface ISparkDevice
{
    public String readVariable(String variableName);
    public String callFunction(String functionName, String argument);
}
