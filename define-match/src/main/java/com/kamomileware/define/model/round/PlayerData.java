package com.kamomileware.define.model.round;

import akka.actor.ActorRef;
import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.term.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pepe on 10/07/14.
 */
public class PlayerData {

    private final String name;
    private final ActorRef ref;
    private final String pid;
    private final Score score;

    private boolean readyInResult ;

    private TermDefinition definition;
    private VoteDefinition vote;

    private final DefinitionResolver resolver;

    private PlayerData(ActorRef ref, String name, String pid, DefinitionResolver round) {
        this.ref = ref;
        this.name = name;
        this.pid = pid;
        this.resolver = round;
        this.score = new Score();
    }

    public ActorRef getRef() {
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

    public ItemDefinition getItemDefinition() {
        return new ItemDefinition(definition.getId(), definition.getDefinition());
    }

    public void setDefinition(Term term, String definition) {
        this.definition = new TermDefinition(this, definition, term);
    }

    void setDefinition(TermDefinition definition){
        this.definition = definition;
    }

    public Optional<VoteDefinition> getVote() {
        return Optional.ofNullable(vote);
    }

    void vote(int definitionId) {
        final Optional<TermDefinition> termDefinitionOptional = resolver.findDefinitionById(definitionId);
        if(termDefinitionOptional.isPresent()) {
            this.vote = VoteDefinition.createVoteDefinition(this, termDefinitionOptional.get());
        }
    }

    public Score getScore() {
        return score;
    }

    public static PlayerData createPlayerData(ActorRef playerRef, String name, String pid, DefinitionResolver resolver) {
        return new PlayerData(playerRef, name, pid, resolver);
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

    public void updateScore(){
        final TermDefinition selectedDefinition = vote.getSelectedDefinition();
        if(selectedDefinition.isCorrect()){
            this.score.correctVote();
        }else{

        }
    }

    public class Score{
        final MatchConfiguration matchConf = resolver.getMatchConf();
        private int turnScore = 0;
        private int totalScore = 0;
        private boolean correctVote;
        List<String> voters = new ArrayList<>();

        Score(){}

        public int getTurnScore() {
            return turnScore;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public int getVoteScore() {
            return correctVote? turnScore - matchConf.getCorrectVoteValue() : turnScore;
        }

        public boolean isCorrectVote() {
            return correctVote;
        }

        public int voteObtained(String voterPid){
            turnScore += matchConf.getVoteValue();
            voters.add(voterPid);
            return turnScore;
        }

        public int correctVote(){
            turnScore += matchConf.getCorrectVoteValue();
            correctVote = true;
            return turnScore;
        }

        public boolean endRound(){
            totalScore += turnScore;
            return totalScore >= matchConf.getGoalPoints();
        }

        public PlayerData getPlayerData(){
            return PlayerData.this;
        }

        public List<String> getVoters() {
            return voters;
        }
    }
}