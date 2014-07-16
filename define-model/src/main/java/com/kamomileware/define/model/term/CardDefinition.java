package com.kamomileware.define.model.term;

import java.util.Map;

/**
 * Created by pepe on 10/07/14.
 */
public class CardDefinition {
    Long id;
    int order;
    Map<TermCategory,Term> definitions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Map<TermCategory, Term> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<TermCategory, Term> definitions) {
        this.definitions = definitions;
    }
}
