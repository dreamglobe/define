package com.kamomileware.define.term;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Created by pepe on 3/08/14.
 */
public class TermCardRepositoryImpl implements TermCardRepositoryCustom {

    //@Autowired
    protected TermRepository termDao;

    //@Autowired
    protected TermCardRepository cardDao;


    @Override
    public TermCard addNewCard(Map<String, Term> terms) {
        long order = cardDao.count();
        termDao.save(terms.values());
        final TermCard card = cardDao.save(new TermCard(order, terms));
        terms.values().forEach(t -> {
            t.setCard(card);
            termDao.save(t);
        });
        return card;
    }

    @Override
    public TermCard updateCard(int order, Map<String, Term> terms) {
        TermCard card = cardDao.findByOrder(order);
        terms.forEach((k,v)->{
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
            termDao.delete(card.getDefinitions());
            cardDao.delete(card);
            result= true;
        }
        return result;
    }
}
