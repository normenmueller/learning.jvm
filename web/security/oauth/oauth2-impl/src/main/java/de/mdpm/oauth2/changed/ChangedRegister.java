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
public final class ChangedRegister implements OauthEvent {

    public final String type;
    public final String client_name;
    public final String client_url;
    public final String client_description;
    public final String redirect_url;

    @JsonCreator
    public ChangedRegister(String type, String client_name, String client_url, String client_description, String redirect_url) {
        this.type = Preconditions.checkNotNull(type, "type");
        this.client_name = Preconditions.checkNotNull(client_name, "client_name");
        this.client_url = Preconditions.checkNotNull(client_url, "client_url");
        this.client_description = Preconditions.checkNotNull(client_description, "client_description");
        this.redirect_url = Preconditions.checkNotNull(redirect_url, "redirect_url");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ChangedRegister && equalTo((ChangedRegister) another);
    }

    private boolean equalTo(ChangedRegister another) {
        return type.equals(another.type) && client_name.equals(another.client_name) && client_url.equals(another.client_url) && client_description.equals(another.client_description) && redirect_url.equals(another.redirect_url);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + type.hashCode() + client_name.hashCode() + client_url.hashCode() + client_description.hashCode() + redirect_url.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("ChangedRegister").add("type", type).add("client_name", client_name).add("client_url", client_url).add("client_description", client_description).add("redirect_url", redirect_url).toString();
    }
}

