/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package de.mdpm.oauth2.behavior;

import java.time.LocalDateTime;
import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import de.mdpm.oauth2.changed.ChangedRegister;
import de.mdpm.oauth2.dto.ResponseRegister;
import de.mdpm.oauth2.dto.UseRequestRegister;
import sample.helloworld.impl.HelloCommand;
import sample.helloworld.impl.OauthEvent;
import sample.helloworld.impl.WorldState;

public class BehaviourRegister extends PersistentEntity<HelloCommand, OauthEvent, WorldState> {

  @Override
  public Behavior initialBehavior(Optional<WorldState> snapshotState) {

    /*
     * Behaviour is defined using a behaviour builder. The behaviour builder
     * starts with a state, if this entity supports snapshotting (an
     * optimisation that allows the state itself to be persisted to combine many
     * events into one), then the passed in snapshotState may have a value that
     * can be used.
     *
     * Otherwise, the default state is to use the Hello greeting.
     */
    BehaviorBuilder b = newBehaviorBuilder(
        snapshotState.orElse(new WorldState("BehaviourRegister", LocalDateTime.now().toString())));

    /*
     * Command handler for the UseGreetingMessage command.
     */
    b.setCommandHandler(UseRequestRegister.class, (cmd, ctx) ->
      ctx.thenPersist(new ChangedRegister(cmd.type, cmd.client_name, cmd.client_url, cmd.client_description, cmd.redirect_url),
          // Then once the event is successfully persisted, we respond with done.
          evt -> ctx.reply(new ResponseRegister("CLIENT_ID", "SECRET", "ISSUED_AT", "EXPIRES_IN")))); // Done.getInstance()

    /*
     * Event handler for the GreetingMessageChanged event.
     */

//    b.setEventHandler(GreetingMessageChanged.class,
//        // We simply update the current state to use the greeting message from
//        // the event.
//        evt -> new WorldState(evt.message, LocalDateTime.now().toString()));

    /*
     * Command handler for the Hello command.
     */
//    b.setReadOnlyCommandHandler(Hello.class,
//        // Get the greeting from the current state, and prepend it to the name
//        // that we're sending
//        // a greeting to, and reply with that message.
//        (cmd, ctx) -> ctx.reply(state().message + ", " + cmd.name + "!"));

    /*
     * We've defined all our behaviour, so build and return it.
     */
    return b.build();
  }

}
