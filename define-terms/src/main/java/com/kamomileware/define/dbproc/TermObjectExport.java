package com.kamomileware.define.aux;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.model.term.TermCategory;
import com.kamomileware.define.term.StaticTermRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pepe on 8/08/14.
 */
public class TermObjectExport {
    final static Logger logger = LoggerFactory.getLogger(TermObjectExport.class);
    static ObjectMapper mapper;

    public static void main(String[] args){
        mapper = new ObjectMapper();
        if(logger.isDebugEnabled()) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        final List<Term> terms = StaticTermRepository.get();
        final List<TermCard> cards = createCards(terms);

        TermsInformationSystem tis = new TermsInformationSystem(
                TermCategory.categories,
                terms.toArray(new Term[terms.size()]),
                cards.toArray(new TermCard[cards.size()]));

        String serialized = null;
        try {
            serialized = serialize(tis);
            logger.debug(serialized);
            TermsInformationSystem tis2 = deserialize(serialized);
            String serialized2 = serialize(tis2);
            logger.debug(serialized2);
            logger.info(serialized.equals(serialized2)?"Correcto":"Fallo");
        } catch (IOException e) {
            logger.error("Error processing: ", e);
        }
    }

    static String serialize(TermsInformationSystem tis) throws JsonProcessingException {
        return mapper.writeValueAsString(tis);
    }

    static TermsInformationSystem deserialize(String serialized) throws IOException {
        return mapper.readValue(serialized, TermsInformationSystem.class);
    }

    private static List<TermCard> createCards(List<Term> terms) {
        int numCards = terms.size()/4;
        List<TermCard> result = new ArrayList<>(numCards);
        for(int i=0; i< numCards; i++){
            Map<String, Term> definitions = new HashMap<>(4);
            final TermCard card = new TermCard((long) i, definitions);
            for(int j=0; j<4; j++){
                Term term = terms.get(i*4+j);
                definitions.put(term.getCategory().getName(), term);
                term.setCard(card);
            }
            result.add(card);
        }
        return result;
    }

    static class TermsInformationSystem {
        TermCategory[] categories;
        Term[] terms;
        TermCard[] termCards;

        TermsInformationSystem() { }

        TermsInformationSystem(TermCategory[] categories, Term[] terms, TermCard[] termCards) {
            this.categories = categories;
            this.terms = terms;
            this.termCards = termCards;
        }

        public TermCategory[] getCategories() {
            return categories;
        }

        public void setCategories(TermCategory[] categories) {
            this.categories = categories;
        }

        public Term[] getTerms() {
            return terms;
        }

        public void setTerms(Term[] terms) {
            this.terms = terms;
        }

        public TermCard[] getTermCards() {
            return termCards;
        }

        public void setTermCards(TermCard[] termCards) {
            this.termCards = termCards;
        }
    }
}
