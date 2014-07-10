package com.kamomileware.define.model.term;

/**
 * Created by pepe on 10/07/14.
 */
public enum TermCategory {
    NP("Nombre propio"),

    CH("Cheli"),

    SG("S.I.G.L.A"),

    AB("Adverv");

    String label;

    TermCategory(String label){
        this.label = label;
    }
}
