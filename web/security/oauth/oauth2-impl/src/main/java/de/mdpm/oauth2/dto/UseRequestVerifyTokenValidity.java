package de.mdpm.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import sample.helloworld.impl.HelloCommand;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class UseRequestVerifyTokenValidity implements HelloCommand, CompressedJsonable, PersistentEntity.ReplyType<ResponseVerifyTokenValidity> {

    public String access_token;

    @JsonCreator
    public UseRequestVerifyTokenValidity(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof UseRequestVerifyTokenValidity && equalTo((UseRequestVerifyTokenValidity) another);
    }

    private boolean equalTo(UseRequestVerifyTokenValidity another) {
        return access_token.equals(another.access_token);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + access_token.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("UseRequestVerifyTokenValidity").add("access_token", access_token).toString();
    }


}

