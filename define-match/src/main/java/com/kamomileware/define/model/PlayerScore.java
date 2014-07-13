package com.kamomileware.define.model;

import java.util.List;

/**
 * TODO: Remove PlayerData.Score reference
 */
public class PlayerScore {
    private final String pid;
    private final Integer defId;
    private final int voteScore, turnScore, totalPoints;
    private final List<String> pidVoters;
    private final boolean correctDefinition;

    private PlayerScore(String pid, Integer defId, int voteScore, int turnScore,
                        int totalPoints, List<String> pidVoters, boolean correctDefinition) {
        this.pid = pid;
        this.defId = defId;
        this.voteScore = voteScore;
        this.turnScore = turnScore;
        this.totalPoints = totalPoints;
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

    public int getTotalPoints() {
        return totalPoints;
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