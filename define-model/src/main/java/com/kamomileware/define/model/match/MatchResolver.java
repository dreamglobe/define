package com.kamomileware.define.model.match;

import com.kamomileware.define.model.round.PlayerData;
import com.kamomileware.define.model.round.Round;
import com.kamomileware.define.model.round.TurnResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pepe on 19/07/14.
 */
public class MatchResolver {

    public static <REF> Set<TurnResult> resolve(Round round) {
        MatchConfiguration config = round.getMatchConf();
        Set<TurnResult> results = new HashSet<>(TurnResult.values().length);
        List<PlayerData<REF>> players = round.getPlayerList();
        // end match conditions
        if(players.size() < config.getMinimumPlayers()){
            results.add(TurnResult.NO_PLAYERS_LEFT);
        }
        if (config.getGoalPoints().isPresent() && round.hasWinners()){
            results.add(TurnResult.GOAL_REACHED);
        }
        if(config.getMaximunRounds().isPresent() && config.getMaximunRounds().get() < round.getRoundNumber()){
            results.add(TurnResult.TURN_LIMIT_REACHED);
        }
        if(config.getTimeLimit().isPresent() && config.getTimeLimit().get() < round.getMatchTime()){
            results.add(TurnResult.TIME_LIMIT_REACHED);
        }
        return results;
    }
}
