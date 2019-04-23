package de.mdpm.oauth2.changed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import sample.helloworld.impl.OauthEvent;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ChangedQueryEndpoint implements OauthEvent {

    private String access_token;
    private String endpoint;

    @JsonCreator
    public ChangedQueryEndpoint(String access_token, String endpoint) {
        this.access_token = access_token;
        this.endpoint = endpoint;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ChangedQueryEndpoint && equalTo((ChangedQueryEndpoint) another);
    }

    private boolean equalTo(ChangedQueryEndpoint another) {
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
        return MoreObjects.toStringHelper("ChangedQueryEndpoint").add("access_token", access_token).add("endpoint", endpoint).toString();
    }

}
