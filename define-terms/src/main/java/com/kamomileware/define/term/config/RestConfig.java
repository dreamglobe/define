package com.kamomileware.define.term.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.apache.catalina.mapper.Mapper;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import javax.servlet.MultipartConfigElement;

/**
 * Created by pepe on 19/07/14.
 */
@Configuration
@EnableMongoRepositories(basePackages = {"com.kamomileware.define.term", "com.kamomileware.define.model.term"})
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

    public @Bean
    ObjectMapper jsonMapper(){
        return new ObjectMapper();
    }

    public @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }

}
