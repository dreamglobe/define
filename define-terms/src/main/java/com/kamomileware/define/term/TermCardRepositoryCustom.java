package com.kamomileware.define.term;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;

import java.util.Map;

/**
 * Created by pepe on 3/08/14.
 */
public interface TermCardRepositoryCustom {

    TermCard addNewCard(Map<String,Term> terms);

    TermCard updateCard(int order, Map<String,Term> terms);

    boolean removeCard(int order);
}
