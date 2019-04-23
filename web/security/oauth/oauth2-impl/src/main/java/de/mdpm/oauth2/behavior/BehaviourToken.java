package de.mdpm.oauth2.behavior;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import de.mdpm.oauth2.changed.ChangedToken;
import de.mdpm.oauth2.dto.ResponseToken;
import de.mdpm.oauth2.dto.UseRequestToken;
import sample.helloworld.impl.HelloCommand;
import sample.helloworld.impl.OauthEvent;
import sample.helloworld.impl.WorldState;

import java.time.LocalDateTime;
import java.util.Optional;

public class BehaviourToken extends PersistentEntity<HelloCommand, OauthEvent, WorldState> {

    @Override
    public Behavior initialBehavior(Optional<WorldState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(
                snapshotState.orElse(new WorldState("BehaviourAuth", LocalDateTime.now().toString())));

        /*
         * Command handler for the UseGreetingMessage command.
         */
        b.setCommandHandler(UseRequestToken.class, (cmd, ctx) ->
                ctx.thenPersist(new ChangedToken(cmd.grant_type, cmd.client_id, cmd.redirect_uri, cmd.client_secret, cmd.code),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(new ResponseToken("token_type", "access_token", "expiration_time", "user_id")))); // Done.getInstance()

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
