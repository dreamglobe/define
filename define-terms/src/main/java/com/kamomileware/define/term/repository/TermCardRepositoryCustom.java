package com.kamomileware.define.term.repository;


import com.kamomileware.define.model.term.TermCard;

import java.util.List;


/**
 * Created by pepe on 3/08/14.
 */
public interface TermCardRepositoryCustom {

    TermCard addNew(TermCard newCard);

    List<TermCard> addNew(Iterable<TermCard> newCards);

    TermCard updateWithTerms(TermCard newCard);

    boolean removeWithTerms(int order);

    java.util.Set<String> findDuplicateTerms();
}
