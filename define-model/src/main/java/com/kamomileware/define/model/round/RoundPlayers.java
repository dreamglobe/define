package com.kamomileware.define.model.round;

import com.kamomileware.define.model.PlayerInfo;
import com.kamomileware.define.model.term.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoundPlayers<REF extends Object> {
    final List<PlayerData<REF>> players;
    final Map<REF, PlayerData<REF>> playersByRef;
    final Map<String, PlayerData<REF>> playersByPid;
    final Map<String, String> playerNamesByPid;

    public RoundPlayers() {
        this.players = new ArrayList<>();
        this.playersByPid = new HashMap<>();
        this.playersByRef = new HashMap<>();
        this.playerNamesByPid = new HashMap<>();
    }

    public RoundPlayers(RoundPlayers previousPlayers) {
        this.players = previousPlayers.getPlayerList();
        this.playersByPid = previousPlayers.getPlayersByPid();
        this.playersByRef = previousPlayers.getPlayersByRef();
        this.playerNamesByPid = previousPlayers.getPlayerNamesByPid();
    }


    public boolean addRoundPlayer(REF playerRef, String name, String pid) {
        boolean alreadyExists = playersByRef.containsKey(playerRef) || playersByPid.containsKey(pid);
        if (!alreadyExists) {
            PlayerData player = PlayerData.createPlayerData(playerRef, name, pid, null);
            this.players.add(player);
            this.playersByPid.put(pid, player);
            this.playersByRef.put(playerRef, player);
            this.playerNamesByPid.put(pid, name);
        }
        return !alreadyExists;
    }

    public int removePlayerData(PlayerData playerData) {
        final Object ref = playerData.getRef();
        final String pid = playerData.getPid();
        if (players.contains(playerData) && playersByPid.containsKey(pid) && playersByRef.containsKey(ref)) {
            players.remove(playerData);
            playersByPid.remove(pid);
            playersByRef.remove(ref);
            playerNamesByPid.remove(pid);
        }
        return players.size();
    }

    public int removePlayerData(REF ref) {
        return removePlayerData(playersByRef.get(ref));
    }

    public PlayerData getPlayerData(int pos) {
        return pos < 0 || pos >= players.size() ? null : players.get(pos);
    }

    public List<PlayerInfo> createPlayersInfo() {
        return players.stream().map(PlayerData::createPlayerInfo).collect(Collectors.toList());
    }

    public void applyPlayers(Consumer<PlayerData<REF>> p) {
        players.forEach(p);
    }

    public void applyPlayersRefs(Consumer<REF> p) {
        players.stream().map(PlayerData::getRef).forEach(p);
    }

    public boolean hasAnyoneResponse() {
        return players.stream().anyMatch(p -> p.hasResponse());
    }

    public boolean hasEveryoneResponse() {
        return players.stream().allMatch(PlayerData::hasResponse);
    }

    public boolean hasEveryoneVote() {
        return players.stream().allMatch(p -> p.hasVote());
    }

    public boolean isEveryoneReadyInResult() {
        return players.stream().allMatch(PlayerData::isReadyInResult);
    }

    // Getter & Setters

    public List<PlayerData<REF>> getPlayerList() {
        return players;
    }

    Map<REF, PlayerData<REF>> getPlayersByRef() {
        return playersByRef;
    }

    Map<String, PlayerData<REF>> getPlayersByPid() {
        return playersByPid;
    }

    public PlayerData<REF> get(REF playerRef) {
        return playersByRef.get(playerRef);
    }

    public PlayerData<REF> get(String pid) {
        return playersByPid.get(pid);
    }

    public Map<String, String> getPlayerNamesByPid() {
        return playerNamesByPid;
    }

    public String getPlayerPidByRef(REF sender) {
        return get(sender).getPid();
    }

    public void setPlayerDefinition(REF playerRef, String definition, Term term) {
        final PlayerData player = get(playerRef);
        if(player!=null){
            player.setDefinition(term, definition);
        }
    }

    public void applyVotes() {
        this.players.stream().forEach(p -> p.getVote().ifPresent(VoteDefinition::applyVote));
        this.players.stream().forEach(p -> p.getScore().applyTurnScore());
    }
}