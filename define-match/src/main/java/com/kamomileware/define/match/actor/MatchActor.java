package com.kamomileware.define.match.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.MessageTypes;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.match.CardStack;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.round.Round;
import com.kamomileware.define.model.round.RoundPhase;
import com.kamomileware.define.model.round.TermDefinition;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kamomileware.define.model.MessageTypes.*;
import static com.kamomileware.define.model.round.RoundPhase.*;

/**
 * Match Actor for coordinating and operate the match state. Match state refers to
 * players actors, definitions, votes, and points in the current turn.
 * Base state is modelled by {@link MatchFSM}.
 *
 * Created by pepe on 12/06/14.
 */
@Service
@Component("crossgate")
@Scope("prototype")
public class MatchActor extends MatchFSM {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final StoppedBehaviour stopBehavior = new StoppedBehaviour();
    private final WaitResponseBehaviour waitResponseBehavior = new WaitResponseBehaviour();
    private final WaitVotesBehaviour waitVotesBehavior = new WaitVotesBehaviour();
    private final ShowResultBehaviour showResultBehavior = new ShowResultBehaviour();

    private final CardStack cardStack = new CardStack();

    @Autowired @Qualifier("dbWorkersRouter")
    private ActorRef dbWorkerRouter;

    /**
     * Sets the initial state becoming {@link MatchActor.StoppedBehaviour}
     */
    protected MatchActor() {
        this.getContext().become(new StoppedBehaviour());
    }

    /**
     * Contains the logic to apply when state change. Apply the behaviour associated to the state.
     * @param old previous state
     * @param next new state
     */
    @Override
    protected void transition(RoundPhase old, RoundPhase next) {
        if (next == STOPPED) {
            this.cancelLatch();
            this.sendUsers(STOP);
            this.round = null;
            this.switchBehaviour(this.stopBehavior);
        } else if (next == CONFIG) {
            this.round = Round.createEmptyRound();
            this.switchBehaviour(new WaitConfigureMatchBehaviour(getSender()));
        } else if (next == PHASE_RESPONSE) {
            this.switchBehaviour(this.waitResponseBehavior);
            long leftTime = this.startLatch(PHASE_RESPONSE);
            this.sendUsersTerm(leftTime);
        } else if (next == PHASE_VOTE) {
            this.switchBehaviour(this.waitVotesBehavior);
            long leftTime = this.startLatch(PHASE_VOTE);
            this.sendUsersDefinitions(leftTime);
        } else if (next == PHASE_RESULT) {
            this.switchBehaviour(this.showResultBehavior);
            long leftTime = this.startLatch(PHASE_RESULT);
            this.sendUsersScores(leftTime);
            final Term nextTerm = this.cardStack.drawTerm(this.round.getNextHandCategory());
            this.round = this.round.createNext(nextTerm);
            if(this.cardStack.cardsLeft() < 5) this.askForCards(10);
        } else if(next == END_MATCH){
            this.sendUsersFinalScores();
            this.round.endRound();
            self().tell(STOP, self());
        }
    }

    private void askForCards(int cardNbr) {
        dbWorkerRouter.tell(new DBGetCards(cardNbr, cardStack.getCardIds()), getSelf());
    }

    /**
     * Never used because the actor starts becoming {@link MatchActor.StoppedBehaviour}.
     * Other way send the message to the parent.
     * @see akka.actor.UntypedActor#onReceive(Object)
     */
    @Override
    public void onReceive(Object message) throws Exception {
        unhandled(message);
    }

    /**
     * In this versión unhandled message goes to log for avoiding the actor restart
     * @param message message to handle in last instance
     */
    public void aunhandled(Object message){
        log.error("unHandled message: {}", message.toString());
    }

    /* Private methods for handling message */
    private void handleRegisterUser(RegisterUser message, ActorRef sender) {
        this.sendUsers(message);
        round.addPlayer(sender(), message.getName(), message.getPid());
        sender.tell(new PlayerList(round.createPlayersInfo()), this.self());
    }

    private void sendUsersTerm(long phaseMillisLeft) {
        this.sendUsers(new StartDefinition(phaseMillisLeft, round.getTerm()));
    }

    private void handleUserResponse(String definition, ActorRef sender) {
        round.setPlayerDefinition(sender, definition);
        final String pid = round.getPlayerPid(sender);
        this.sendUsers(new UserDefinition(pid));
    }

    private void sendUsersDefinitions(final long timeLeft) {
        round.createRoundDefinitions();
        round.applyPlayers(p -> p.getRef().tell(
                new StartVote(timeLeft, round.getDefinitionsForPlayer(p), p.createItemDefinition()),
                this.self()));
    }

    private void handleVote(UserVote message) {
        final Integer voteId = message.getVoteId();
        round.playerVote(this.sender(), voteId);
        sendUsers(new UserVote(round.getPlayerPid(this.sender()), voteId));
    }

    private void handleEndVote(){
        this.round.applyVotes();
        this.round.calculateRoundResult();
    }

    /**
     * Send th message to al register player actor in the match
     */
    private void sendUsersScores(long timeLeft) {
        List<PlayerScore> scores = this.round.createPlayersScores();
        final ItemDefinition correctDefinition = TermDefinition.createItemDefinition(round.getCorrectDefinition());
        this.sendUsers(new StartShowScores(timeLeft, scores, correctDefinition));
    }

    private void handleUserReady(UserReady message, ActorRef sender) {
        boolean isReady = round.setPlayerReadyInResult(sender, message.isReady());
        this.sendUsers(new UserReady(round.getPlayerPid(sender), isReady));
    }

    private void sendUsersFinalScores() {
        List<PlayerScore> scores = this.round.createPlayersScores();
        final ItemDefinition def = TermDefinition.createItemDefinition(round.getCorrectDefinition());
        this.sendUsers(new ShowEndScores(scores, def));
    }

    /**
     * Send the message to al register player actor in the match
     * @param message to send to every user player in the match
     */
    private void sendUsers(Object message) {
        round.applyPlayersRefs(a -> a.tell(message, this.self()));
    }

    /* Static Members */
    /**
     * Builder pattern for props
     * @return Props for Match actor
     */
    public static Props props() {
        return Props.create(MatchActor.class);
    }

    /**
     * Behaviour for {@link RoundPhase#STOPPED} state
     */
    private class StoppedBehaviour implements Procedure<Object> {
        @Override
        public void apply(Object message) {
            if (message instanceof RegisterUser) {
                setState(CONFIG);
                askForCards(40);
                handleRegisterUser((RegisterUser) message, sender());
                sender().tell(new Starting(MatchConfiguration.createDefault()), sender());
            } else {
                aunhandled(message);
            }
        }
    }

    /**
     * Common behaviour for non {@link RoundPhase#STOPPED} state
     */
    private abstract class CommonStartedBehaviour implements Procedure<Object> {
        @Override
        public void apply(Object message) {
            if (message instanceof Stop) {
                sendUsers(message);
                setState(STOPPED);
            } else if (message instanceof RemoveUser) {
                round.removePlayer(sender());
                if (round.getPlayerList().isEmpty()) {
                    round.endRound();
                    self().tell(STOP, self());
                } else {
                    sendUsers(message);
                }
            }else if(message instanceof DBCards){
                cardStack.addCards(((DBCards)message).getCards());
            } else {
                aunhandled(message);
            }
        }
    }

    /**
     * Behaviour for {@link RoundPhase#CONFIG} state
     */
    private class WaitConfigureMatchBehaviour extends CommonStartedBehaviour {
        private final ActorRef starter;
        private boolean canStart = false;

        private WaitConfigureMatchBehaviour(ActorRef starter) {
            this.starter = starter;
        }

        @Override
        public void apply(Object message) {
            if(message instanceof RegisterUser){
                handleRegisterUser((RegisterUser) message, sender());
                sender().tell(new Starting(), self());
            } else if(message instanceof DBCards){
                cardStack.addCards(((DBCards)message).getCards());
                starter.tell(new CanStartMatch(), getSelf());
                canStart = true;
            } else if(canStart && message instanceof ClientStartMatch) {
                final ClientStartMatch startMatch = (ClientStartMatch) message;
                round = round.createNext(cardStack.drawTerm());
                round.setMatchConf(startMatch.getConfig());
                setState(PHASE_RESPONSE);
            } else {
                super.apply(message);
            }
        }
    }

    /**
     * Common behaviour for {@link RoundPhase#PHASE_RESPONSE} state
     */
    private class WaitResponseBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message) {
            if (message instanceof UserDefinition) {
                handleUserResponse(((UserDefinition) message).getResponse(), sender());
                if (round.isFastEnd(PHASE_RESPONSE)) {
                    cancelLatch();
                    setState(PHASE_VOTE);
                }
            } else if (message instanceof Latch) {
                if (round.extendPhase(PHASE_RESPONSE)) {
                    final long extended = startLatchExtend(PHASE_RESPONSE);
                    sendUsers(new ExtendTime(extended));
                } else {
                    if (round.hasAnyoneResponse()) {
                        setState(PHASE_VOTE);
                    } else {
                        handleEndVote();
                        setState(PHASE_RESULT);
                    }
                }
            } else if (message instanceof RegisterUser) {
                final long phaseDurationInMillis = round.getPhaseDurationInMillis(PHASE_RESPONSE);
                final long phaseMillisLeft = getPhaseMillisLeft(phaseDurationInMillis);
                handleRegisterUser((RegisterUser) message, sender());
                sender().tell(new StartDefinition(phaseMillisLeft, round.getTerm()), self());
            } else {
                super.apply(message);
            }
        }
    }

    /**
     * Common behaviour for {@link RoundPhase#PHASE_VOTE} state
     */
    private class WaitVotesBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message){
            if(message instanceof UserVote){
                handleVote((UserVote) message);
                if(round.isFastEnd(PHASE_VOTE)){
                    cancelLatch();
                    handleEndVote();
                    if(!round.isFinalRound()) {
                        setState(PHASE_RESULT);
                    } else {
                        setState(END_MATCH);
                    }
                }
            }else if(message instanceof Latch){
                if (round.extendPhase(PHASE_VOTE)) {
                    final long extended = startLatchExtend(PHASE_VOTE);
                    sendUsers(new ExtendTime(extended));
                } else {
                    handleEndVote();
                    if (!round.isFinalRound()) {
                        setState(PHASE_RESULT);
                    } else {
                        setState(STOPPED);
                    }
                }
            } else if(message instanceof RegisterUser){
                handleRegisterUser((RegisterUser) message, sender());
                long timeLeft = getPhaseMillisLeft(round.getPhaseDurationInMillis(PHASE_VOTE));
                final RegisterUserInVote msg = new RegisterUserInVote(timeLeft, round.getTerm(),
                        round.getRoundItemDefinitions(), null);
                sender().tell(msg, self());
            } else {
                super.apply(message);
            }
        }
    }

    /**
     * Common behaviour for {@link RoundPhase#PHASE_RESULT} state
     */
    private class ShowResultBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message) {
            if (message instanceof UserReady) {
                handleUserReady(((UserReady) message), sender());
                if (round.isFastEnd(PHASE_RESULT)) {
                    cancelLatch();
                    setState(PHASE_RESPONSE);
                }
            } else if (message instanceof Latch) {
                if (round.extendPhase(PHASE_RESULT)) {
                    final long extended = startLatchExtend(PHASE_RESULT);
                    sendUsers(new ExtendTime(extended));
                } else {
                    setState(PHASE_RESPONSE);
                }
            } else if (message instanceof RegisterUser) {
                handleRegisterUser((RegisterUser) message, sender());
                final ItemDefinition defId = TermDefinition.createItemDefinition(round.getCorrectDefinition());
                final long phaseMillisLeft = getPhaseMillisLeft(round.getPhaseDurationInMillis(PHASE_RESULT));
                final RegisterUserInShowScores msg = new RegisterUserInShowScores(
                        phaseMillisLeft, round.getPreviousTerm(), round.getRoundItemDefinitions(),
                        null, round.createPlayersScores(), defId);
                sender().tell(msg, self());
            } else {
                super.apply(message);
            }
        }
    }
}