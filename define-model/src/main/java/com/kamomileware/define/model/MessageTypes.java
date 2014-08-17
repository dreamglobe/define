package com.kamomileware.define.model;

import com.fasterxml.jackson.annotation.*;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.term.Term;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pepe on 29/06/14.
 */
public class MessageTypes {

    /**
     *
     */
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
    public abstract static class DeffineMessage implements Serializable {
        private static final long serialVersionUID = 5144661293016326895L;
        private final String TYPE = this.getClass().getSimpleName();

        @JsonTypeId
        public String getType() {
            return TYPE;
        }
    }

    public abstract static class PhaseDeffineMessage extends DeffineMessage {
        private static final long serialVersionUID = -5764893248138092071L;
        public long time = 0L;

        protected PhaseDeffineMessage(long time){
            this.time = time;
        }
    }

    /**
     * Client Messages *
     */

    @JsonSubTypes({
            @JsonSubTypes.Type(value = ClientResponse.class, name = "ClientResponse"),
            @JsonSubTypes.Type(value = ClientReady.class, name = "ClientReady"),
            @JsonSubTypes.Type(value = ClientVote.class, name = "ClientVote"),
            @JsonSubTypes.Type(value = ClientStartMatch.class, name = "ClientStartMatch")})
    public abstract static class ClientMessage extends DeffineMessage {
        private static final long serialVersionUID = -4040598146039087356L;
    }


    public static class ClientStartMatch extends ClientMessage {
        MatchConfiguration config;

        @JsonCreator
        public ClientStartMatch(@JsonProperty("config") MatchConfiguration config) {
            this.config = config;
        }

        public MatchConfiguration getConfig() {
            return config;
        }
    }

    public static class ClientReady extends ClientMessage {
        private static final long serialVersionUID = 7686791648984584553L;
        private final Boolean ready;

        @JsonCreator
        public ClientReady(@JsonProperty("ready") boolean ready) {
            this.ready = ready;
        }

        public Boolean isReady() {
            return ready;
        }
    }

    public static class ClientResponse extends ClientMessage {
        private static final long serialVersionUID = 7686791648984584553L;
        private final String response;

        @JsonCreator
        public ClientResponse(@JsonProperty("voteId") String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }

    public static class ClientVote extends ClientMessage {
        private static final long serialVersionUID = -9097073963665625692L;
        private final Integer voteId;

        @JsonCreator
        public ClientVote(@JsonProperty("voteId") Integer voteId) {
            this.voteId = voteId;
        }

        public Integer getVoteId() {
            return voteId;
        }
    }

    /** Inter Actor Messages **/

    public static class Start extends PhaseDeffineMessage implements Serializable {
        private static final long serialVersionUID = -2085677311635169798L;
        private Start(){super(0L);}
    }

    public static final Start START = new Start();

    public static class Stop extends PhaseDeffineMessage {
        private static final long serialVersionUID = -417020725588326199L;
        private Stop(){super(0L);}
    }

    public static final Stop STOP = new Stop();

    public static class Latch extends PhaseDeffineMessage {
        private static final long serialVersionUID = -7555609005105323268L;
        protected final int sequence;

        public Latch(int seq, long millis) {
            super(millis);
            this.sequence = seq;
        }

        public int getSequence() {
            return sequence;
        }
    }

    public static class ExtendTime extends PhaseDeffineMessage {

        public ExtendTime(long time) {
            super(time);
        }
    }

    public static class RegisterUser extends DeffineMessage {
        private static final long serialVersionUID = 1958789168037278074L;
        private final String pid;
        private final String name;
        private final int totalScore;
        private final int turnScore;

        public RegisterUser(String pid, String name) {
            this.pid = pid;
            this.name = name;
            this.totalScore = 0;
            this.turnScore = 0;
        }

        public RegisterUser(String pid, String name, int totalScore, int turnScore) {
            this.pid = pid;
            this.name = name;
            this.totalScore = totalScore;
            this.turnScore = turnScore;
        }

        public String getPid() {
            return pid;
        }

        public String getName() {
            return name;
        }

        public int getTurnScore() {
            return turnScore;
        }

        public int getTotalScore() {
            return totalScore;
        }
    }

    public static class RemoveUser extends DeffineMessage {
        private static final long serialVersionUID = -329444905850140400L;
        private final String pid;

        public RemoveUser(String pid) {
            this.pid = pid;
        }

        public String getPid() {
            return pid;
        }
    }

    public static class PlayerList extends DeffineMessage {
        private static final long serialVersionUID = -5759721640234426466L;
        final List<PlayerInfo> players;

        public PlayerList(List<PlayerInfo> playersList) {
            this.players = playersList;
        }

        public List<PlayerInfo> getPlayers() {
            return players;
        }
    }

    public static class Starting extends DeffineMessage {

        MatchConfiguration config;

        public Starting() {
        }

        public Starting(MatchConfiguration config) {
            this.config = config;
        }

        public MatchConfiguration getConfig() {
            return config;
        }
    }

    public static class ShowEndScores extends DeffineMessage {
        private final ItemDefinition correctDef;
        private final long numCorrectVotes;
        private final List<PlayerScore> scores;

        public ShowEndScores(List<PlayerScore> scores, ItemDefinition correctDef) {
            this.scores = scores;
            this.correctDef = correctDef;
            this.numCorrectVotes = scores.stream().filter(ps -> ps.isCorrectDefinition()).count();
        }

        public ItemDefinition getCorrectDef() {
            return correctDef;
        }

        public long getNumCorrectVotes() {
            return numCorrectVotes;
        }

        public List<PlayerScore> getScores() {
            return scores;
        }
    }

    public static class StartDefinition extends PhaseDeffineMessage {
        private static final long serialVersionUID = 8232869194624432443L;
        private final Term term;

        public StartDefinition(long millis, Term term){
            super(millis);
            this.term = new Term(term.getName(),null,term.getCategory());
        }

        public Term getTerm() {
            return term;
        }
    }

    public static class StartVote extends PhaseDeffineMessage {
        private static final long serialVersionUID = 1097452098031248677L;
        private final List<ItemDefinition> definitions;
        private final ItemDefinition playerDefinition;

        public StartVote(long millis, List<ItemDefinition> definitions, ItemDefinition playerDefinition ){
            super(millis);
            this.definitions = definitions;
            this.playerDefinition = playerDefinition;
        }

        public List<ItemDefinition> getDefinitions(){
            return this.definitions;
        }

        public ItemDefinition getPlayerDefinition(){
            return this.playerDefinition;
        }
    }

    public static class RegisterUserInVote extends StartVote {
        private static final long serialVersionUID = 1097452098031248677L;
        private final Term term;

        public RegisterUserInVote(long millis, Term term, List<ItemDefinition> definitions, ItemDefinition playerDefinition ){
            super(millis, definitions, playerDefinition);
            this.term = term;
        }

        public Term getTerm() {
            return term;
        }
    }

    public static class StartShowScores extends PhaseDeffineMessage {
        private static final long serialVersionUID = -7914974657516737999L;
        private final ItemDefinition correctDef;
        private final long numCorrectVotes;
        private final List<PlayerScore> scores;

        public StartShowScores(long millis, List<PlayerScore> scores, ItemDefinition correctDef) {
            super(millis);
            this.scores = scores;
            this.correctDef = correctDef;
            this.numCorrectVotes = scores.stream().filter(ps -> ps.isCorrectDefinition()).count();
        }

        public ItemDefinition getCorrectDef() {
            return correctDef;
        }

        public long getNumCorrectVotes() {
            return numCorrectVotes;
        }

        public List<PlayerScore> getScores() {
            return scores;
        }
    }

    public static class RegisterUserInShowScores extends StartShowScores {
        private final List<ItemDefinition> definitions;
        private final Term term;
        private final ItemDefinition playerDefinition;

        public RegisterUserInShowScores(long millis, Term term, List<ItemDefinition> definitions,
                            ItemDefinition playerDefinition, List<PlayerScore> scores, ItemDefinition correctDefId) {
            super(millis, scores, correctDefId);
            this.term = term;
            this.definitions = definitions;
            this.playerDefinition = playerDefinition;
        }

        public Term getTerm() {
            return term;
        }

        public List<ItemDefinition> getDefinitions() {
            return definitions;
        }

        public ItemDefinition getPlayerDefinition() {
            return playerDefinition;
        }
    }

    public static class UserDefinition extends DeffineMessage {
        private static final long serialVersionUID = -1010573446123056025L;
        private final String pid;
        private final String response;

        public UserDefinition(String pid) {
            this.pid = pid;
            this.response=null;
        }

        public UserDefinition(String pid, String response) {
            this.pid = pid;
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public String getPid() {
            return pid;
        }
    }

    public static class UserVote extends DeffineMessage {
        private static final long serialVersionUID = -6486443526196673922L;
        private final String pid;
        private final Integer voteId;

        public UserVote(String pid, Integer voteId) {
            this.pid = pid;
            this.voteId = voteId;
        }

        public Integer getVoteId() {
            return voteId;
        }

        public String getPid() {
            return pid;
        }
    }

    public static class UserReady extends DeffineMessage {
        private static final long serialVersionUID = 7686791648984584553L;
        private final String pid;
        private final Boolean ready;

        public UserReady(String pid, boolean ready) {
            this.pid = pid;
            this.ready = Boolean.valueOf(ready);
        }

        public Boolean isReady() {
            return ready;
        }

        public String getPid() {
            return pid;
        }
    }
}
