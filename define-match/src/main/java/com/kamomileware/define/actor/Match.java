package com.kamomileware.define.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.round.MatchConfiguration;
import com.kamomileware.define.model.round.Round;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCategory;

import java.util.*;

import static com.kamomileware.define.actor.RoundPhase.*;
import static com.kamomileware.define.model.MessageTypes.*;

/**
 * Match Actor for coordinating and operate the match state. Match state refers to
 * players actors, definitions, votes, and points in the current turn.
 * Base state is modelled by {@link MatchFSM}.
 *
 * Created by pepe on 12/06/14.
 */
public class Match extends MatchFSM {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Term term = new Term("recañí", "Ventana", TermCategory.CH);

    private final StoppedBehaviour stopBehavior = new StoppedBehaviour();
    private final WaitResponseBehaviour waitResponseBehavior = new WaitResponseBehaviour();
    private final WaitVotesBehaviour waitVotesBehavior = new WaitVotesBehaviour();
    private final ShowResultBehaviour showResultBehavior = new ShowResultBehaviour();

    private Round round ;

    /**
     * Sets the initial state becoming {@link com.kamomileware.define.actor.Match.StoppedBehaviour}
     */
    protected Match() {
        this.getContext().become(new StoppedBehaviour());
    }

    /**
     * Never used because the actor starts becoming {@link com.kamomileware.define.actor.Match.StoppedBehaviour}.
     * Other way send the message to the parent.
     * @see akka.actor.UntypedActor#onReceive(Object)
     */
    @Override
    public void onReceive(Object message) throws Exception {
        unhandled(message);
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
        } else if (next == PHASE_RESPONSE) {
            round = new Round(term, round);
            this.sendUsers(new StartDefinition(round.getPhaseTotalDuration(PHASE_RESPONSE), term));
            this.switchBehaviour(this.waitResponseBehavior);
            this.startLatch();
        } else if (next == PHASE_VOTE) {
            this.sendUsersDefinitions();
            this.switchBehaviour(this.waitVotesBehavior);
            this.startLatch();
        } else if (next == PHASE_RESULT) {
            this.sendUsersResults();
            this.switchBehaviour(this.showResultBehavior);
            this.startLatch();
        }
    }

    /**
     * In this versión unhandled message goes to log for avoiding the actor restart
     * @param message message to handle in last instance
     */
    public void aunhandled(Object message){
        log.error("unHandled message: " + message.toString());
    }

    /**
     * Behaviour for {@link RoundPhase#STOPPED} state
     */
    private class StoppedBehaviour implements Procedure<Object> {
        public void apply(Object message) {
            if (message instanceof RegisterUser) {
                round = new Round(term, MatchConfiguration.createMatchConfiguration());
                handleRegisterUser((RegisterUser) message, sender());
                setState(PHASE_RESPONSE);
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
                int left = round.removePlayerData(sender());
                sendUsers(message);
                if ( left == 0) {
                    self().tell(STOP, self());
                }
            } else {
                aunhandled(message);
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
                if (round.hasEveryoneResponse()) {
                    cancelLatch();
                    setState(PHASE_VOTE);
                }
            } else if (message instanceof Latch) {
                if (round.canExtendPhase(PHASE_RESPONSE)) {
                    round.extendPhase(PHASE_RESPONSE);
                    startLatchExtend();
                } else {
                    setState(round.hasAnyoneResponse() ? PHASE_VOTE : PHASE_RESULT);
                }
            } else if (message instanceof RegisterUser) {
                // TODO: model phase and time
                final long phaseMillisLeft = getPhaseMillisLeft(round.getPhaseTotalDuration(PHASE_RESPONSE));
                handleRegisterUser((RegisterUser) message, sender());
                sender().tell(new StartDefinition(phaseMillisLeft, term), self());
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
                if(round.hasEveryonrVote()){
                    cancelLatch();
                    setState(PHASE_RESULT);
                }
            }else if(message instanceof Latch){
                setState(PHASE_RESULT); // TODO: Config phase
            } else if(message instanceof RegisterUser){
                handleRegisterUser((RegisterUser) message, sender());
                long timeLeft = getPhaseMillisLeft(round.getPhaseTotalDuration(PHASE_VOTE));
                sender().tell(new StartVote(timeLeft, round.getRoundItemDefinitions(), null), self());
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
                final int defId = round.getCorrectDefinition().getId();
                final long phaseMillisLeft = getPhaseMillisLeft(round.getPhaseTotalDuration(PHASE_RESULT));
                sender().tell(new ResultForRegisterUser(
                        defId, round.createPlayersScores(),
                        round.getRoundItemDefinitions(),
                        phaseMillisLeft), self());
            } else {
                super.apply(message);
            }
        }
    }

    /* Private methods for handling message */
    private void handleRegisterUser(RegisterUser message, ActorRef sender) {
        this.sendUsers(message);
        round.addPlayerData(sender(), message.getName(), message.getPid());
        sender.tell(new UsersList(round.createPlayersInfo()), this.self());
    }

    private void handleUserResponse(String definition, ActorRef sender) {
        round.setPlayerDefinition(sender, definition);
        final String pid = round.getPlayerPidByRef(sender);
        this.sendUsers(new UserDefinition(pid));
    }

    private void sendUsersDefinitions() {
        round.buildRoundDefinitions();
        round.applyPlayers(p -> p.getRef().tell(
                new StartVote(PHASE_DURATION.toMillis(),
                        round.getDefinitionsForPlayer(p),
                        p.createItemDefinition()),
                this.self()));
    }

    private void handleVote(UserVote message) {
        final Integer voteId = message.getVoteId();
        round.playerVote(this.sender(), voteId);
        sendUsers(new UserVote(round.getPlayerPidByRef(this.sender()), voteId));
    }

    private void handleUserReady(UserReady message, ActorRef sender) {
        boolean isReady = round.setPlayerReadyInResult(sender, message.isReady());
        this.sendUsers(new UserReady(round.getPlayerPidByRef(sender), isReady));
    }

    /**
     * Send th message to al register player actor in the match
     */
    private void sendUsersResults() {
        List<PlayerScore> scores = round.applyVotesAndBuildRoundResults();
        final int defId = round.getCorrectDefinition().getId();
        this.sendUsers(new Result(defId, scores, PHASE_DURATION.toMillis()));
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
        return Props.create(Match.class);
    }
}