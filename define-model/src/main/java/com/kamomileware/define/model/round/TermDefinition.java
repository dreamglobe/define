package com.kamomileware.define.model.round;

import com.kamomileware.define.model.ItemDefinition;
import com.kamomileware.define.model.term.Term;

/**
 * Created by pepe on 10/07/14.
 */
public class TermDefinition<REF> {

    private final boolean correct;
    private final PlayerData<REF> playerData;
    private final Term term;
    private final String text;
    private final String originalDefinition;
    private final Integer defId;

    /**
     * Player definition
     * @param playerData the player that is giving the definition
     * @param text the given definition
     * @param term the term the definition correspond
     */
    public TermDefinition(PlayerData<REF> playerData, String text, Term term) {
        this.playerData = playerData;
        this.playerData.setDefinition(this);
        this.originalDefinition = text;
        this.text = term.getCategory().format(text);
        this.term = term;
        this.defId = calculateDefinitionId(text);
        this.correct = false;
    }

    /**
     * Correct definition constructor;
     * @param term the term for
     */
    public TermDefinition(Term term) {
        this.correct = true;
        this.playerData = null;
        this.originalDefinition = term.getDefinition();
        this.text = term.getCategory().format(originalDefinition);
        this.defId = calculateDefinitionId(text);
        this.term = term;
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

    public PlayerData<REF> getPlayerData() {
        return playerData;
    }

    public String getText() {
        return text;
    }

    public Integer getDefId() {
        return defId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public static ItemDefinition createItemDefinition(TermDefinition definition){
        return new ItemDefinition(definition.getDefId(), definition.getText());
    }
}
