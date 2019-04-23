package de.mdpm.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ResponseVerifyTokenValidity {

    public final String valid;

    @JsonCreator
    public ResponseVerifyTokenValidity(String valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ResponseVerifyTokenValidity && equalTo((ResponseVerifyTokenValidity) another);
    }

    private boolean equalTo(ResponseVerifyTokenValidity another) {
        return valid.equals(another.valid);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + valid.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("ResponseVerifyTokenValidity").add("valid", valid).toString();
    }

}
