/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.helloworld.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import de.mdpm.oauth2.api.OauthService;

/**
 * The module that binds the OauthServiceModule so that it can be served.
 */
public class OauthServiceModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindServices(serviceBinding(OauthService.class, OauthServiceImpl.class));
  }
}
