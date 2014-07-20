package com.kamomileware.define.model;

import com.kamomileware.define.model.round.PlayerData;

import java.util.List;

/**
 */
public class PlayerScore {
    private final String pid;
    private final Integer defId;
    private final int voteScore, turnScore, totalScore;
    private final List<String> pidVoters;
    private final boolean correctDefinition;

    private PlayerScore(String pid, Integer defId, int voteScore, int turnScore,
                        int totalScore, List<String> pidVoters, boolean correctDefinition) {
        this.pid = pid;
        this.defId = defId;
        this.voteScore = voteScore;
        this.turnScore = turnScore;
        this.totalScore = totalScore;
        this.pidVoters = pidVoters;
        this.correctDefinition = correctDefinition;
    }

    public String getPid() {
        return pid;
    }

    public Integer getDefId() {
        return defId;
    }

    public int getVoteScore() {
        return voteScore;
    }

    public int getTurnScore() {
        return turnScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public List<String> getPidVoters() {
        return pidVoters;
    }

    public boolean isCorrectDefinition() {
        return correctDefinition;
    }

    public static PlayerScore create(String pid, Integer defId, int voteScore, int turnScore,
                                     int totalScore, List<String> voters, boolean correctVote) {
        return new PlayerScore(pid, defId, voteScore, turnScore, totalScore, voters, correctVote);
    }
}