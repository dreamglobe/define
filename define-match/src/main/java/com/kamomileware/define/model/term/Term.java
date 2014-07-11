package com.kamomileware.define.model.term;

/**
 * Created by pepe on 10/07/14.
 */
public class Term {

    private Long id;
    private String name;
    private String definition;
    private TermCategory category;
    private CardDefinition card;

    public Term(String name, String definition, TermCategory ch) {
        this.name = name;
        this.definition = definition;
        this.category = ch;
    }

    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TermCategory getCategory() {
        return category;
    }

    public void setCategory(TermCategory category) {
        this.category = category;
    }

    public CardDefinition getCard() {
        return card;
    }

    public void setCard(CardDefinition card) {
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Term)) return false;

        Term term = (Term) o;

        if (category != null ? !category.equals(term.category) : term.category != null) return false;
        if (!definition.equals(term.definition)) return false;
        if (!name.equals(term.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + definition.hashCode();
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
