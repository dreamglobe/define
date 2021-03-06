package com.kamomileware.define.model.round;

import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.MessageInfoFactory;
import com.kamomileware.define.model.PlayerInfo;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.term.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kamomileware.define.model.round.VoteDefinition.createVoteDefinition;

/**
 * Created by pepe on 10/07/14.
 */
public class PlayerData<REF> implements MessageInfoFactory {

    private final REF ref;
    private final String name;
    private final String pid;
    private final Score score;
    private boolean readyInResult ;
    private TermDefinition definition;
    private VoteDefinition vote;
    private DefinitionResolver resolver;

    private PlayerData(REF ref, String name, String pid, DefinitionResolver round) {
        this.ref = ref;
        this.name = name;
        this.pid = pid;
        this.resolver = round;
        this.score = new Score();
    }

    void vote(Integer definitionId) {
        final Optional<TermDefinition> termDefinitionOptional = resolver.findDefinitionById(definitionId);
        this.vote = termDefinitionOptional.isPresent() ?
            createVoteDefinition(this, termDefinitionOptional.get()):
            null;
    }

    public boolean hasResponse() {
        return definition!=null;
    }

    public boolean hasVote() {
        return vote != null;
    }

    public boolean isReadyInResult() {
        return readyInResult;
    }

    public void setReadyInResult(boolean readyInResult) {
        this.readyInResult = readyInResult;
    }

    protected void prepareNextRound(DefinitionResolver newRound){
        this.definition = null;
        this.vote = null;
        this.score.consolidateRound();
        this.resolver = newRound;
        this.readyInResult = false;
    }

    public REF getRef() {
        return ref;
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public TermDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(Term term, String definition) {
        this.definition = new TermDefinition<REF>(this, definition, term);
    }

    void setDefinition(TermDefinition definition){
        this.definition = definition;
    }

    public Optional<VoteDefinition> getVote() {
        return Optional.ofNullable(vote);
    }

    public Score getScore() {
        return score;
    }

    public boolean isWinner(){
        return score.getTotalScore() > resolver.getMatchConf().getGoalPointsOpt().orElse(Integer.MAX_VALUE);
    }

    public static <PREF> PlayerData createPlayerData(PREF playerRef, String name, String pid, DefinitionResolver resolver) {
        return new PlayerData<>(playerRef, name, pid, resolver);
    }

    @Override
    public ItemDefinition createItemDefinition() {
        return definition != null ? new ItemDefinition(definition.getDefId(), definition.getText()) : new ItemDefinition(0,"");
    }

    @Override
    public PlayerScore createPlayerScore(){
        Integer defId = definition!=null? definition.getDefId():null;
        return PlayerScore.create(this.pid, defId, this.score.getVoteScore(), this.score.getTurnScore(),
                this.score.getTotalScore(), this.score.getVoters(), this.score.isCorrectVote());
    }

    @Override
    public PlayerInfo createPlayerInfo() {
        return new PlayerInfo(this.pid, this.name, this.isReadyInResult(), this.score.getTotalScore(), this.score.getLastTurnScore());
    }

    public class Score{
        private int turnScore = 0;
        private int totalScore = 0;
        private int lastTurnScore = 0;
        private boolean correctVote;
        List<String> voters = new ArrayList<>();

        Score(){
        }

        public int voteObtained(String voterPid){
            turnScore += resolver.getMatchConf().getVoteValue();
            voters.add(voterPid);
            return turnScore;
        }

        public int correctVote(){
            correctVote = true;
            turnScore += resolver.getMatchConf().getCorrectVoteValue();
            return turnScore;
        }

        public void consolidateRound(){
            correctVote = false;
            voters = new ArrayList<>();
            lastTurnScore = turnScore;
            turnScore = 0;
        }

        public void applyTurnScore(){
            if(turnScore>0){
                totalScore += turnScore;
            }
        }

        public PlayerData<REF> getPlayerData(){
            return PlayerData.this;
        }

        public List<String> getVoters() {
            return voters;
        }

        public int getTurnScore() {
            return turnScore;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public int getVoteScore() {
            return correctVote? turnScore - resolver.getMatchConf().getCorrectVoteValue() : turnScore;
        }

        public int getLastTurnScore() {
            return lastTurnScore;
        }

        public boolean isCorrectVote() {
            return correctVote;
        }
    }
}