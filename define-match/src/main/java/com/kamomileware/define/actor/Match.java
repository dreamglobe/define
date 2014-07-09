package com.kamomileware.define.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.PlayerScore;
import scala.concurrent.duration.FiniteDuration;

import java.util.*;

import static com.kamomileware.define.model.MessageTypes.*;
import static com.kamomileware.define.actor.MatchFSM.State.*;
import static com.kamomileware.define.model.ItemDefinition.convertToList;

/**
 * Match Actor for coordinating and operate the match state. Match state refers to
 * players actors, definitions, votes, and points in the current turn.
 * Base state is modelled by {@link MatchFSM}.
 *
 * Created by pepe on 12/06/14.
 */
public class Match extends MatchFSM {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final String DEFINITION = "Definicion correcta";

    private final StoppedBehaviour stopBehavior = new StoppedBehaviour();
    private final WaitResponseBehaviour waitResponseBehavior = new WaitResponseBehaviour();
    private final WaitVotesBehaviour waitVotesBehavior = new WaitVotesBehaviour();
    private final ShowResultBehaviour showResultBehavior = new ShowResultBehaviour();

    protected Map<ActorRef, String> actor_pid = new HashMap<>();
    protected Map<String, String> pid_name = new HashMap<>();
    protected Map<String, String> pid_definition = null;
    protected Map<String, Integer> pid_voteId = null;
    protected BiMap<Integer, String> defId_pid = null;
    protected Map<Integer, String> defId_definition;
    protected Set<String> pidReady = null;
    protected List<PlayerScore> lastTurnScores = null;
    protected List<Map<String, String>> oldResponses = new ArrayList<>();
    private int correctDefId;
    private int correctNumVotes;

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
    protected void transition(State old, State next) {
        if (next == STOPPED) {
            this.sendUsers(STOP);
            cancelLatch();
            this.switchBehaviour(this.stopBehavior);
        } else if (next == PHASE_RESPONSE) {
            this.prepareNextRound();
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
     * Behaviour for {@link com.kamomileware.define.actor.MatchFSM.State#STOPPED} state
     */
    private class StoppedBehaviour implements Procedure<Object> {
        public void apply(Object message) {
            if (message instanceof Start) {
                setState(PHASE_RESPONSE);
            } else if (message instanceof RegisterUser) {
                if (actor_pid.isEmpty()) {
                    self().tell(START, self());
                }
                handleRegisterUser((RegisterUser) message, sender());
            } else {
                aunhandled(message);
            }
        }
    }

    /**
     * Common behaviour for non {@link com.kamomileware.define.actor.MatchFSM.State#STOPPED} state
     */
    private abstract class CommonStartedBehaviour implements Procedure<Object> {
        @Override
        public void apply(Object message) {
            if (message instanceof Stop) {
                sendUsers(message);
                setState(STOPPED);
            } else if (message instanceof RemoveUser) {
                handleRemoveUser(message, sender());
                if (actor_pid.isEmpty()) {
                    self().tell(STOP, self());
                }
            } else {
                aunhandled(message);
            }
        }
    }

    /**
     * Common behaviour for {@link com.kamomileware.define.actor.MatchFSM.State#PHASE_RESPONSE} state
     */
    private class WaitResponseBehaviour extends CommonStartedBehaviour {
        boolean extended = false;
        @Override
        public void apply(Object message) {
            if (message instanceof UserDefinition) {
                handleUserResponse(((UserDefinition) message).getResponse(), sender());
                if (isResponseComplete()) {
                    cancelLatch();
                    extended = false;
                    setState(PHASE_VOTE);
                }
            } else if (message instanceof Latch) {
                if (!extended && pid_definition.isEmpty()) {
                    extended = true;
                    startLatchExtend();
                } else {
                    extended = false;
                    setState(!pid_definition.isEmpty()?PHASE_VOTE:PHASE_RESULT);
                }
            } else if (message instanceof RegisterUser) {
                FiniteDuration timeLeft = extended? PHASE_DURATION.plus(EXTEND_DURATION):PHASE_DURATION;
                sender().tell(new StartDefinition(getPhaseMillisLeft(timeLeft)), self());
                handleRegisterUser((RegisterUser) message, sender());
            } else {
                super.apply(message);
            }
        }
    }

    /**
     * Common behaviour for {@link com.kamomileware.define.actor.MatchFSM.State#PHASE_VOTE} state
     */
    private class WaitVotesBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message){
            if(message instanceof UserVote){
                final Integer voteId = ((UserVote) message).getVoteId();
                final boolean isVote = handleVote(voteId, sender());
                if(isVote && isVoteComplete()){
                    cancelLatch();
                    setState(PHASE_RESULT);
                }
            }else if(message instanceof Latch){
                setState(PHASE_RESULT);
            } else if(message instanceof RegisterUser){
                final List<ItemDefinition> itemDefinitions = convertToList(defId_definition);
                Collections.shuffle(itemDefinitions);
                sender().tell(new StartVote(getPhaseMillisLeft(PHASE_DURATION), itemDefinitions, 0, ""), self());
                handleRegisterUser((RegisterUser) message, sender());
            } else {
                super.apply(message);
            }
        }
    }

    /**
     * Common behaviour for {@link com.kamomileware.define.actor.MatchFSM.State#PHASE_RESULT} state
     */
    private class ShowResultBehaviour extends CommonStartedBehaviour {
        @Override
        public void apply(Object message) {
            if (message instanceof UserReady) {
                final Boolean ready = ((UserReady) message).isReady();
                handleUserReady(ready, sender());
                if (ready && isReadyComplete()) {
                    cancelLatch();
                    setState(PHASE_RESPONSE);
                }
            } else if (message instanceof Latch) {
                setState(PHASE_RESPONSE);
            } else if (message instanceof RegisterUser) {
                List<ItemDefinition> itemDefinitions = convertToList(defId_definition);
                Collections.shuffle(itemDefinitions);
                ItemDefinition emptyDefinition = new ItemDefinition(0,"");
                sender().tell(new ResultForRegisterUser(itemDefinitions, emptyDefinition, correctDefId, correctNumVotes, lastTurnScores, getPhaseMillisLeft(PHASE_DURATION)), self());
                handleRegisterUser((RegisterUser) message, sender());
            } else {
                super.apply(message);
            }
        }
    }

    /* Private methods for handling message */

    private void handleRemoveUser(Object message, ActorRef sender) {
        this.actor_pid.remove(sender);
        this.pid_name.remove(((RemoveUser) message).getUserId());
        this.sendUsers(message);
    }

    private void handleRegisterUser(RegisterUser message, ActorRef sender) {
        this.sendUsers(message);
        this.actor_pid.put(sender, message.getUserId());
        this.pid_name.put(message.getUserId(), message.getUserName());
        sender.tell(new UsersList(this.pid_name), this.self());
    }

    private void handleUserResponse(String response, ActorRef sender) {
        this.pid_definition.put(this.actor_pid.get(sender), response);
        this.sendUsers(new UserDefinition(this.actor_pid.get(sender), null));
    }

    private boolean handleVote(Integer voteId, ActorRef sender) {
        final boolean voteEmitted = voteId != null;
        if(voteEmitted) {
            this.pid_voteId.put(this.actor_pid.get(sender), voteId);
        }else{
            this.pid_voteId.remove(this.actor_pid.get(sender));
        }
        this.sendUsers(new UserVote(this.actor_pid.get(sender), voteId));
        return voteEmitted;
    }

    private void handleUserReady(Boolean isReady, ActorRef sender) {
        this.sendUsers(new UserReady(this.actor_pid.get(sender), isReady));
        if (isReady) {
            this.pidReady.add(this.actor_pid.get(sender));
        } else {
            this.pidReady.remove(this.actor_pid.get(sender));
        }
    }

    private void prepareNextRound() {
        // send the information to statistics
        this.sendUsers(START_RESPONSE);
        this.oldResponses.add(this.pid_definition);
        this.pid_definition = new HashMap<>(this.actor_pid.size());
        this.pid_voteId = new HashMap<>(this.actor_pid.size());
        this.defId_pid = HashBiMap.create(this.actor_pid.size());
        this.defId_definition = new HashMap<>(this.actor_pid.size());
        this.pidReady = new HashSet<>(this.actor_pid.size());
    }

    private void sendUsersDefinitions() {
        this.calculateVotesIds();
        this.buildUsersDefinitions();
        this.actor_pid.forEach((a, id) -> a.tell(createStartVote(id, new HashMap<>(this.defId_definition)), this.self()));
    }

    private void calculateVotesIds() {
        this.pid_definition.forEach((k, v) -> this.defId_pid.put((v + k).hashCode(), k));
    }

    private void buildUsersDefinitions() {
        BiMap<String, Integer> pid_defId = this.defId_pid.inverse();
        this.defId_definition = new HashMap<>(this.pid_definition.size());
        this.pid_definition.forEach((k, v) -> this.defId_definition.put(pid_defId.get(k), v));
        this.defId_definition.put(DEFINITION.hashCode(), DEFINITION);
    }

    private StartVote createStartVote(String senderId, Map<Integer, String> definitions) {
        final BiMap<String, Integer> pid_defId = this.defId_pid.inverse();
        Integer senderDefId = pid_defId.get(senderId);
        String senderDefinition = definitions.remove(pid_defId.get(senderId));
        final List<ItemDefinition> itemDefinitions = convertToList(definitions);
        Collections.shuffle(itemDefinitions);
        return new StartVote(PHASE_DURATION.toMillis(), itemDefinitions, senderDefId, senderDefinition);
    }

    /**
     * Send th message to al register player actor in the match
     * @param message
     */
    private void sendUsersResults() {
        this.lastTurnScores = buildTurnScores();
        this.correctDefId = DEFINITION.hashCode();
        this.correctNumVotes = (int) pid_voteId.values().stream().filter(p -> Integer.valueOf(correctDefId).equals(p)).count();
        this.sendUsers(new Result(this.correctDefId, this.correctNumVotes, this.lastTurnScores, PHASE_DURATION.toMillis()));
    }

    private List<PlayerScore> buildTurnScores() {
        List<PlayerScore> scores = new ArrayList<>(this.pid_name.size());
        this.pid_name.keySet().forEach(pid -> scores.add(buildPlayerScore(pid)));
        return scores;
    }

    private PlayerScore buildPlayerScore(String pid) {
        final BiMap<String, Integer> pid_defId = this.defId_pid.inverse();
        Integer defId = pid_defId.get(pid);
        List<String> pidVoters = new ArrayList<>(this.pid_voteId.size());
        final int[] votePoints = {0};
        if(defId!=null) {
            this.pid_voteId.forEach((playerId, voteId) -> {
                if (defId.equals(voteId)) {
                    votePoints[0]++; // TODO: config vote points
                    pidVoters.add(playerId);
                }
            });
        }
        boolean correctDefinition = false;
        if(this.pid_voteId.containsKey(pid)) {
            correctDefinition = DEFINITION.hashCode() == this.pid_voteId.get(pid);
        }
        int turnPoints = correctDefinition? votePoints[0] +2 : votePoints[0]; // TODO: config correct vote points
        return new PlayerScore(pid, defId, votePoints[0], turnPoints, 0, pidVoters, correctDefinition);
    }

    /**
     * Send the message to al register player actor in the match
     * @param message to send to every user player in the match
     */
    private void sendUsers(Object message) {
        this.actor_pid.keySet().forEach(a -> a.tell(message, this.self()));
    }

    private boolean isResponseComplete() {
        return this.actor_pid.size() == this.pid_definition.size();
    }

    private boolean isVoteComplete() {
        return this.actor_pid.size() == this.pid_voteId.size();
    }

    private boolean isReadyComplete() {
        return this.actor_pid.size() == this.pidReady.size();
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