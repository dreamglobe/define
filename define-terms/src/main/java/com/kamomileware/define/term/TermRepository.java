package com.kamomileware.define.term;

import com.kamomileware.define.model.term.Term;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "terms", path = "terms")
public interface TermRepository  extends MongoRepository<Term, String> {


}