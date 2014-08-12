package com.kamomileware.define.match.actor;

import akka.actor.Actor;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by pepe on 13/06/14.
 */
public interface ClientActor extends Actor {
    ClientActor setWebSocketSession(WebSocketSession wsSession);
}
