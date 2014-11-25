package com.github.grantwest.sparkj.SparkCloudJsonObjects;

public abstract class TokenBase implements IToken {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof IToken)) return false;

        IToken that = (IToken) o;

        if (getClientName() != null ? !getClientName().equals(that.getClientName()) : that.getClientName() != null) return false;
        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getClientName() != null ? getClientName().hashCode() : 0);
        return result;
    }
}
