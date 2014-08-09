package com.kamomileware.define.term;

import com.kamomileware.define.model.term.Term;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface TermRepository  extends MongoRepository<Term, String> {


}