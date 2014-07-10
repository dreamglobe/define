package com.kamomileware.define.model.round;

import akka.actor.ActorRef;
import com.kamomileware.define.model.term.Term;

import java.util.*;

/**
 * Created by pepe on 10/07/14.
 */
public class Round implements DefinitionResolver {

    private int roundNumber;

    private final List<PlayerData> players;
    private final Map<ActorRef, PlayerData> playersByRef;
    private final Map<String, PlayerData> playersByPid;

    private final Term term;
    private final Map<Integer, TermDefinition> termDefinitionMap;
    private final List<TermDefinition> roundDefinitions;

    private final MatchConfiguration matchConf;


    /**
     * First round constructor
     * @param term
     * @param matchConf
     */
    public Round(Term term, MatchConfiguration matchConf) {
        this.roundNumber = 1;
        this.term = term;
        this.matchConf = matchConf;
        this.players = null;
        this.playersByPid = null;
        this.playersByRef = null;
        this.termDefinitionMap = null;
        roundDefinitions = null;
    }

    /**
     * Consecutive round constructor
     * @param term
     * @param previousRound
     */
    public Round(Term term, Round previousRound) {
        this.term = term;
        this.roundNumber = previousRound.getRoundNumber();
        this.matchConf = previousRound.getMatchConf();
        this.players = new ArrayList<>(previousRound.getPlayers());
        this.playersByPid = new HashMap<>(previousRound.getPlayersByPid());
        this.playersByRef = new HashMap<>(previousRound.getPlayersByRef());
        this.termDefinitionMap = null;
        roundDefinitions = null;
    }

    public boolean addPlayerData(ActorRef playerRef, String name, String pid){
        boolean added = false;
        if (!playersByRef.containsKey(playerRef) && !playersByPid.containsKey(pid)) {
            PlayerData newPlayerData = PlayerData.createPlayerData(playerRef, name, pid, this);
            this.players.add(newPlayerData);
            this.playersByPid.put(pid, newPlayerData);
            this.playersByRef.put(playerRef, newPlayerData);
            added = true;
        }
        return added;
    }

    public boolean removePlayerData(PlayerData playerData){
        boolean removed = false;
        final ActorRef ref = playerData.getRef();
        final String pid = playerData.getPid();
        if (players.contains(playerData) && playersByPid.containsKey(pid) && playersByRef.containsKey(ref)) {
            players.remove(playerData);
            playersByPid.remove(pid);
            playersByRef.remove(ref);
            removed = true;
        }
        return removed;
    }

    public PlayerData getPlayerDataByRef(ActorRef playerRef) {
        return playersByRef.get(playerRef);
    }

    public PlayerData getPlayerDataByPid(String pid) {
        return playersByPid.get(pid);
    }

    public PlayerData getPlayerData(int pos) {
        return pos<0 || pos >= players.size()? null: players.get(pos);
    }

    public PlayerData getNextHandPlayer() {
        return players.get((roundNumber+1) % players.size());
    }

    Map<ActorRef, PlayerData> getPlayersByRef() {
        return playersByRef;
    }

    Map<String, PlayerData> getPlayersByPid() {
        return playersByPid;
    }

    List<PlayerData> getPlayers() {
        return players;
    }

    public Term getTerm() {
        return term;
    }

    public MatchConfiguration getMatchConf() {
        return matchConf;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    TermDefinition getDefinitionById(Integer definitionId) {
        return termDefinitionMap.get(definitionId);
    }

    public void setRoundDefinitions(){
        this.roundDefinitions.addAll(shuffleDefinitions());
    }

    List<TermDefinition> shuffleDefinitions(){
        final ArrayList<TermDefinition> result = new ArrayList<TermDefinition>(termDefinitionMap.values());
        Collections.shuffle(result);
        return result;
    }

    List<TermDefinition> getShuffleDefinitionsForPlayer(String pid){
        List<TermDefinition> playerDefinitions = new ArrayList<>(this.shuffleDefinitions());
        playerDefinitions.remove(playersByPid.get(pid).getDefinition());
        return playerDefinitions;
    }

    @Override
    public TermDefinition resolveDefinitionId(Integer id) {
        return getDefinitionById(id);
    }
}
