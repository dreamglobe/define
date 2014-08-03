package com.kamomileware.define.model.term;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pepe on 10/07/14.
 */
@Document(collection = "card") @TypeAlias("card")
public class TermCard {

    String id;

    @Indexed(unique = true)
    private long order;

    @DBRef
    private Map<String,Term> definitions = new HashMap<>(TermCategory.categories.length);

    @PersistenceConstructor
    public TermCard(long order, Map<String,Term> definitions){
        this.order = order;
        this.definitions = definitions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public Term getDefinition(TermCategory cat) {
        return definitions.get(cat.getName());
    }

    public void setDefinition(TermCategory cat, Term term) {
        this.definitions.put(cat.getName(), term);
    }

    public Term getDefinition(String catName) {
        return definitions.get(catName);
    }

    public void setDefinition(String catName, Term term) {
        this.definitions.put(catName, term);
    }

    public Iterable<Term> getDefinitions(){
        return definitions.values();
    }
}
