package com.kamomileware.define.dbproc;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCategory;

/**
 * Created by pepe on 9/08/14.
 */
public interface DBService {

    void saveTerms(Term[] terms);
    void saveCat(TermCategory[] cats);
}
