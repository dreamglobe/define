package com.kamomileware.define.term.dbproc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.model.term.TermCategory;
import com.kamomileware.define.term.repository.TermCardRepository;
import com.kamomileware.define.term.service.DBServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pepe on 9/08/14.
 */
@Service
public class DBMongoServiceImpl implements DBService {

    private static final Logger logger = LoggerFactory.getLogger(DBMongoServiceImpl.class);

    @Autowired
    private MongoTemplate template;

    @Autowired
    private TermCardRepository cardDao;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void clear() {
        template.dropCollection(TermCard.class);
        template.dropCollection(Term.class);
    }

    @Override
    public void importBD(String jsonDump) {
        cardDao.addNew(deserialize(jsonDump).getTermCards());
    }

    @Override
    public String export(){
        return ""; // TODO: read db Values in TIS and export
    }

    private TermsInformationSystem deserialize(String serialized) {
        try {
            return mapper.readValue(serialized, TermsInformationSystem.class);
        } catch (IOException e) {
            throw new DBServiceException(e);
        }
    }

    @PostConstruct
    public void afterPropertySet() {
        if (logger.isDebugEnabled()) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    static class TermsInformationSystem {
        TermCategory[] categories;
        Term[] terms;
        TermCard[] termCards;

        TermsInformationSystem() {
        }

        TermsInformationSystem(TermCategory[] categories, Term[] terms, TermCard[] termCards) {
            this.categories = categories;
            this.terms = terms;
            this.termCards = termCards;
        }

        public List<TermCategory> getCategories() {
            return Arrays.asList(categories);
        }

        public void setCategories(TermCategory[] categories) {
            this.categories = categories;
        }

        public List<Term> getTerms() {
            return Arrays.asList(terms);
        }

        public void setTerms(Term[] terms) {
            this.terms = terms;
        }

        public List<TermCard> getTermCards() {
            return Arrays.asList(termCards);
        }

        public void setTermCards(TermCard[] termCards) {
            this.termCards = termCards;
        }
    }
}
