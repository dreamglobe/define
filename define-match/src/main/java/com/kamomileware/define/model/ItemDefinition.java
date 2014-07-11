package com.kamomileware.define.model;

import com.kamomileware.define.model.round.TermDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ItemDefinition {
    private final Integer defId;
    private final String definition;

    public ItemDefinition(Integer defId, String definition) {
        this.defId = defId;
        this.definition = definition;
    }

    public Integer getDefId() {
        return this.defId;
    }

    public String getDefinition() {
        return this.definition;
    }

}