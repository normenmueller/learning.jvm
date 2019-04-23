package de.mdpm.oauth2.changed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import sample.helloworld.impl.OauthEvent;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ChangedRefreshToken implements OauthEvent {
    public String access_token;

    @JsonCreator
    public ChangedRefreshToken(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ChangedRefreshToken && equalTo((ChangedRefreshToken) another);
    }

    private boolean equalTo(ChangedRefreshToken another) {
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
        return MoreObjects.toStringHelper("ChangedRefreshToken").add("access_token", access_token).toString();
    }
}
