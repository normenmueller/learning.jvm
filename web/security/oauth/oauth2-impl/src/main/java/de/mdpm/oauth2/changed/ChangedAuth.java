package de.mdpm.oauth2.changed;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import sample.helloworld.impl.OauthEvent;

@Immutable
@JsonDeserialize
public final class ChangedAuth implements OauthEvent
{
    public final String client_id;
    public final String redirect_uri;
    public final String response_type;

    @JsonCreator
    public ChangedAuth(String client_id, String redirect_uri, String response_type) {
        this.client_id = Preconditions.checkNotNull(client_id, "client_id");
        this.redirect_uri = Preconditions.checkNotNull(redirect_uri, "redirect_uri");
        this.response_type = Preconditions.checkNotNull(response_type, "response_type");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ChangedAuth && equalTo((ChangedAuth) another);
    }

    private boolean equalTo(ChangedAuth another) {
        return client_id.equals(another.client_id) && redirect_uri.equals(another.redirect_uri) && response_type.equals(another.response_type);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + client_id.hashCode() + redirect_uri.hashCode() + response_type.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("ChangedAuth").add("client_id", client_id).add("redirect_uri", redirect_uri).add("response_type", response_type).toString();
    }
}
