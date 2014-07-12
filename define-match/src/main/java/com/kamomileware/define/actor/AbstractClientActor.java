package com.kamomileware.define.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by pepe on 29/06/14.
 */
public abstract class AbstractClientActor extends UntypedActor implements ClientActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private static final ObjectMapper mapper = new ObjectMapper();

    protected WebSocketSession wsSession;
    protected String sessionId;
    protected String name;

    public AbstractClientActor() {
        super();
    }

    public AbstractClientActor(WebSocketSession wsSession) {
        super();
        this.setWebSocketSession(wsSession);
    }

    @Override
    public ClientActor setWebSocketSession(WebSocketSession wsSession) {
        this.wsSession =  wsSession;
        this.sessionId = wsSession.getId();
        this.name = wsSession.getPrincipal().getName();
        return this;
    }

    protected void sendClient(Object message) throws IOException {
        if(wsSession.isOpen()) {
            log.debug("Sending client %s message %s".format(this.name, message));
            wsSession.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } else {
            log.warning("Not sending client %s message %s. The session is close".format(this.name, message));
        }
    }
}
