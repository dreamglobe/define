package com.kamomileware.define.model.term;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pepe on 10/07/14.
 */
@Document
public class TermsCard {

    String id;

    @Indexed(unique = true)
    private int order;

    private Map<TermCategory,Term> definitions = new HashMap<TermCategory,Term>(4);



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Term getDefinition(TermCategory cat) {
        return definitions.get(cat);
    }

    public void setDefinition(TermCategory cat, Term term) {
        this.definitions.put(cat, term);
    }
}
