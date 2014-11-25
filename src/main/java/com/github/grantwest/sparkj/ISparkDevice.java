package com.infinity.sparkj;

public interface ISparkDevice
{
    public String readVariable(String variableName);
    public int callFunction(String functionName, String argument);
}
