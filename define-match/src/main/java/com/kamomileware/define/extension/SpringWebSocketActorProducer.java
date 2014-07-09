package com.kamomileware.define.extension;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import com.kamomileware.define.actor.ClientActor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.WebSocketSession;

/**
 * An actor producer that lets Spring create the Actor instances.
 */
public class SpringWebSocketActorProducer implements IndirectActorProducer {
    final ApplicationContext applicationContext;
    final String actorBeanName;
    final WebSocketSession wsSession;

    public SpringWebSocketActorProducer(ApplicationContext applicationContext,
                                        WebSocketSession wsSession,
                                        String actorBeanName) {
        this.applicationContext = applicationContext;
        this.wsSession = wsSession;
        this.actorBeanName = actorBeanName;
    }

    @Override
    public Actor produce() {
        return ((ClientActor) applicationContext.getBean(actorBeanName)).setWebSocketSession(wsSession);
    }

    @Override @SuppressWarnings("unchecked")
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}