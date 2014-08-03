package com.kamomileware.define.term;

import com.kamomileware.define.model.term.TermsCard;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by pepe on 3/08/14.
 */
public interface TermCardRepository extends MongoRepository<TermsCard, String> {
}
