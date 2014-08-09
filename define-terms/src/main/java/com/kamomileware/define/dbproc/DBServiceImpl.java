package com.kamomileware.define.dbproc;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCategory;
import com.kamomileware.define.term.TermCardRepository;
import com.kamomileware.define.term.TermCategoryRepository;
import com.kamomileware.define.term.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Created by pepe on 9/08/14.
 */
@Service
public class DBServiceImpl implements  DBService {

    @Autowired
    TermCategoryRepository catDao;

    @Autowired
    TermRepository termDao;

    @Autowired
    TermCardRepository cardDao;

    public void saveTerms(Term[] terms) {
        termDao.save(Arrays.asList(terms));
    }

    public void saveCat(TermCategory[] cats){
        catDao.save(Arrays.asList(cats));
    }


}
