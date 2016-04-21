package com.github.grantwest.sparkj.SparkCloudJsonObjects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SparkEvent {
    public String name;
    public String data;
    public int ttl;
    public String coreid;

    @JsonIgnore
    public Instant timestamp;

    @JsonSetter("published_at")
    public void setPublishedAt(String timestamp) {
        this.timestamp = Instant.parse(timestamp);
    }

    @JsonGetter("published_at")
    public String getPublishedAt() {
        return timestamp.toString();
    }

    public SparkEvent() {
    }

    @Override
    public String toString() {
        return "SparkEvent{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", ttl=" + ttl +
                ", published_at=" + getPublishedAt() +
                ", coreid='" + coreid + '\'' +
                '}';
    }
}
