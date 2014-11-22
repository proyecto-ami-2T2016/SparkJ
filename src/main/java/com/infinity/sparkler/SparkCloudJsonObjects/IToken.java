package com.infinity.sparkler.SparkCloudJsonObjects;

public interface IToken {
    String getKey();
    boolean isExpired();
    String getClientName();
}
