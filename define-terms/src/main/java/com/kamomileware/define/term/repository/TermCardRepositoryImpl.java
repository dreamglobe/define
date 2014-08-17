package com.kamomileware.define.term.repository;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Created by pepe on 3/08/14.
 */
public class TermCardRepositoryImpl implements TermCardRepositoryCustom {

    @Autowired
    protected TermRepository termDao;

    @Autowired
    protected TermCardRepository cardDao;


    @Override
    public TermCard addNewCard(TermCard newCard) {
        // TODO: must be max for allowing deletes
        long order = cardDao.count();
        termDao.save(newCard.getDefinitionsList());
        newCard.setOrder(order);
        final TermCard card = cardDao.save(newCard);
        newCard.getDefinitionsList().forEach(t -> {
            t.setCard(card);
            termDao.save(t);
        });
        return card;
    }

    @Override
    public TermCard updateCard(TermCard newCard) {
        TermCard card = cardDao.findByOrder(newCard.getOrder());
        newCard.getDefinitions().forEach((k,v)->{
            Assert.isTrue(k.equals(v.getCategory().getName()));
            Term oldTerm = card.getDefinition(k);
            if(!oldTerm.getName().equals(v.getName())){
                termDao.delete(oldTerm);
            }
            v.setCard(card);
            termDao.save(v);
        });
        return card;
    }

    @Override
    public boolean removeCard(int order) {
        boolean result = false;
        TermCard card = cardDao.findByOrder(order);
        if(card!=null) {
            termDao.delete(card.getDefinitionsList());
            cardDao.delete(card);
            result= true;
        }
        return result;
    }
}
