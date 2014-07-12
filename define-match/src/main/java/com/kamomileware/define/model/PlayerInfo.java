package com.kamomileware.define.model;

/**
 * Created by pepe on 12/07/14.
 */
public class PlayerInfo {
    private final String pid;
    private final String name;
    private final int totalPoints;
    private final int lastPhasePoints;

    public PlayerInfo(String pid, String name, int totalPoints, int lastPhasePoints) {
        this.pid = pid;
        this.name = name;
        this.totalPoints = totalPoints;
        this.lastPhasePoints = lastPhasePoints;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getLastPhasePoints() {
        return lastPhasePoints;
    }
}
