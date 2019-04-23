package de.mdpm.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ResponseQueryEndpoint {

    public final String granted;

    @JsonCreator
    public ResponseQueryEndpoint(String granted) {
        this.granted = granted;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ResponseQueryEndpoint && equalTo((ResponseQueryEndpoint) another);
    }

    private boolean equalTo(ResponseQueryEndpoint another) {
        return granted.equals(another.granted);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + granted.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("ResponseQueryEndpoint").add("granted", granted).toString();
    }

}
