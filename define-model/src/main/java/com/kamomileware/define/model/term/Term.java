package com.kamomileware.define.model.term;


import com.fasterxml.jackson.annotation.*;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@JsonTypeName("term")
public class Term {

    @Id
    @NotBlank
    private String name;

    @Field("def") @NotBlank
    private String definition;

    @DBRef(lazy = false) @NotNull @JsonIdentityReference
    private TermCategory category;

    @DBRef(lazy = true) @JsonBackReference("terms")
    private TermCard card;

    @PersistenceConstructor
    public Term(String name, String definition, String category, TermCard card){
        this(name, definition, category);
        this.card = card;
    }

    public Term(String name,
                String definition,
                String cat) {
        this(name, definition, TermCategory.forName(cat));
    }

    public Term() {}

    public Term(String name, String definition, TermCategory category){
        this.name = name;
        this.definition = definition;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public TermCategory getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = TermCategory.forName(category);
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
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        if (name != null ? !name.equals(term.name) : term.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
