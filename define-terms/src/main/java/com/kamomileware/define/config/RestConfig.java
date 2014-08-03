package com.kamomileware.define.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by pepe on 19/07/14.
 */
@Configuration
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
