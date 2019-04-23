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
public final class UseRequestToken implements HelloCommand, CompressedJsonable, PersistentEntity.ReplyType<ResponseToken> {
    public final String grant_type;
    public final String client_id;
    public final String redirect_uri;
    public final String client_secret;
    public final String code;

    @JsonCreator
    public UseRequestToken(String grant_type, String client_id, String redirect_uri, String client_secret, String code) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.client_secret = client_secret;
        this.code = code;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof UseRequestToken && equalTo((UseRequestToken) another);
    }

    private boolean equalTo(UseRequestToken another) {
        return grant_type.equals(another.grant_type) && client_id.equals(another.client_id) && redirect_uri.equals(another.redirect_uri) && client_secret.equals(another.client_secret) && code.equals(another.code);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + grant_type.hashCode() + client_id.hashCode() + redirect_uri.hashCode() + client_secret.hashCode() + code.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("UseRequestToken").add("grant_type", grant_type).add("client_id", client_id).add("redirect_uri", redirect_uri).add("client_secret", client_secret).add("code", code).toString();
    }
}
