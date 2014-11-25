package com.infinity.sparkj.SparkCloudJsonObjects;

public interface IToken {
    String getKey();
    boolean isExpired();
    String getClientName();
}
