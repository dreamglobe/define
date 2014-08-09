package com.kamomileware.define.term;


import com.kamomileware.define.model.term.TermCard;


/**
 * Created by pepe on 3/08/14.
 */
public interface TermCardRepositoryCustom {

    TermCard addNewCard(TermCard newCard);

    TermCard updateCard(TermCard newCard);

    boolean removeCard(int order);
}
