/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package de.mdpm.oauth2.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import de.mdpm.oauth2.dto.*;

/**
 * The hello service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the OauthService.
 */
public interface OauthService extends Service {

  /**
   curl -H "Content-Type: application/json" -X POST -d '{
       "type": "pull",
       "client_name": "test-app",
       "client_url": "localhost:9000",
       "client_description": "my test application",
       "redirect_url": "localhost:8080/myService/redirectUrlAfterLogin"
    }' http://localhost:9000/api/oauth2/register
   */
  ServiceCall<RequestRegister, ResponseRegister> register();

  /**
   http://localhost:9000/api/oauth2/auth?client_id=abcd&redirect_uri=http://localhost:9000/endpoint&response_type=code

   curl -G 'http://localhost:9000/api/oauth2/auth' --data-urlencode 'client_id=abcd' --data-urlencode 'redirect_uri=http://localhost:9000/endpoint' --data-urlencode 'response_type=code'
   */
  ServiceCall<NotUsed, String> auth(String client_id, String redirect_uri, String response_type);

  /**
   curl -H "Content-Type: application/json" -X POST -d '{
       "grant_type": "authorization_code",
       "client_id": "123456789",
       "redirect_uri": "http://localhost:9000/endpoint",
       "client_secret": "1a2b3c4d5e",
       "code": "123456789"
   }' http://localhost:9000/api/oauth2/token
   */
  ServiceCall<RequestToken, ResponseToken> token();

  /**
   curl -G 'http://localhost:9000/api/oauth2/refresh_token' --data-urlencode 'access_token=1a2b3c4d5f6g7h'
   */
  ServiceCall<NotUsed, ResponseRefreshToken> refresh_token(String access_token);

  /**
   curl -G 'http://localhost:9000/api/oauth2/verify_token_validity' --data-urlencode 'access_token=1a2b3c4d5f6g7h'
   */
  ServiceCall<NotUsed, ResponseVerifyTokenValidity> verify_token_validity(String access_token);

  /**
   curl -G 'http://localhost:9000/api/oauth2/query_endpoint' --data-urlencode 'access_token=1a2b3c4d5f6g7h' --data-urlencode 'endpoint=http//localhost/api/users'
  */
  ServiceCall<NotUsed, ResponseQueryEndpoint> query_endpoint(String access_token, String endpoint);

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("helloservice").withCalls(
        pathCall("/api/oauth2/register", this::register),
        pathCall("/api/oauth2/auth?client_id&redirect_uri&response_type", this::auth),
        pathCall("/api/oauth2/token", this::token),
        pathCall("/api/oauth2/refresh_token?access_token", this::refresh_token),
        pathCall("/api/oauth2/verify_token_validity?access_token", this::verify_token_validity),
        pathCall("/api/oauth2/query_endpoint?access_token&endpoint", this::query_endpoint)
      ).withAutoAcl(true);
    // @formatter:on
  }
}
