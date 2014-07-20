package com.kamomileware.define.model.round;

import com.kamomileware.define.model.PlayerInfo;
import com.kamomileware.define.model.term.Term;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoundPlayers<REF> extends RoundPlayersBase<REF> {
    public RoundPlayers() {
        super();
    }

    public RoundPlayers(RoundPlayers<REF> previousPlayers) {
        super(previousPlayers);
    }

    public List<PlayerInfo> createPlayersInfo() {
        return this.stream().map(PlayerData::createPlayerInfo).collect(Collectors.toList());
    }

    public void applyPlayers(Consumer<PlayerData<REF>> p) {
        this.forEach(p);
    }

    public void applyPlayersRefs(Consumer<REF> p) {
        this.stream().map(PlayerData::getRef).forEach(p);
    }

    public boolean hasAnyoneResponse() {
        return this.stream().anyMatch(p -> p.hasResponse());
    }

    public boolean hasEveryoneResponse() {
        return stream().allMatch(PlayerData::hasResponse);
    }

    public boolean hasEveryoneVote() {
        return stream().allMatch(p -> p.hasVote());
    }

    public boolean isEveryoneReadyInResult() {
        return stream().allMatch(PlayerData::isReadyInResult);
    }

    public PlayerData<REF> get(REF playerRef) {
        return playersByRef.get(playerRef);
    }

    public PlayerData<REF> get(String pid) {
        return playersByPid.get(pid);
    }

    public String getPid(REF sender) {
        return get(sender).getPid();
    }

    public boolean removeByRef(REF ref) {
        return this.remove(playersByRef.get(ref));
    }

    public void setPlayerDefinition(REF playerRef, String definition, Term term) {
        final PlayerData player = get(playerRef);
        if(player!=null){
            player.setDefinition(term, definition);
        }
    }

    public void applyVotes() {
        this.stream().forEach(p -> p.getVote().ifPresent(VoteDefinition::applyVote));
        this.stream().forEach(p -> p.getScore().applyTurnScore());
    }
}