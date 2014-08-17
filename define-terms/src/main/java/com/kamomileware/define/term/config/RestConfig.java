package com.kamomileware.define.term.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * Created by pepe on 19/07/14.
 */
@Configuration
@EnableMongoRepositories(basePackages = {"com.kamomileware.define.term", "com.kamomileware.define.model.term", "com.kamomileware.define.config"})
@Import(RepositoryRestMvcConfiguration.class)
public class RestConfig {
    public @Bean
    Mongo mongo() throws Exception {
        return new MongoClient("localhost");
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        final Mongo client = mongo();
        client.setWriteConcern(WriteConcern.SAFE);
        return new MongoTemplate(client, "test");
    }
}
