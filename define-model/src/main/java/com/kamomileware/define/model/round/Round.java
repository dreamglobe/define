package com.kamomileware.define.model.round;

import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.PlayerInfo;
import com.kamomileware.define.model.PlayerScore;
import com.kamomileware.define.model.match.MatchConfiguration;
import com.kamomileware.define.model.match.MatchResolver;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCategory;

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
    private final Term previousTerm;
    private MatchConfiguration matchConf;

    private final RoundPlayers<REF> roundPlayers;
    private List<TermDefinition> roundDefinitions = new ArrayList<>();
    private Set<TurnResult> result = new HashSet<>();

    private final long matchStartTime;
    private final long roundStartTime;

    /**
     * First round constructor
     */
    private Round() {
        this.roundNumber = 0;
        this.previousTerm = null;
        this.roundPlayers = new RoundPlayers<REF>();
        this.roundDefinitions = null;
        this.term = null;
        this.matchStartTime = this.roundStartTime = System.currentTimeMillis();
    }

    /**
     * Consecutive round constructor
     * @param previousRound
     */
    private Round(Round<REF> previousRound, Term term) {
        this.roundNumber = previousRound.roundNumber+1;
        this.previousTerm = previousRound.getTerm();
        this.term = term;
        this.matchConf = previousRound.matchConf;
        this.roundDefinitions = new ArrayList<>();
        this.matchStartTime = previousRound.matchStartTime;
        this.roundStartTime = System.currentTimeMillis();
        this.roundPlayers = previousRound.prepareNextRoundPlayers(this);
    }

    public Round<REF> createNext(Term nextTerm) {
        return new Round(this, nextTerm);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Term getPreviousTerm() {
        return previousTerm;
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

    public TermCategory getNextHandCategory() {
        int score = this.getNextHandPlayer().getScore().getTotalScore();
        int pos = score % TermCategory.categories.length;
        return TermCategory.categories[pos];
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

    public boolean hasAnyoneVote() {
        return roundPlayers.hasAnyoneVote();
    }

    public boolean isEveryoneReadyInResult() {
        return roundPlayers.isEveryoneReadyInResult();
    }

    public boolean isAnyoneReadyInResult() {
        return roundPlayers.isAnyoneReadyInResult();
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

    public boolean extendPhase(RoundPhase state) {
        if(matchConf.getPhaseConf(state).canExtend(this)) {
            matchConf.getPhaseConf(state).setExtended(true);
            return true;
        }else{
            return false;
        }
    }

    public long getPhaseDurationInMillis(RoundPhase state) {
        return matchConf.getPhaseConf(state).getDurationInSeconds() * 1000;
    }

    public void endRound(){
//        this.roundPlayers.clear();
//        this.roundDefinitions.clear();
//        this.result.clear();
    }

    public boolean isFastEnd(RoundPhase phase) {
        boolean result = false;
        if(matchConf.getPhaseConf(phase).isFastEnd()){
            switch(phase){
                case PHASE_RESPONSE:
                    result = hasEveryoneResponse();
                    break;
                case PHASE_VOTE:
                    result = hasEveryoneVote();
                    break;
                case PHASE_RESULT:
                    result = isEveryoneReadyInResult();
                    break;
            }
        }
        return result;
    }

    public void resetExtendedState(RoundPhase state) {
        final MatchConfiguration.PhaseConfiguration phaseConf = this.getMatchConf().getPhaseConf(state);
        if(phaseConf.isExtended()) {
            phaseConf.setExtended(false);
        }
    }

    public static <REF> Round<REF> createEmptyRound() {
        return new Round<>();
    }
}
