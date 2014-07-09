package com.kamomileware.define.model;

import java.util.List;

/**
 *
 */
public class PlayerScore {
    private final String pid;
    private final int defId;
    private final int votePoints, turnPoints, totalPoints;
    private final List<String> pidVoters;
    private final boolean correctDefinition;

    public PlayerScore(String pid, int defId, int votePoints, int turnPoints, int totalPoints, List<String> pidVoters, boolean correctDefinition) {
        this.pid = pid;
        this.defId = defId;
        this.votePoints = votePoints;
        this.turnPoints = turnPoints;
        this.totalPoints = totalPoints;
        this.pidVoters = pidVoters;
        this.correctDefinition = correctDefinition;
    }

    public String getPid() {
        return pid;
    }

    public int getDefId() {
        return defId;
    }

    public int getVotePoints() {
        return votePoints;
    }

    public int getTurnPoints() {
        return turnPoints;
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
}