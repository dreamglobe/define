package com.kamomileware.define.model.term;


import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * Created by pepe on 10/07/14.
 */
@Document (collection = "term")
public class Term {

    @Id
    @NotBlank
    private final String name;

    @Field("def") @NotBlank
    private final String definition;

    @DBRef(lazy = false) @NotNull
    private final TermCategory category;

    @DBRef(lazy = true)
    private TermCard card;

    @PersistenceConstructor
    public Term(String name, String definition, TermCategory category, TermCard card){
        this(name, definition, category);
        this.card = card;
    }

    public Term(String name, String definition, TermCategory ch) {
        this.name = name;
        this.definition = definition;
        this.category = ch;
    }

    public String getDefinition() {
        return definition;
    }

    public String getName() {
        return name;
    }

    public TermCategory getCategory() {
        return category;
    }

    public TermCard getCard() {
        return card;
    }

    public void setCard(TermCard card) {
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
