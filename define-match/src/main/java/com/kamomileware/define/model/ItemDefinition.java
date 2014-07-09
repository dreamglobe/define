package com.kamomileware.define.model;

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

    public static List<ItemDefinition> convertToList(Map<Integer, String> definitions) {
        List<ItemDefinition> result = new ArrayList<>(definitions.size());
        definitions.forEach( (k,v) -> result.add(new ItemDefinition(k,v)));
        return result;
    }
}