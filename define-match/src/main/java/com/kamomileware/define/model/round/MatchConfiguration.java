package com.kamomileware.define.model.round;

import com.kamomileware.define.actor.RoundPhase;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by pepe on 10/07/14.
 */
public class MatchConfiguration {
    private int voteValue = 1;
    private int correctVoteValue = 2;
    private int minimunPlayers = 2;

    private Optional<Integer> goalPoints = Optional.ofNullable(45);
    private Optional<Integer> maximunRounds = Optional.ofNullable(null);
    private Optional<Long> timeLimit = Optional.ofNullable(null);

    private PhaseConfiguration responsePhaseConf = new PhaseConfiguration(RoundPhase.PHASE_RESPONSE, 60, 30, PhaseExtension.NO_PLAYER, true);
    private PhaseConfiguration votePhaseConf = new PhaseConfiguration(RoundPhase.PHASE_VOTE, 60, 0, PhaseExtension.NEVER, true);
    private PhaseConfiguration resultPhaseConf = new PhaseConfiguration(RoundPhase.PHASE_RESULT, 40, 0, PhaseExtension.NEVER, true);


    private MatchConfiguration() {
    }

    public static MatchConfiguration createMatchConfiguration() {
        return new MatchConfiguration();
    }

    public int getVoteValue() {
        return voteValue;
    }

    public void setVoteValue(int voteValue) {
        this.voteValue = voteValue;
    }

    public int getCorrectVoteValue() {
        return correctVoteValue;
    }

    public void setCorrectVoteValue(int correctVoteValue) {
        this.correctVoteValue = correctVoteValue;
    }

    public Optional<Integer> getGoalPoints() {
        return goalPoints;
    }

    public void setGoalPoints(Integer goalPoints) {
        this.goalPoints = Optional.ofNullable(goalPoints);
    }

    public int getMinimunPlayers() {
        return minimunPlayers;
    }

    public Optional<Integer> getMaximunRounds() {
        return maximunRounds;
    }

    public void setMaximunRounds(Integer maximunRounds) {
        this.maximunRounds = Optional.ofNullable(maximunRounds);
    }

    public Optional<Long> getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = Optional.ofNullable(timeLimit);
    }

    public PhaseConfiguration getResponsePhaseConf() {
        return responsePhaseConf;
    }

    public void setResponsePhaseConf(PhaseConfiguration responsePhaseConf) {
        this.responsePhaseConf = responsePhaseConf;
    }

    public PhaseConfiguration getVotePhaseConf() {
        return votePhaseConf;
    }

    public void setVotePhaseConf(PhaseConfiguration votePhaseConf) {
        this.votePhaseConf = votePhaseConf;
    }

    public PhaseConfiguration getResultPhaseConf() {
        return resultPhaseConf;
    }

    public void setResultPhaseConf(PhaseConfiguration resultPhaseConf) {
        this.resultPhaseConf = resultPhaseConf;
    }

    public PhaseConfiguration getPhaseConf(RoundPhase phase) {
        PhaseConfiguration result = null;
        switch (phase){
            case PHASE_RESPONSE:
                result = responsePhaseConf;
                break;
            case PHASE_VOTE:
                result = votePhaseConf;
                break;
            case PHASE_RESULT:
                result = resultPhaseConf;
                break;
        }
        return result;
    }

    public int getMinimumPlayers() {
        return minimunPlayers;
    }

    public void setMinimunPlayers(int minimunPlayers) {
        this.minimunPlayers = minimunPlayers;
    }

    public Set<TurnResult> resolveRound(Round round) {
        Set<TurnResult> results = new HashSet<>(TurnResult.values().length);
        List<PlayerData> players = round.getPlayers();
        // end match conditions
        if(players.size() < this.minimunPlayers){
            results.add(TurnResult.NO_PLAYERS_LEFT);
        }
        if (this.goalPoints.isPresent() && round.hasWinners()){
            results.add(TurnResult.GOAL_REACHED);
        }
        if(this.maximunRounds.isPresent() && this.maximunRounds.get() < round.getRoundNumber()){
            results.add(TurnResult.TURN_LIMIT_REACHED);
        }
        if(this.timeLimit.isPresent() && this.timeLimit.get() < round.getMatchTime()){
            results.add(TurnResult.TIME_LIMIT_REACHED);
        }
        return results;
    }

    public static enum PhaseExtension {
        NEVER, NO_PLAYER, SOME_PLAYER, NONE_OR_SOME_PLAYERS, ALWAYS;
    }

    public static class PhaseConfiguration {

        private RoundPhase phase;
        private int duration = 60;
        private int extendedDuration;
        private PhaseExtension extendedWhen = PhaseExtension.NO_PLAYER;
        private boolean fastEnd = true;
        private boolean extended = false;

        public PhaseConfiguration(RoundPhase phase, int duration, int extendedDuration, PhaseExtension extendedWhen, boolean fastEnd) {
            this.phase = phase;
            this.duration = duration;
            this.extendedDuration = extendedDuration;
            this.extendedWhen = extendedWhen;
            this.fastEnd = fastEnd;
        }

        public boolean canExtend(Round round){
            boolean result = false;
            if(!extended) {
                switch (extendedWhen) {
                    case NO_PLAYER:
                        result = !round.hasAnyoneResponse();
                        break;
                    case SOME_PLAYER:
                        result = !round.hasEveryoneResponse() && round.hasAnyoneResponse();
                        break;
                    case NONE_OR_SOME_PLAYERS:
                        result = !round.hasEveryoneResponse();
                        break;
                    case NEVER:
                        result = false;
                        break;
                    case ALWAYS:
                        result = true;
                        break;
                }
            }
            return result;
        }

        public RoundPhase getPhase() {
            return phase;
        }

        public void setPhase(RoundPhase phase) {
            this.phase = phase;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getExtendedDuration() {
            return extendedDuration;
        }

        public void setExtendedDuration(int extendedDuration) {
            this.extendedDuration = extendedDuration;
        }

        public int getTotalDuration(){
            return extended? duration + extendedDuration : duration;
        }

        public PhaseExtension getExtendedWhen() {
            return extendedWhen;
        }

        public void setExtendedWhen(PhaseExtension extendedWhen) {
            this.extendedWhen = extendedWhen;
        }

        public boolean isFastEnd() {
            return fastEnd;
        }

        public void setFastEnd(boolean fastEnd) {
            this.fastEnd = fastEnd;
        }

        public boolean isExtended() {
            return extended;
        }

        public void setExtended(boolean extended) {
            this.extended = extended;
        }


    }
}
