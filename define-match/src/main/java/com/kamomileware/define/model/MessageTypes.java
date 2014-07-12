package com.kamomileware.define.model;

import com.fasterxml.jackson.annotation.*;
import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.round.TermDefinition;
import com.kamomileware.define.model.term.Term;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @JsonSubTypes.Type(value = ClientVote.class, name = "ClientVote")})
    public abstract static class ClientMessage extends DeffineMessage {
        private static final long serialVersionUID = -4040598146039087356L;
    }

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     * Created by pepe on 12/06/14.
     */
    public static class Start extends PhaseDeffineMessage implements Serializable {
        private static final long serialVersionUID = -2085677311635169798L;
        private Start(){super(0L);}
    }

    public static final Start START = new Start();

    /**
     * Created by pepe on 12/06/14.
     */
    public static class Stop extends PhaseDeffineMessage {
        private static final long serialVersionUID = -417020725588326199L;
        private Stop(){super(0L);}
    }

    public static final Stop STOP = new Stop();

    /**
     * Created by pepe on 12/06/14.
     */
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

    /**
     * Created by pepe on 12/06/14.
     */
    public static class RegisterUser extends DeffineMessage {
        private static final long serialVersionUID = 1958789168037278074L;
        private final String userId;
        private final String userName;

        public RegisterUser(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }

    /**
     * Created by pepe on 12/06/14.
     */
    public static class RemoveUser extends DeffineMessage {
        private static final long serialVersionUID = -329444905850140400L;
        private final String userId;

        public RemoveUser(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

    /**
     * Created by pepe on 12/06/14.
     */
    public static class Result extends PhaseDeffineMessage {
        private static final long serialVersionUID = -7914974657516737999L;
        private final int correctDefId;
        private final long correctNumVotes;
        private final List<PlayerScore> scores;
        public Result(int correctDefId, List<PlayerScore> scores, long millis) {
            super(millis);
            this.scores = scores;
            this.correctDefId = correctDefId;
            this.correctNumVotes = scores.stream().filter(ps -> ps.isCorrectDefinition()).count();
        }

        public int getCorrectDefId() {
            return correctDefId;
        }

        public long getCorrectNumVotes() {
            return correctNumVotes;
        }

        public List<PlayerScore> getScores() {
            return scores;
        }
    }

    public static class ResultForRegisterUser extends Result {
        private final List<ItemDefinition> definitions;

        public ResultForRegisterUser(int correctDefId, List<PlayerScore> scores,
                                     List<ItemDefinition> definitions, long millis) {
            super(correctDefId, scores, millis);
            this.definitions = definitions;
        }

        public List<ItemDefinition> getDefinitions() {
            return definitions;
        }

    }

    /**
     * Created by pepe on 12/06/14.
     */
    public static class UsersList extends DeffineMessage {
        private static final long serialVersionUID = -5759721640234426466L;
        final List<PlayerInfo> users;

        public UsersList(List<PlayerInfo> playersList) {
            this.users = playersList;
        }

        public List<PlayerInfo> getUsers() {
            return users;
        }
    }

    public static class StartDefinition extends PhaseDeffineMessage {
        private static final long serialVersionUID = 8232869194624432443L;
        private final Term term;

        public StartDefinition(long millis, Term term){
            super(millis);
            this.term = term;
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

    /**
     * Created by pepe on 12/06/14.
     */
    public static class UserDefinition extends DeffineMessage {
        private static final long serialVersionUID = -1010573446123056025L;
        private final String userId;
        private final String response;

        /**
         * Constructor for informing the rest of the players
         * @param userId
         */
        public UserDefinition(String userId) {
            this.userId = userId;
            this.response=null;
        }

        public UserDefinition(String userId, String response) {
            this.userId = userId;
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public String getUserId() {
            return userId;
        }
    }

    public static class UserVote extends DeffineMessage {
        private static final long serialVersionUID = -6486443526196673922L;
        private final String userId;
        private final Integer voteId;

        public UserVote(String userId, Integer voteId) {
            this.userId = userId;
            this.voteId = voteId;
        }

        public Integer getVoteId() {
            return voteId;
        }

        public String getUserId() {
            return userId;
        }
    }

    /**
     * Created by pepe on 29/06/14.
     */
    public static class UserReady extends DeffineMessage {
        private static final long serialVersionUID = 7686791648984584553L;
        private final String userId;
        private final Boolean ready;

        public UserReady(String userId, boolean ready) {
            this.userId = userId;
            this.ready = Boolean.valueOf(ready);
        }

        public Boolean isReady() {
            return ready;
        }

        public String getUserId() {
            return userId;
        }
    }
}
