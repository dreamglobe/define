package com.kamomileware.define.term.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamomileware.define.model.term.Term;
import com.kamomileware.define.model.term.TermCard;
import com.mongodb.*;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import javax.servlet.MultipartConfigElement;
import java.util.ArrayList;
import java.util.List;

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
    MongoDbFactory mongoDbFactory() throws Exception {
        final Mongo mongo = mongo();
        mongo.setWriteConcern(WriteConcern.SAFE);
        return new SimpleMongoDbFactory(mongo, "test");
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    private MappingMongoConverter mappingMongoConverter() throws Exception {
        MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return converter;
    }

    public @Bean(name = "mapper")
    ObjectMapper jsonMapper(){
        return new ObjectMapper();
    }

    @Bean @Profile("dev")
    public LoggingEventListener mappingEventsListener() {
        return new LoggingEventListener();
    }

    public @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }

    @Bean
    public CustomConversions customConversions() throws Exception {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new TermReadConverter());
        return new CustomConversions(converterList);
    }

    @ReadingConverter
    static public class TermReadConverter implements Converter<DBObject, Term> {
        public Term convert(DBObject source) {
            final Term term = new Term((String) source.get("_id"), (String) source.get("def"), (String) ((DBRef) source.get("cat")).getId());
            final DBRef dbRef = (DBRef) source.get("card");
            if (dbRef!=null) {
                TermCard card = new TermCard();
                final long order = Long.parseLong((String) dbRef.getId());
                card.setOrder(order);
                term.setCard(card);
            }
            return term;
        }

    }
}
