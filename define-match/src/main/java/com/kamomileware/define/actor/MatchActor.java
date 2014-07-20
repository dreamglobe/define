package com.kamomileware.define.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.round.Round;
import com.kamomileware.define.model.round.RoundPhase;

import java.util.*;

import static com.kamomileware.define.term.TermRepository.*;
import static com.kamomileware.define.model.round.RoundPhase.*;
import static com.kamomileware.define.model.MessageTypes.*;

/**
 * Match Actor for coordinating and operate the match state. Match state refers to
 * players actors, definitions, votes, and points in the current turn.
 * Base state is modelled by {@link MatchFSM}.
 *
 * Created by pepe on 12/06/14.
 */
public class MatchActor extends MatchFSM {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final StoppedBehaviour stopBehavior = new StoppedBehaviour();
    private final WaitConfigureMatchBehaviour waitConfigureMatchBehaviour = new WaitConfigureMatchBehaviour();
    private final WaitResponseBehaviour waitResponseBehavior = new WaitResponseBehaviour();
    private final WaitVotesBehaviour waitVotesBehavior = new WaitVotesBehaviour();
    private final ShowResultBehaviour showResultBehavior = new ShowResultBehaviour();


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
            cancelLatch();
            this.sendUsers(STOP);
            round = null;
            this.switchBehaviour(this.stopBehavior);
        } else if (next == CONFIG) {
            round = new Round<>(MatchConfiguration.createDefault(), shuffleAndGet());
            this.switchBehaviour(this.waitConfigureMatchBehaviour);
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
            round = new Round<>(round, get(round.getRoundNumber()));
        } else if(next == END_MATCH){
            this.sendUsersFinalScores();
            this.round.endRound();
            self().tell(STOP, self());
        }
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

    /**
     * Behaviour for {@link RoundPhase#STOPPED} state
     */
    private class StoppedBehaviour implements Procedure<Object> {
        @Override
        public void apply(Object message) {
            if (message instanceof RegisterUser) {
                setState(CONFIG);
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
                }else{
                    sendUsers(message);
                }
            } else {
                aunhandled(message);
            }
        }
    }

    /**
     * Behaviour for {@link RoundPhase#CONFIG} state
     */
    private class WaitConfigureMatchBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message) {
            if(message instanceof RegisterUser){
                handleRegisterUser((RegisterUser) message, sender());
                sender().tell(new Starting(), self());
            }else if(message instanceof ClientStartMatch) {
                handleConfigureRound((ClientStartMatch)message);
                setState(PHASE_RESPONSE);
            } else {
                super.apply(message);
            }
        }
    }

    private void handleConfigureRound(ClientStartMatch message) {
        // First Round
        round = new Round<>(round, get(round.getRoundNumber()));
        round.setMatchConf(message.getConfig());
    }

    /**
     * Common behaviour for {@link RoundPhase#PHASE_RESPONSE} state
     */
    private class WaitResponseBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message) {
            if (message instanceof UserDefinition) {
                handleUserResponse(((UserDefinition) message).getResponse(), sender());
                if (round.hasEveryoneResponse()) {
                    cancelLatch();
                    setState(PHASE_VOTE);
                }
            } else if (message instanceof Latch) {
                if (round.canExtendPhase(PHASE_RESPONSE)) {
                    round.extendPhase(PHASE_RESPONSE);
                    startLatchExtend(PHASE_RESPONSE);
                } else {
                    if (round.hasAnyoneResponse()) {
                        setState(PHASE_VOTE);
                    } else {
                        handleEndVote();
                        setState(PHASE_RESULT);
                    }
                }
            } else if (message instanceof RegisterUser) {
                // TODO: model phase and time
                final long phaseMillisLeft = getPhaseMillisLeft(round.getPhaseTotalDuration(PHASE_RESPONSE));
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
                if(round.hasEveryoneVote()){
                    cancelLatch();
                    handleEndVote();
                    if(!round.isFinalRound()) {
                        setState(PHASE_RESULT);
                    } else {
                        setState(END_MATCH);
                    }
                }
            }else if(message instanceof Latch){
                handleEndVote();
                if(!round.isFinalRound()) {
                    setState(PHASE_RESULT);
                } else {
                    setState(STOPPED);
                }
            } else if(message instanceof RegisterUser){
                handleRegisterUser((RegisterUser) message, sender());
                long timeLeft = getPhaseMillisLeft(round.getPhaseTotalDuration(PHASE_VOTE));
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
                if (round.isEveryoneReadyInResult()) {
                    cancelLatch();
                    setState(PHASE_RESPONSE);
                }
            } else if (message instanceof Latch) {
                setState(PHASE_RESPONSE);
            } else if (message instanceof RegisterUser) {
                handleRegisterUser((RegisterUser) message, sender());
                final int defId = round.getCorrectDefinition().getDefId();
                final long phaseMillisLeft = getPhaseMillisLeft(round.getPhaseTotalDuration(PHASE_RESULT));
                final RegisterUserInShowScores msg = new RegisterUserInShowScores(
                        phaseMillisLeft, round.getTerm(), round.getRoundItemDefinitions(),
                        null, round.createPlayersScores(), defId);
                sender().tell(msg, self());
            } else {
                super.apply(message);
            }
        }
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
        final int defId = round.getCorrectDefinition().getDefId();
        this.sendUsers(new StartShowScores(timeLeft, scores, defId));
    }

    private void handleUserReady(UserReady message, ActorRef sender) {
        boolean isReady = round.setPlayerReadyInResult(sender, message.isReady());
        this.sendUsers(new UserReady(round.getPlayerPid(sender), isReady));
    }

    private void sendUsersFinalScores() {
        List<PlayerScore> scores = this.round.createPlayersScores();
        final int defId = round.getCorrectDefinition().getDefId();
        this.sendUsers(new ShowEndScores(scores, defId));
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
}