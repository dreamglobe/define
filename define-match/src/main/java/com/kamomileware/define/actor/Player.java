package com.kamomileware.define.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamomileware.define.extension.SpringWSocketClientExtension;
import com.kamomileware.define.model.MessageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Optional;

import static com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS;
import static com.kamomileware.define.extension.SpringWSocketClientExtension.SpringExtProvider;
import static com.kamomileware.define.model.MessageTypes.*;

/**
 * Created by pepe on 13/06/14.
 */
@Named("userActor")
@Scope("prototype")
public class Player extends AbstractClientActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Inject @Named("matchActor") ActorRef match;

    protected Player() {
        super();
    }

    protected Player(WebSocketSession wsSession) {
        super(wsSession);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof RegisterUser
        || message instanceof RemoveUser
        || message instanceof PlayerList
        || message instanceof UserDefinition
        || message instanceof UserVote
        || message instanceof UserReady
        || message instanceof StartDefinition
        || message instanceof StartVote
        || message instanceof StartShowScores
                ){
            sendClient(message);
        } else if(message instanceof ClientResponse){
            final String response = ((ClientResponse) message).getResponse();
            match.tell(new UserDefinition(this.sessionId, response), self());
        } else if(message instanceof ClientVote){
            final Integer voteId = ((ClientVote) message).getVoteId();
            match.tell(new UserVote(this.sessionId, voteId), self());
        } else if(message instanceof ClientReady){
            final Boolean ready = ((ClientReady) message).isReady();
            match.tell(new UserReady(this.sessionId, ready), self());
        } else if(message instanceof Start){
            match.tell(new RegisterUser(this.sessionId, this.name), self());
        } else if(message instanceof Stop){
            match.tell(new RemoveUser(sessionId), self());
            getContext().stop(this.self());
        } else {
            unhandled(message);
        }
    }

    /** Static Methods **/

    static Props props() {
        return Props.create(Player.class);
    }

    static Props props(WebSocketSession wsSession) {
        return Props.create(Player.class, wsSession);
    }

    @Controller
    public static class ClientHandler extends TextWebSocketHandler {

        private final static ObjectMapper objectMapper = new ObjectMapper().configure(USE_ANNOTATIONS, true);

        @Autowired
        private ActorSystem system;
        private Optional<ActorRef> player = Optional.ofNullable(null);
        private String name = null;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            final SpringWSocketClientExtension.SpringExt springExt = SpringExtProvider.get(system);
            name = session.getPrincipal().getName();
            this.player = Optional.ofNullable(system.actorOf(springExt.props(session, "userActor"), name));
            this.sendMessageToPlayer(MessageTypes.START);

        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            MessageTypes.ClientMessage clientMsg = buildMessage(message.getPayload());
            this.sendMessageToPlayer(clientMsg, ActorRef.noSender());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            this.sendMessageToPlayer(MessageTypes.STOP, ActorRef.noSender());
        }

        @RequestMapping("/login")
        @ResponseBody
        public String handleLogin(@RequestBody String name){
            // TODO: check names to avoid duplications
            Authentication authentication = new UsernamePasswordAuthenticationToken(name, null,
                    AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return RequestContextHolder.currentRequestAttributes().getSessionId();
        }

        private MessageTypes.ClientMessage buildMessage(String messageStr) throws java.io.IOException {
            return objectMapper.readValue(messageStr, MessageTypes.ClientMessage.class);
        }

        private void sendMessageToPlayer(Object message) {
            this.player.ifPresent(p -> p.tell(message, p));
        }

        private void sendMessageToPlayer(Object message, ActorRef sender) {
            this.player.ifPresent(p -> p.tell(message, sender));
        }

    }
}
