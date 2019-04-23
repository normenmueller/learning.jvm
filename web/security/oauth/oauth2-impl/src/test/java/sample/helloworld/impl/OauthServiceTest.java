/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.helloworld.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import de.mdpm.oauth2.api.OauthService;
import de.mdpm.oauth2.dto.RequestRegister;
import de.mdpm.oauth2.dto.ResponseQueryEndpoint;
import org.junit.Test;

public class OauthServiceTest {

  @Test
  public void shouldStorePersonalizedGreeting() throws Exception {
    withServer(defaultSetup().withCassandra(true), server -> {
      OauthService service = server.client(OauthService.class);

      //service.register().invoke().toCompletableFuture().get(5, SECONDS);
      ResponseQueryEndpoint responseQueryEndpoint = service.query_endpoint("1a2b3c4d5g", "http://localhost/api/users").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals(responseQueryEndpoint.granted, "true");

      /*
      String msg1 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hello, Alice!", msg1); // default greeting

      service.useGreeting("Alice").invoke(new RequestRegister("Hi")).toCompletableFuture().get(5, SECONDS);
      String msg2 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hi, Alice!", msg2);

      String msg3 = service.hello("Bob").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hello, Bob!", msg3); // default greeting
      */
    });
  }

}
