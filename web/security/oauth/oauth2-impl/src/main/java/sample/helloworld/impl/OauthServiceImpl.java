/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.helloworld.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import de.mdpm.oauth2.behavior.*;
import de.mdpm.oauth2.dto.*;
import de.mdpm.oauth2.api.OauthService;

/**
 * Implementation of the OauthService.
 */
public class OauthServiceImpl implements OauthService {

  private final PersistentEntityRegistry persistentEntityRegistry;

  @Inject
  public OauthServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
    this.persistentEntityRegistry = persistentEntityRegistry;
    persistentEntityRegistry.register(BehaviourRegister.class);
    persistentEntityRegistry.register(BehaviourAuth.class);
    persistentEntityRegistry.register(BehaviourToken.class);
    persistentEntityRegistry.register(BehaviourRefreshToken.class);
    persistentEntityRegistry.register(BehaviourVerifyTokenValidity.class);
    persistentEntityRegistry.register(BehaviourQueryEndpoint.class);
  }

  @Override
  public ServiceCall<RequestRegister, ResponseRegister> register() {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(BehaviourRegister.class, request.client_name);
      // Tell the entity to use the greeting message specified.
      //return ref.ask(new UseGreetingMessage(request.client_name));
      return ref.ask(new UseRequestRegister(request.type, request.client_name, request.client_url, request.client_description, request.redirect_url));
    };
  }

  @Override
  public ServiceCall<NotUsed, String> auth(String client_id, String redirect_uri, String response_type) {
    return request -> {
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(BehaviourAuth.class, client_id);
      return ref.ask(new UseRequestAuth(client_id, redirect_uri, response_type));
    };
  }

  @Override
  public ServiceCall<RequestToken, ResponseToken> token() {
    return request -> {
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(BehaviourToken.class, request.client_id);
      return ref.ask(new UseRequestToken(request.grant_type, request.client_id, request.redirect_uri, request.client_secret, request.code));
    };
  }

  @Override
  public ServiceCall<NotUsed, ResponseRefreshToken> refresh_token(String access_token) {
    return request -> {
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(BehaviourRefreshToken.class, access_token);
      return ref.ask(new UseRequestRefreshToken(access_token));
    };
  }

  @Override
  public ServiceCall<NotUsed, ResponseVerifyTokenValidity> verify_token_validity(String access_token) {
    return request -> {
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(BehaviourVerifyTokenValidity.class, access_token);
      return ref.ask(new UseRequestVerifyTokenValidity(access_token));
    };
  }

  @Override
  public ServiceCall<NotUsed, ResponseQueryEndpoint> query_endpoint(String access_token, String endpoint) {
    return request -> {
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(BehaviourQueryEndpoint.class, access_token);
      return ref.ask(new UseRequestQueryEndpoint(access_token, endpoint));
    };
  }

}
