package com.kamomileware.define.model.match;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.model.term.TermCategory;

import java.util.*;

/**
 * Created by pepe on 1/09/14.
 */
public class CardStack {
    private List<TermCard> cards = new ArrayList<>(40);
    private Set<Integer> cardIds =  new HashSet<>(40);
    private int index = 0;

    public CardStack addCards(Collection<TermCard> newCards){
        cards.addAll(newCards);
        for(TermCard newCard : newCards){
            cardIds.add(newCard.getOrder().intValue());
        }
        return this;
    }

    public TermCard drawCard(){
        if(index>cards.size()) throw new IllegalStateException("No more cards");
        return cards.get(index++ -1);
    }

    public Term drawTerm(){
        int pos = new Random().nextInt(TermCategory.categories.length);
        return drawTerm(TermCategory.categories[pos]);
    }

    public Term drawTerm(TermCategory cat){
        if(index>cards.size()) throw new IllegalStateException("No more cards");
        return cards.get(++index -1).getDefinition(cat.getName());
    }

    public boolean hasNext(){
        return index<=cards.size();
    }

    public Set<Integer> getCardIds(){
        return new HashSet<>(cardIds);
    }

    public int cardsLeft(){
        return cards.size() - (index + 1);
    }
}
