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
public class UseRequestQueryEndpoint implements HelloCommand, CompressedJsonable, PersistentEntity.ReplyType<ResponseQueryEndpoint> {

    public String access_token;
    public String endpoint;

    @JsonCreator
    public UseRequestQueryEndpoint(String access_token, String endpoint) {
        this.access_token = access_token;
        this.endpoint = endpoint;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof UseRequestQueryEndpoint && equalTo((UseRequestQueryEndpoint) another);
    }

    private boolean equalTo(UseRequestQueryEndpoint another) {
        return access_token.equals(another.access_token) && endpoint.equals(another.endpoint);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + access_token.hashCode() + endpoint.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("UseRequestQueryEndpoint").add("access_token", access_token).add("endpoint", endpoint).toString();
    }

}
