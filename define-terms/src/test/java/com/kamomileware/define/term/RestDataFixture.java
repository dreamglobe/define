package com.kamomileware.define.term;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import com.kamomileware.define.model.term.TermCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by pepe on 5/08/14.
 */
public class RestDataFixture {

    public static final String CAT1_NAME = "DBG1";
    public static final String CAT2_NAME = "DBG2";
    public static final String CAT3_NAME = "DBG3";
    public static final String CAT4_NAME = "DBG4";


    public static TermCard notYetCreatedCard(){
        return new TermCard(-1L, definitions());
    }

    public static TermCard newlyCreatedCard(){
        int order = new Random().nextInt(1000);
        return new TermCard(new Long(order), definitions());
    }

    public static Map<String, Term> definitions() {
        Map<String, Term> terms = new HashMap(4);
        terms.put(CAT1_NAME, term1());
        terms.put(CAT2_NAME, term2());
        terms.put(CAT3_NAME, term3());
        terms.put(CAT4_NAME, term4());
        return terms;
    }

    public static Term term4() {
        return new Term("term4", "def4", category4());
    }

    public static Term term3() {
        return new Term("term3", "def3", category3());
    }

    public static Term term2() {
        return new Term("term2", "def2", category2());
    }

    public static Term term1() {
        return new Term("term1", "def1", category1());
    }

    public static TermCategory category1(){
        return TermCategory.NP;
    }

    public static TermCategory category2(){
        return TermCategory.CH;
    }

    public static TermCategory category3(){
        return TermCategory.SG;
    }

    public static TermCategory category4(){
        return TermCategory.AB;
    }

    public static String createEventsAsJSON() throws JsonProcessingException {
        final String s = new ObjectMapper().writeValueAsString(notYetCreatedCard());
        return s;
    }
}
