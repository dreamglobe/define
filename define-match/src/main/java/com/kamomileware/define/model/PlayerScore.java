package com.kamomileware.define.model;

import com.kamomileware.define.model.round.PlayerData;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class PlayerScore {
    private final String pid;
    private final int defId;
    private final int votePoints, turnPoints, totalPoints;
    private final List<String> pidVoters;
    private final boolean correctDefinition;

    private PlayerScore(String pid, int defId, int votePoints, int turnPoints, int totalPoints, List<String> pidVoters, boolean correctDefinition) {
        this.pid = pid;
        this.defId = defId;
        this.votePoints = votePoints;
        this.turnPoints = turnPoints;
        this.totalPoints = totalPoints;
        this.pidVoters = pidVoters;
        this.correctDefinition = correctDefinition;
    }

    private PlayerScore(PlayerData.Score score){
        pid = score.getPlayerData().getPid();
        defId = score.getPlayerData().getDefinition().getId();
        votePoints = score.getVoteScore();
        turnPoints = score.getTurnScore();
        totalPoints = score.getTotalScore();
        pidVoters = score.getVoters();
        correctDefinition = score.isCorrectVote();
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

    public static PlayerScore createPlayerScore(PlayerData.Score score) {
        return new PlayerScore(score);
    }

    public static List<PlayerScore> createPlayersScore(List<PlayerData.Score> scores) {
        return scores.stream().map(s -> new PlayerScore(s)).collect(Collectors.toList()) ;
    }
}