package com.kamomileware.define.term.repository;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

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
        //termDao.save(newCard.getDefinitionsList());
        newCard.getDefinitionsList().forEach(t -> {
            t.setCard(null);
            termDao.save(t);
        });
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
        final TermCard card = cardDao.findOne(newCard.getOrder().toString());
        boolean needCardSave = false;
        for(Map.Entry<String, Term> entry : newCard.getDefinitions().entrySet()){
            String cat = entry.getKey();
            Term newTerm = entry.getValue();
            Assert.isTrue(cat.equals(newTerm.getCategory().getName()));
            Term oldTerm = card.getDefinition(cat);
            boolean needTermSave = false;
            if(oldTerm ==null ) oldTerm = new Term();
            if ( !newTerm.getName().equals(oldTerm.getName())) {
                oldTerm.setName(newTerm.getName());
                oldTerm.setDefinition(newTerm.getDefinition());
                if(!needCardSave) needCardSave = true;
                needTermSave = true;
            }else if(!newTerm.getDefinition().equals(oldTerm.getDefinition())) {
                oldTerm.setDefinition(newTerm.getDefinition());
                needTermSave = true;
            }
            if(needTermSave) termDao.save(oldTerm);
        }
        if(needCardSave) cardDao.save(card);
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

    @Override
    public Set<String> findDuplicateTerms() {

        final List<TermCard> allCards = cardDao.findAll();
        Map<String,Integer> counterMap = new HashMap<>(allCards.size());
        for(TermCard card : allCards){
            for(Term term : card.getDefinitionsList()){
                final String name = term.getName();
                if(counterMap.containsKey(name)){
                    final Integer counter = counterMap.get(name);
                    counterMap.put(name, counter.intValue() + 1);
                }else{
                    counterMap.put(name, 1);
                }
            }
        }
        return counterMap.entrySet()
                .stream()
                .filter(p -> p.getValue().intValue() > 1)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).keySet();
    }
}
