package com.kamomileware.define.model.round;

/**
 * Created by pepe on 10/07/14.
 */
public class VoteDefinition<REF> {

    private final TermDefinition selectedDefinition;

    private final PlayerData<REF> voterPlayer;

    private VoteDefinition(PlayerData<REF> voterPlayer, TermDefinition selectedDefinition) {
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

    public static <REF> VoteDefinition createVoteDefinition(PlayerData<REF> voterPlayer, TermDefinition definition) {
        return new VoteDefinition<REF>(voterPlayer, definition);
    }
}
