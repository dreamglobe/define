package com.kamomileware.define.model.term;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Created by pepe on 10/07/14.
 */
@Document(collection = "cards") @TypeAlias("card")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "order")
@JsonTypeName("card")
public class TermCard {

    @Id
    private String order;

    @DBRef(lazy = true)
    @JsonManagedReference("terms")
    @JsonDeserialize(keyAs = String.class, contentAs = Term.class)
    private Map<String,Term> definitions;

    @PersistenceConstructor
    public TermCard(String order,
                    Map<String,Term> definitions){
        this.order = order;
        this.definitions = definitions;
    }

    public TermCard() { }

    public Long  getOrder() {
        return order!=null? Long.parseLong(order):null;
    }

    public void setOrder(Long order) {
        this.order = order.toString();
    }

    @JsonIgnore
    public Term getDefinition(String catName) {
        return definitions.get(catName);
    }

    public void setDefinition(String catName, Term term) {
        this.definitions.put(catName, term);
    }

    @JsonIgnore
    public Iterable<Term> getDefinitionsList(){
        return definitions.values();
    }

    public void setDefinitions(Map<String, Term> definitions){
        this.definitions = definitions;
    }


    public Map<String, Term> getDefinitions() {
        return definitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermCard termCard = (TermCard) o;

        if (order != null ? !order.equals(termCard.order) : termCard.order != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return order != null ? order.hashCode() : 0;
    }
}
