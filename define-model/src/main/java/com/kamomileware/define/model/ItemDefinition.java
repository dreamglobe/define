package com.kamomileware.define.model;

/**
 *
 */
public class ItemDefinition {
    private final Integer defId;
    private final String text;

    public ItemDefinition(Integer defId, String text) {
        this.defId = defId;
        this.text = text;
    }

    public Integer getDefId() {
        return this.defId;
    }

    public String getText() {
        return this.text;
    }

}