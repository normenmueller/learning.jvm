package de.mdpm.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public class ResponseToken {

    public final String token_type;
    public final String access_token;
    public final String expires_in;
    public final String user_id;

    @JsonCreator
    public ResponseToken(String token_type, String access_token, String expires_in, String user_id) {
        this.token_type = token_type;
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.user_id = user_id;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof ResponseToken && equalTo((ResponseToken) another);
    }

    private boolean equalTo(ResponseToken another) {
        return token_type.equals(another.token_type) && access_token.equals(another.access_token) && expires_in.equals(another.expires_in) && user_id.equals(another.user_id);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + token_type.hashCode() + access_token.hashCode() + expires_in.hashCode() + user_id.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("ResponseToken").add("token_type", token_type).add("access_token", access_token).add("expires_in", expires_in).add("user_id", user_id).toString();
    }


}
