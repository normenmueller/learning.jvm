package de.mdpm.oauth2.behavior;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import de.mdpm.oauth2.changed.ChangedAuth;
import de.mdpm.oauth2.dto.UseRequestAuth;
import sample.helloworld.impl.HelloCommand;
import sample.helloworld.impl.OauthEvent;
import sample.helloworld.impl.WorldState;

import java.time.LocalDateTime;
import java.util.Optional;

public class BehaviourAuth extends PersistentEntity<HelloCommand, OauthEvent, WorldState> {

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
                snapshotState.orElse(new WorldState("BehaviourAuth", LocalDateTime.now().toString())));

        /*
         * Command handler for the UseGreetingMessage command.
         */
        b.setCommandHandler(UseRequestAuth.class, (cmd, ctx) ->
                ctx.thenPersist(new ChangedAuth(cmd.client_id, cmd.redirect_uri, cmd.response_type),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply("RESPONSE"))); // Done.getInstance()

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
