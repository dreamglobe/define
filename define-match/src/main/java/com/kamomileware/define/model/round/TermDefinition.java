package com.kamomileware.define.model.round;

import com.kamomileware.define.model.term.Term;

/**
 * Created by pepe on 10/07/14.
 */
public class TermDefinition {

    private final boolean correct;
    private final PlayerData playerData;
    private final Term term;
    private final String definition;
    private final Integer definitionId;

    /**
     * Player definition
     * @param playerData the player that is giving the definition
     * @param definition the given definition
     * @param term the term the definition correspond
     */
    public TermDefinition(PlayerData playerData, String definition, Term term) {
        this.playerData = playerData;
        this.playerData.setDefinition(this);
        this.definition = definition;
        this.term = term;
        this.definitionId = calculateDefinitionId(definition);
        this.correct = false;
    }

    /**
     * Correct definition constructor;
     * @param term the term for
     */
    public TermDefinition(Term term) {
        this.playerData = null;
        this.definition = term.getDefinition();
        this.definitionId = calculateDefinitionId(definition);
        this.term = term;
        this.correct = true;
    }

    private Integer calculateDefinitionId(String definition) {
        return (definition+ getSalted()).hashCode();
    }

    private String getSalted() {
        return isCorrect()?"SALTTED":playerData.getPid();
    }

    public Term getTerm() {
        return term;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public String getDefinition() {
        return definition;
    }

    public Integer getId() {
        return definitionId;
    }

    public boolean isCorrect() {
        return correct;
    }
}
