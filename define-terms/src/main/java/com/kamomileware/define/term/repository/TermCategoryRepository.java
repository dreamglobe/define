package com.kamomileware.define.term.repository;


import com.kamomileware.define.model.term.TermCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by pepe on 3/08/14.
 */
public interface TermCategoryRepository extends MongoRepository<TermCategory, String> {

}
