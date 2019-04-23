package de.mdpm.oauth2.behavior;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import de.mdpm.oauth2.changed.ChangedQueryEndpoint;
import de.mdpm.oauth2.changed.ChangedVerifyTokenValidity;
import de.mdpm.oauth2.dto.ResponseQueryEndpoint;
import de.mdpm.oauth2.dto.ResponseVerifyTokenValidity;
import de.mdpm.oauth2.dto.UseRequestQueryEndpoint;
import de.mdpm.oauth2.dto.UseRequestVerifyTokenValidity;
import sample.helloworld.impl.HelloCommand;
import sample.helloworld.impl.OauthEvent;
import sample.helloworld.impl.WorldState;

import java.time.LocalDateTime;
import java.util.Optional;

public class BehaviourVerifyTokenValidity extends PersistentEntity<HelloCommand, OauthEvent, WorldState> {

    @Override
    public Behavior initialBehavior(Optional<WorldState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(
                snapshotState.orElse(new WorldState("BehaviourVerifyTokenValidity", LocalDateTime.now().toString())));

        b.setCommandHandler(UseRequestVerifyTokenValidity.class, (cmd, ctx) ->
                ctx.thenPersist(new ChangedVerifyTokenValidity(cmd.access_token),
                        evt -> ctx.reply(new ResponseVerifyTokenValidity("true")))); // valid: true

        return b.build();
    }

}
