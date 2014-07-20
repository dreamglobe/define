package com.kamomileware.define.model.match;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kamomileware.define.model.round.Round;
import com.kamomileware.define.model.round.RoundPhase;

import java.util.*;

/**
 * Created by pepe on 10/07/14.
 */
public class MatchConfiguration {
    private int voteValue = 1;
    private int correctVoteValue = 2;
    private int minimumPlayers = 1;
    private int maximumPlayers = 10;

    private Optional<Integer> goalPoints = Optional.ofNullable(45);
    private Optional<Integer> maximumRounds = Optional.ofNullable(null);
    private Optional<Long> timeLimit = Optional.ofNullable(null);

    private PhaseConfiguration definePhaseConf = new PhaseConfiguration(RoundPhase.PHASE_RESPONSE, 180, 60, PhaseExtension.NO_PLAYER, true);
    private PhaseConfiguration votePhaseConf = new PhaseConfiguration(RoundPhase.PHASE_VOTE, 90, 0, PhaseExtension.NEVER, true);
    private PhaseConfiguration resultPhaseConf = new PhaseConfiguration(RoundPhase.PHASE_RESULT, 30, 0, PhaseExtension.NEVER, true);


    private MatchConfiguration() {
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

    public void setCorrectVoteValue(Integer correctVoteValue) {
        this.correctVoteValue = correctVoteValue;
    }

    @JsonIgnore
    public Optional<Integer> getGoalPointsOpt() {
        return goalPoints;
    }

    public Integer getGoalPoints() {
        return goalPoints.orElse(null);
    }

    public void setGoalPoints(Integer goalPoints) {
        this.goalPoints = Optional.ofNullable(goalPoints);
    }

    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    @JsonIgnore
    public Optional<Integer> getMaximumRoundsOpt() {
        return maximumRounds;
    }

    public Integer getMaximumRounds() {
        return maximumRounds.orElse(null);
    }

    @JsonIgnore
    public void setMaximumRounds(Integer maximumRounds) {
        this.maximumRounds = Optional.ofNullable(maximumRounds);
    }

    @JsonIgnore
    public Optional<Long> getTimeLimitOpt() {
        return timeLimit;
    }

    public Long getTimeLimit() {
        return timeLimit.orElse(null);
    }

    @JsonIgnore
    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = Optional.ofNullable(timeLimit);
    }

    public PhaseConfiguration getDefinePhaseConf() {
        return definePhaseConf;
    }


    public void setDefinePhaseConf(PhaseConfiguration definePhaseConf) {
        this.definePhaseConf = definePhaseConf;
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
                result = definePhaseConf;
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

        @JsonCreator
        public PhaseConfiguration(
                @JsonProperty("phase") RoundPhase phase,
                @JsonProperty("duration") int duration,
                @JsonProperty("extendedDuration") int extendedDuration,
                @JsonProperty("extendedWhen") PhaseExtension extendedWhen,
                @JsonProperty("fastEnd") boolean fastEnd) {
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

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public int getExtendedDuration() {
            return extendedDuration;
        }

        public void setExtendedDuration(Integer extendedDuration) {
            this.extendedDuration = extendedDuration;
        }

        @JsonIgnore
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

        @JsonIgnore
        public boolean isExtended() {
            return extended;
        }

        @JsonIgnore
        public void setExtended(boolean extended) {
            this.extended = extended;
        }
    }

    public static MatchConfiguration createDefault() {
        return new MatchConfiguration();
    }
}
