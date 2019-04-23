package de.mdpm.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ResponseRegister {
    public final String client_id;
    public final String secret;
    public final String issued_at;
    public final String expires_in;

    @JsonCreator
    public ResponseRegister(String client_id, String secret, String issued_at, String expires_in) {
        this.client_id = Preconditions.checkNotNull(client_id, "client_id");
        this.secret = Preconditions.checkNotNull(secret, "secret");
        this.issued_at = Preconditions.checkNotNull(issued_at, "issued_at");
        this.expires_in = Preconditions.checkNotNull(expires_in, "expires_in");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ResponseRegister && equalTo((ResponseRegister) another);
    }

    private boolean equalTo(ResponseRegister another) {
        return client_id.equals(another.client_id) && secret.equals(another.secret) && issued_at.equals(another.issued_at) && expires_in.equals(another.expires_in);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + client_id.hashCode() + secret.hashCode() + issued_at.hashCode() + expires_in.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("ResponseRegister").add("client_id", client_id).add("secret", secret).add("issued_at", issued_at).add("expires_in", expires_in).toString();
    }
}
