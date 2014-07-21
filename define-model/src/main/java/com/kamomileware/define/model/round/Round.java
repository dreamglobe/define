package com.kamomileware.define.model.round;

import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.PlayerInfo;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.match.MatchResolver;
import com.kamomileware.define.model.term.Term;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.kamomileware.define.model.round.PlayerData.createPlayerData;

/**
 * Created by pepe on 10/07/14.
 */
public class Round<REF> implements DefinitionResolver {

    private final int roundNumber;

    private final Term term;
    private MatchConfiguration matchConf;

    private final RoundPlayers<REF> roundPlayers;
    private List<TermDefinition> roundDefinitions = new ArrayList<>();
    private Set<TurnResult> result = new HashSet<>();

    private final long matchStartTime;
    private final long roundStartTime;

    /**
     * First round constructor
     * @param matchConf
     */
    public Round(MatchConfiguration matchConf, Term term) {
        this.roundNumber = 0;
        this.term = term;
        this.roundPlayers = new RoundPlayers<REF>();
        this.matchConf = matchConf;
        this.roundDefinitions = new ArrayList<>();
        this.matchStartTime = this.roundStartTime = System.currentTimeMillis();
    }

    /**
     * Consecutive round constructor
     * @param previousRound
     */
    public Round(Round<REF> previousRound, Term term) {
        this.roundNumber = previousRound.roundNumber+1;
        this.term = term;
        this.matchConf = previousRound.matchConf;
        this.roundPlayers = previousRound.prepareNextRoundPlayers(this);
        this.roundDefinitions = new ArrayList<>();
        this.matchStartTime = previousRound.matchStartTime;
        this.roundStartTime = System.currentTimeMillis();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Term getTerm() {
        return term;
    }

    @Override
    public MatchConfiguration getMatchConf() {
        return matchConf;
    }

    public void setMatchConf(MatchConfiguration matchConf) {
        this.matchConf = matchConf;
    }

    public List<PlayerData<REF>> getPlayerList(){
        return roundPlayers;
    }

    public boolean addPlayer(REF playerRef, String name, String pid){
        PlayerData<REF> player = createPlayerData(playerRef, name, pid, this);
        return roundPlayers.add(player);
    }

    public boolean removePlayer(REF ref) {
        return roundPlayers.removeByRef(ref);
    }

    public PlayerData<REF> getNextHandPlayer() {
        return roundPlayers.get((roundNumber + 1) % roundPlayers.size());
    }

    public String getPlayerPid(REF ref) {
        return roundPlayers.getPid(ref);
    }

    public List<PlayerInfo> createPlayersInfo(){
        return roundPlayers.createPlayersInfo();
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

    RoundPlayers<REF> prepareNextRoundPlayers(Round<REF> newRound) {
        this.roundPlayers.forEach(p -> p.prepareNextRound(newRound));
        return new RoundPlayers<>(roundPlayers);
    }

    @Override
    public Optional<TermDefinition> findDefinitionById(Integer idDef) {
        return roundDefinitions.stream()
                .filter(d -> d.getDefId().equals(idDef))
                .findFirst();
    }

    public void createRoundDefinitions(){
        this.roundDefinitions.clear();
        this.roundDefinitions.addAll(roundPlayers.stream()
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
        return result.size() >0;
    }

    public boolean hasWinners( ) {
        return roundPlayers.stream().anyMatch(PlayerData::isWinner);
    }

    public List<PlayerData<REF>> getWinners(){
        return this.roundPlayers.stream()
                        .filter(PlayerData::isWinner)
                        .collect(Collectors.toList());
    }

    public List<PlayerData<REF>.Score> getScores() {
        return roundPlayers.stream().map((t) -> t.getScore()).collect(Collectors.toList());
    }

    public List<PlayerScore> createPlayersScores() {
        return roundPlayers.stream().map(PlayerData::createPlayerScore).collect(Collectors.toList());
    }

    public boolean setPlayerReadyInResult(REF playerRef, boolean isReady){
        roundPlayers.get(playerRef).setReadyInResult(isReady);
        return isReady;
    }

    public void calculateRoundResult(){
        this.result.clear();
        this.result.addAll(MatchResolver.resolve(this));
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

    public boolean canExtendPhase(RoundPhase state) {
        return matchConf.getPhaseConf(state).canExtend(this);
    }

    public void extendPhase(RoundPhase state) {
        matchConf.getPhaseConf(state).setExtended(true);
    }

    public long getPhaseTotalDuration(RoundPhase state) {
        return matchConf.getPhaseConf(state).getTotalDuration() * 1000;
    }

    public void endRound(){
        this.roundPlayers.clear();
        this.roundDefinitions.clear();
        this.result.clear();
    }
}
