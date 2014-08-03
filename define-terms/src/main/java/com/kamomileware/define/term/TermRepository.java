package com.kamomileware.define.term;

import com.kamomileware.define.model.term.Term;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


//@RepositoryRestResource(collectionResourceRel = "terms", path = "term")
public interface TermRepository  extends MongoRepository<Term, String> {
//
//	List<Term> findByName(@Param("name") String name);
//
//    List<Term> findByCategory(@Param("category") String category);
//
//    List<Term> findByCard(@Param("card") String card);

}