package com.kamomileware.define.model.round;

/**
 * Created by pepe on 10/07/14.
 */
public class VoteDefinition {

    private final TermDefinition selectedDefinition;

    private VoteDefinition(TermDefinition selectedDefinition) {
        this.selectedDefinition = selectedDefinition;
    }

    public TermDefinition getSelectedDefinition() {
        return selectedDefinition;
    }

    public static VoteDefinition createVoteDefinition(TermDefinition definition) {
        return new VoteDefinition(definition);
    }
}
