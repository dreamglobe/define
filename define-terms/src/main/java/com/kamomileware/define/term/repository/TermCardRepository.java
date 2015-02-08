package com.kamomileware.define.term.repository;

import com.kamomileware.define.model.term.TermCard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by pepe on 3/08/14.
 */
@RepositoryRestResource(collectionResourceRel = "cards", path = "cards")
public interface TermCardRepository extends MongoRepository<TermCard, String>, TermCardRepositoryCustom {

    TermCard findByOrder(@Param("id") long order);
}
