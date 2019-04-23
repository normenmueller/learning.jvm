package de.mdpm.oauth2.changed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import de.mdpm.oauth2.dto.UseRequestVerifyTokenValidity;
import sample.helloworld.impl.OauthEvent;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ChangedVerifyTokenValidity implements OauthEvent {

    public String access_token;

    @JsonCreator
    public ChangedVerifyTokenValidity(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ChangedVerifyTokenValidity && equalTo((ChangedVerifyTokenValidity) another);
    }

    private boolean equalTo(ChangedVerifyTokenValidity another) {
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
        return MoreObjects.toStringHelper("ChangedVerifyTokenValidity").add("access_token", access_token).toString();
    }

}
