package com.kamomileware.define.model.round;

import java.util.*;

/**
 * Created by pepe on 20/07/14.
 */
public class RoundPlayersBase<REF> implements List<PlayerData<REF>> {
    protected final List<PlayerData<REF>> players;
    protected final Map<REF, PlayerData<REF>> playersByRef;
    protected final Map<String, PlayerData<REF>> playersByPid;
    protected final Map<String, String> playerNamesByPid;

    public RoundPlayersBase() {
        this.players = new ArrayList<>();
        this.playersByPid = new HashMap<>();
        this.playersByRef = new HashMap<>();
        this.playerNamesByPid = new HashMap<>();
    }

    public RoundPlayersBase(RoundPlayersBase<REF> previousPlayers) {
        this.players = previousPlayers.players;
        this.playersByPid = previousPlayers.playersByPid;
        this.playersByRef = previousPlayers.playersByRef;
        this.playerNamesByPid = previousPlayers.playerNamesByPid;
    }

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return players.contains(o);
    }

    @Override
    public Iterator<PlayerData<REF>> iterator() {
        return players.iterator();
    }

    @Override
    public Object[] toArray() {
        return players.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return players.toArray(a);
    }

    @Override
    public boolean add(PlayerData<REF> playerData) {
        final boolean add = players.add(playerData);
        if(add) {
            this.playersByPid.put(playerData.getPid(), playerData);
            this.playersByRef.put(playerData.getRef(), playerData);
            this.playerNamesByPid.put(playerData.getPid(), playerData.getName());
        }
        return add;
    }

    @Override
    public boolean remove(Object o) {
        if(o instanceof PlayerData){
            return removePlayerData((PlayerData) o);
        }
        return players.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return players.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends PlayerData<REF>> c) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean addAll(int index, Collection<? extends PlayerData<REF>> c) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() {
        players.clear();
        playersByRef.clear();
        playersByPid.clear();
        playerNamesByPid.clear();
    }

    @Override
    public PlayerData<REF> get(int index) {
        return players.get(index);
    }

    @Override
    public PlayerData<REF> set(int index, PlayerData<REF> element) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void add(int index, PlayerData<REF> element) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PlayerData<REF> remove(int index) {
        final PlayerData<REF> remove = players.remove(index);
        removePlayerData(remove);
        return remove;
    }

    @Override
    public int indexOf(Object o) {
        return players.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return lastIndexOf(o);
    }

    @Override
    public ListIterator<PlayerData<REF>> listIterator() {
        return players.listIterator();
    }

    @Override
    public ListIterator<PlayerData<REF>> listIterator(int index) {
        return players.listIterator(index);
    }

    @Override
    public List<PlayerData<REF>> subList(int fromIndex, int toIndex) {
        return players.subList(fromIndex,toIndex);
    }

    protected boolean removePlayerData(PlayerData playerData) {
        final Object ref = playerData.getRef();
        final String pid = playerData.getPid();
        if (players.contains(playerData)
                && playersByPid.containsKey(pid)
                && playersByRef.containsKey(ref)) {
            players.remove(playerData);
            playersByRef.remove(ref);
            playersByPid.remove(pid);
            playerNamesByPid.remove(pid);
            return true;
        }
        return false;
    }
}
