package com.kamomileware.define.match.config;

import akka.actor.*;
import akka.routing.FromConfig;
import com.kamomileware.define.match.actor.DBWorker;
import com.kamomileware.define.match.actor.MatchActor;
import com.kamomileware.define.match.extension.SpringExtension;
import com.kamomileware.define.match.extension.SpringWSocketClientExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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
        // initialize the application context in the Akka Spring Extension
        SpringWSocketClientExtension.SpringExtProvider.get(system).initialize(applicationContext);
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);
        return system;
    }

    @Bean
    @Named("matchActor")
    public ActorRef crossGateActor() {
        return actorSystem().actorOf(MatchActor.props(), "match");
    }


    @Bean
    @Named("dbWorkersRouter")
    public ActorRef dbWorkersRouter() {
        final SupervisorStrategy strategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES),
                        Collections.<Class<? extends Throwable>>singletonList(Exception.class));
        return actorSystem().actorOf(DBWorker.props()
                        .withRouter(FromConfig.getInstance()
                                .withSupervisorStrategy(strategy))
                        .withDispatcher("db-workers-dispatcher"),
                "dbWorkerRouter");
    }
}
