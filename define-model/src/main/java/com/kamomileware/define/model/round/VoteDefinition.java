package com.kamomileware.define.model.round;

/**
 * Created by pepe on 10/07/14.
 */
public class VoteDefinition<REF> {

    private final TermDefinition selectedDefinition;

    private final PlayerData<REF> voterPlayer;

    private VoteDefinition(PlayerData voterPlayer, TermDefinition selectedDefinition) {
        this.voterPlayer = voterPlayer;
        this.selectedDefinition = selectedDefinition;
    }

    public TermDefinition getSelectedDefinition() {
        return selectedDefinition;
    }

    public void applyVote(){
        if(selectedDefinition.isCorrect()){
            voterPlayer.getScore().correctVote();
        } else {
            selectedDefinition.getPlayerData().getScore().voteObtained(voterPlayer.getPid());
        }
    }

    public static VoteDefinition createVoteDefinition(PlayerData voterPlayer, TermDefinition definition) {
        return new VoteDefinition(voterPlayer, definition);
    }
}
