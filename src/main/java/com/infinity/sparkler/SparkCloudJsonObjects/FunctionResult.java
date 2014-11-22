package com.infinity.sparkler.SparkCloudJsonObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionResult {
    public String id;
    public String name;
    public boolean connected;
    public int return_value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionResult that = (FunctionResult) o;

        if (connected != that.connected) return false;
        if (return_value != that.return_value) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (connected ? 1 : 0);
        result = 31 * result + return_value;
        return result;
    }
}
