package com.kamomileware.define.config;

import com.kamomileware.define.model.term.TermCategory;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;

/**
 * Created by pepe on 6/08/14.
 */
public class RepositoryInitializer {

    MongoTemplate template;

    public RepositoryInitializer(MongoTemplate template) {
        this.template = template;
    }

    @PostConstruct
    public void initialize(){
        template.collectionExists(TermCategory.class);

    }
}
