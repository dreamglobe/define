package com.kamomileware.define.model.round;

import akka.actor.ActorRef;
import com.kamomileware.define.model.term.Term;

/**
 * Created by pepe on 10/07/14.
 */
public class PlayerData {

    private final String name;
    private final ActorRef ref;
    private final String pid;
    private final Score score;

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

    public void setDefinition(Term term, String definition) {
        this.definition = new TermDefinition(this, definition, term);
    }

    void setDefinition(TermDefinition definition){
        this.definition = definition;
    }

    public VoteDefinition getVote() {
        return vote;
    }

    void vote(int definitionId) {
        this.vote = VoteDefinition.createVoteDefinition(resolver.resolveDefinitionId(definitionId));
    }

    public Score getScore() {
        return score;
    }

    public static PlayerData createPlayerData(ActorRef playerRef, String name, String pid, DefinitionResolver resolver) {
        return new PlayerData(playerRef, name, pid, resolver);
    }


    class Score{
        final MatchConfiguration matchConf = resolver.getMatchConf();
        private int turnScore = 0;
        private int totalScore = 0;

        Score(){}

        public int getTurnScore() {
            return turnScore;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public int voteObtained(){
            turnScore += matchConf.getVoteValue();
            return turnScore;
        }

        public int correctVote(){
            turnScore += matchConf.getCorrectVoteValue();
            return turnScore;
        }

        public boolean endRound(){
            totalScore += turnScore;
            return totalScore >= matchConf.getGoalPoints();
        }
    }
}