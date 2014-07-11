package com.kamomileware.define.model.round;

import akka.actor.ActorRef;
import com.kamomileware.define.actor.RoundPhase;
import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.term.Term;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by pepe on 10/07/14.
 */
public class Round implements DefinitionResolver {

    private int roundNumber;

    private final List<PlayerData> players;
    private final Map<ActorRef, PlayerData> playersByRef;
    private final Map<String, PlayerData> playersByPid;
    private final Map<String, String> playerNamesByPid;

    private final Term term;
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
        roundDefinitions = null;
        playerNamesByPid = null;
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
        playerNamesByPid = new HashMap<>(previousRound.getPlayerNamesByPid());;
        roundDefinitions = null;
    }

    public boolean addPlayerData(ActorRef playerRef, String name, String pid){
        boolean alreadyExists = playersByRef.containsKey(playerRef) || playersByPid.containsKey(pid);
        if (!alreadyExists) {
            PlayerData player = PlayerData.createPlayerData(playerRef, name, pid, this);
            this.players.add(player);
            this.playersByPid.put(pid, player);
            this.playersByRef.put(playerRef, player);
            this.playerNamesByPid.put(pid, name);
        }
        return !alreadyExists;
    }

    public int removePlayerData(PlayerData playerData){
        final ActorRef ref = playerData.getRef();
        final String pid = playerData.getPid();
        if (players.contains(playerData) && playersByPid.containsKey(pid) && playersByRef.containsKey(ref)) {
            players.remove(playerData);
            playersByPid.remove(pid);
            playersByRef.remove(ref);
            playerNamesByPid.remove(pid);
        }
        return players.size();
    }

    public int removePlayerData(ActorRef ref) {
        return removePlayerData(playersByRef.remove(ref));
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

    public Map<String,String> getPlayerNamesByPid() {
        return Collections.unmodifiableMap(playerNamesByPid);
    }

    public String getPlayerPidByRef(ActorRef sender) {
        return getPlayerDataByRef(sender).getPid();
    }

    public Term getTerm() {
        return term;
    }

    @Override
    public MatchConfiguration getMatchConf() {
        return matchConf;
    }

    @Override
    public Optional<TermDefinition> findDefinitionById(Integer idDef) {
        return players.stream()
                .map(PlayerData::getDefinition)
                .filter(d -> idDef.equals(d.getId()))
                .findFirst();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setPlayerDefinition(ActorRef playerRef, String definition) {
        final PlayerData player = playersByRef.get(playerRef);
        if(player!=null){
            player.setDefinition(term, definition);
        }
    }

    public void buildRoundDefinitions(){
        this.roundDefinitions.addAll(players.stream()
                .map(p -> p.getDefinition())
                .filter(td -> td == null)
                .collect(Collectors.toList()));
        this.roundDefinitions.add(getCorrectDefinition());
        Collections.shuffle(roundDefinitions);
    }

    public TermDefinition getCorrectDefinition() {
        return new TermDefinition(term);
    }

    public void playerVote(ActorRef playerRef, Integer voteId){
        playersByRef.get(playerRef).vote(voteId);
    }

    private List<ItemDefinition> transformTermInItemDefinition(List<TermDefinition> definitions) {
        return definitions.stream().map(d -> new ItemDefinition(d.getId(),d.getDefinition()))
                .collect(Collectors.toList());
    }

    public List<ItemDefinition> getRoundItemDefinitions(){
        return transformTermInItemDefinition(roundDefinitions);
    }

    public List<ItemDefinition> getDefinitionsForPlayer(PlayerData player){
        List<TermDefinition> playerDefinitions = new ArrayList<>(roundDefinitions);
        playerDefinitions.remove(player.getDefinition());
        return transformTermInItemDefinition(playerDefinitions);
    }

    public List<PlayerData.Score> applyVotesAndBuildRoundResults() {
        players.stream().forEach(p -> p.getVote().ifPresent(VoteDefinition::applyVote));
        return getScores();
    }

    public List<PlayerData.Score> getScores() {
        return players.stream().map(PlayerData::getScore).collect(Collectors.toList());
    }

    public boolean isPlayerReadyInResult(ActorRef playerRef){
        return getPlayerDataByRef(playerRef).isReadyInResult();
    }

    public boolean setPlayerReadyInResult(ActorRef playerRef, boolean isReady){
        getPlayerDataByRef(playerRef).setReadyInResult(isReady);
        return isReady;
    }

    public void applyPlayers(Consumer<PlayerData> p){
        players.forEach(p);
    }

    public void applyPlayersRefs(Consumer<ActorRef> p){
        players.stream().map(PlayerData::getRef).forEach(p);
    }

    public boolean hasAnyoneResponse() {
        return players.stream().anyMatch(p -> p.hasResponse());
    }

    public boolean hasEveryoneResponse() {
        return players.stream().allMatch(p -> p.hasResponse());
    }

    public boolean hasEveryonrVote() {
        return players.stream().allMatch(p -> p.hasVote());
    }

    public boolean isEveryoneReadyInResult() {
        return players.stream().allMatch(p -> p.isReadyInResult());
    }

    public boolean canExtendPhase(RoundPhase state) {
        return matchConf.getPhaseConf(state).canExtend(this);
    }

    public void extendPhase(RoundPhase state) {
        matchConf.getPhaseConf(state).setExtended(true);
    }

    public long getPhaseTotalDuration(RoundPhase state) {
        return matchConf.getPhaseConf(state).getTotalDuration();
    }
}
