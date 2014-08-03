package com.kamomileware.define.term;

import com.kamomileware.define.model.term.TermCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by pepe on 3/08/14.
 */
@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface TermCategoryRepository extends MongoRepository<TermCategory, String> {

}
