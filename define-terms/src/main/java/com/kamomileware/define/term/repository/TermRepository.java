package com.kamomileware.define.term.repository;

import com.kamomileware.define.model.term.Term;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TermRepository  extends MongoRepository<Term, String> {

    public List<Term> findByNameLike(@Param("name") String name);

}