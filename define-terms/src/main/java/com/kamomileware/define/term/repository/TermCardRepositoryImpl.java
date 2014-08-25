package com.kamomileware.define.term.repository;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pepe on 3/08/14.
 */
public class TermCardRepositoryImpl implements TermCardRepositoryCustom {

    @Autowired
    protected TermRepository termDao;

    @Autowired
    protected TermCardRepository cardDao;


    @Override
    public TermCard addNew(TermCard newCard) {
        // TODO: must be max for allowing deletes
        long order = cardDao.count()+1;
        newCard.getDefinitionsList().forEach(d -> d.setCard(null));
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
    public List<TermCard> addNew(Iterable<TermCard> newCards){
        final List<TermCard> result = new ArrayList<>();
        for(TermCard card : newCards) result.add(addNew(card));
        return result;
    }

    @Override
    public TermCard updateWithTerms(TermCard newCard) {
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
    public boolean removeWithTerms(int order) {
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
