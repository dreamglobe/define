package com.kamomileware.define.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.kamomileware.define.extension.SpringWSocketClientExtension;
import com.kamomileware.define.actor.MatchActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

/**
 * The application configuration.
 */
@Configuration
class AppConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("deffineMatchSystem");
        SpringWSocketClientExtension.SpringExtProvider.get(system).initialize(applicationContext);
        // initialize the application context in the Akka Spring Extension
        // SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);
        return system;
    }

    @Bean @Named("matchActor")
    public ActorRef crossGateActor() {
        return actorSystem().actorOf(MatchActor.props(), "match");
    }
}
