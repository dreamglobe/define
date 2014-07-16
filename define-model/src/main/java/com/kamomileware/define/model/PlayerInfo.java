package com.kamomileware.define.model;

/**
 * Created by pepe on 12/07/14.
 */
public class PlayerInfo {
    private final String pid;
    private final String name;
    private final boolean isReady;
    private final int totalScore;
    private final int turnScore;

    public PlayerInfo(String pid, String name, boolean isReady, int totalPoints, int lastPhasePoints) {
        this.pid = pid;
        this.name = name;
        this.isReady = isReady;
        this.totalScore = totalPoints;
        this.turnScore = lastPhasePoints;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return isReady;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getTurnScore() {
        return turnScore;
    }
}
