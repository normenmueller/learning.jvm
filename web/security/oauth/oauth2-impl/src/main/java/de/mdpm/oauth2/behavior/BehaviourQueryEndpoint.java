package de.mdpm.oauth2.behavior;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import de.mdpm.oauth2.changed.ChangedQueryEndpoint;
import de.mdpm.oauth2.dto.ResponseQueryEndpoint;
import de.mdpm.oauth2.dto.UseRequestQueryEndpoint;
import sample.helloworld.impl.HelloCommand;
import sample.helloworld.impl.OauthEvent;
import sample.helloworld.impl.WorldState;

import java.time.LocalDateTime;
import java.util.Optional;

public class BehaviourQueryEndpoint extends PersistentEntity<HelloCommand, OauthEvent, WorldState> {

    @Override
    public Behavior initialBehavior(Optional<WorldState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(
                snapshotState.orElse(new WorldState("BehaviourQueryEndpoint", LocalDateTime.now().toString())));

        b.setCommandHandler(UseRequestQueryEndpoint.class, (cmd, ctx) ->
                ctx.thenPersist(new ChangedQueryEndpoint(cmd.access_token, cmd.endpoint),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(new ResponseQueryEndpoint("true"))));

        return b.build();
    }

}
