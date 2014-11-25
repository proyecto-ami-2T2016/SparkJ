package com.github.grantwest.sparkj.SparkCloudJsonObjects;

public interface IToken {
    String getKey();
    boolean isExpired();
    String getClientName();
}
