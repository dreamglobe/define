package com.kamomileware.define.model.round;

import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.PlayerInfo;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.term.Term;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by pepe on 10/07/14.
 */
public class Round<REF> implements DefinitionResolver {

    private final int roundNumber;

    private final Term term;
    private final MatchConfiguration matchConf;

    private final RoundPlayers<REF> roundPlayers;
    private List<TermDefinition> roundDefinitions;
    private Set<TurnResult> result;

    private final long matchStartTime;
    private final long roundStartTime;

    /**
     * First round constructor
     * @param matchConf
     */
    public Round(MatchConfiguration matchConf, Term term) {
        this.roundNumber = 0;
        this.term = term;
        this.roundPlayers = new RoundPlayers();
        this.matchConf = matchConf;
        this.roundDefinitions = new ArrayList<>();
        this.matchStartTime = this.roundStartTime = System.currentTimeMillis();
    }

    /**
     * Consecutive round constructor
     * @param previousRound
     */
    public Round(Round previousRound, Term term) {
        this.roundNumber = previousRound.getRoundNumber()+1;
        this.term = term;
        this.matchConf = previousRound.getMatchConf();
        this.roundPlayers = previousRound.nextRoundPlayers(this);
        this.roundDefinitions = new ArrayList<>();
        this.matchStartTime = previousRound.getMatchStartTime();
        this.roundStartTime = System.currentTimeMillis();
    }

    public boolean addRoundPlayer(REF playerRef, String name, String pid){
        return roundPlayers.addRoundPlayer(playerRef, name, pid);
    }

    public int removeRoundPlayer(REF ref) {
        return roundPlayers.removePlayerData(ref);
    }

    public PlayerData<REF> getNextHandPlayer() {
        return roundPlayers.getPlayerList().get((roundNumber + 1) % roundPlayers.getPlayerList().size());
    }

    public String getPlayerPid(REF sender) {
        return roundPlayers.getPlayerPidByRef(sender);
    }

    public List<PlayerInfo> createPlayersInfo(){
        return roundPlayers.createPlayersInfo();
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
        return roundDefinitions.stream()
                .filter(d -> d.getDefId().equals(idDef))
                .findFirst();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void createRoundDefinitions(){
        this.roundDefinitions = new ArrayList<>(roundPlayers.getPlayerList().stream()
                .map(PlayerData::getDefinition)
                .filter(td -> td != null)
                .collect(Collectors.toList()));
        this.roundDefinitions.add(getCorrectDefinition());
        Collections.shuffle(roundDefinitions);
    }

    public TermDefinition getCorrectDefinition() {
        return new TermDefinition(term);
    }

    public void playerVote(REF playerRef, Integer voteId){
        roundPlayers.get(playerRef).vote(voteId);
    }

    public List<ItemDefinition> getRoundItemDefinitions(){
        return transformTermInItemDefinition(roundDefinitions);
    }

    private List<ItemDefinition> transformTermInItemDefinition(List<TermDefinition> definitions) {
        return definitions.stream().map(TermDefinition::createItemDefinition).collect(Collectors.toList());
    }

    public void setPlayerDefinition(REF playerRef, String definition){
        roundPlayers.setPlayerDefinition(playerRef, definition, this.getTerm());
    }

    public List<ItemDefinition> getDefinitionsForPlayer(PlayerData<REF> player){
        List<TermDefinition> playerDefinitions = new ArrayList<>(roundDefinitions);
        playerDefinitions.remove(player.getDefinition());
        return transformTermInItemDefinition(playerDefinitions);
    }

    public void applyVotes() {
        roundPlayers.applyVotes();
    }

    public boolean isFinalRound() {
        return result != null && result.size() >0;
    }

    public boolean hasWinners( ) {
        return roundPlayers.getPlayerList().stream().anyMatch(PlayerData::isWinner);
    }

    public List<PlayerData<REF>> getWinners(){
        return this.roundPlayers.getPlayerList().stream()
                        .filter(PlayerData::isWinner)
                        .collect(Collectors.toList());
    }

    public List<PlayerData<REF>.Score> getScores() {
        return roundPlayers.getPlayerList().stream().map((t) -> t.getScore()).collect(Collectors.toList());
    }

    public List<PlayerScore> createPlayersScores() {
        return roundPlayers.getPlayerList().stream().map(PlayerData::createPlayerScore).collect(Collectors.toList());
    }

    public boolean setPlayerReadyInResult(REF playerRef, boolean isReady){
        roundPlayers.get(playerRef).setReadyInResult(isReady);
        return isReady;
    }

    public void applyPlayers(Consumer<PlayerData<REF>> p){
        roundPlayers.applyPlayers(p);
    }

    public void applyPlayersRefs(Consumer<REF> p){
        roundPlayers.applyPlayersRefs(p);
    }

    public boolean hasAnyoneResponse() {
        return roundPlayers.hasAnyoneResponse();
    }

    public boolean hasEveryoneResponse() {
        return roundPlayers.hasEveryoneResponse();
    }

    public boolean hasEveryoneVote() {
        return roundPlayers.hasEveryoneVote();
    }

    public boolean isEveryoneReadyInResult() {
        return roundPlayers.isEveryoneReadyInResult();
    }

    public boolean canExtendPhase(RoundPhase state) {
        return matchConf.getPhaseConf(state).canExtend(this);
    }

    public void extendPhase(RoundPhase state) {
        matchConf.getPhaseConf(state).setExtended(true);
    }

    public long getPhaseTotalDuration(RoundPhase state) {
        return matchConf.getPhaseConf(state).getTotalDuration() * 1000;
    }

    public Set<TurnResult> calculateRoundResult() {
        return result = matchConf.resolveRound(this);
    }

    public long getMatchStartTime() {
        return matchStartTime;
    }

    public long getRoundStartTime() {
        return roundStartTime;
    }

    public long getMatchTime( ) {
        return System.currentTimeMillis() - this.matchStartTime;
    }

    public List<PlayerData<REF>> getPlayerList(){
        return roundPlayers.getPlayerList();
    }

    RoundPlayers<REF> nextRoundPlayers(Round newRound) {
        this.roundPlayers.getPlayerList().forEach(p -> p.prepareNextRound(newRound));
        return roundPlayers;
    }

}
