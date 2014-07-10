package com.kamomileware.define.model.round;

/**
 * Created by pepe on 10/07/14.
 */
public class MatchConfiguration {
    private int voteValue;
    private int correctVoteValue;
    private int goalPoints;

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

    public int getGoalPoints() {
        return goalPoints;
    }

    public void setGoalPoints(int goalPoints) {
        this.goalPoints = goalPoints;
    }
}
