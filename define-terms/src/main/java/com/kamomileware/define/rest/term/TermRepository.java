package com.kamomileware.define.rest.term;

import java.util.List;

import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "terms", path = "term")
public interface TermRepository extends MongoRepository<Term, String> {

	List<Term> findByName(@Param("name") String name);

    List<Term> findByCategory(@Param("category") String category);

    List<Term> findByCard(@Param("card") String card);

}