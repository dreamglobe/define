package com.kamomileware.define.match.extension;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.WebSocketSession;

/**
 * An Akka Extension to provide access to Spring managed Actor Beans.
 */
public class SpringWSocketClientExtension extends
  AbstractExtensionId<SpringWSocketClientExtension.SpringExt> {

  /**
   * The identifier used to access the SpringExtension.
   */
  public static SpringWSocketClientExtension SpringExtProvider = new SpringWSocketClientExtension();

  /**
   * Is used by Akka to instantiate the Extension identified by this
   * ExtensionId, internal use only.
   */
  @Override
  public SpringExt createExtension(ExtendedActorSystem system) {
    return new SpringExt();
  }

  /**
   * The Extension implementation.
   */
  public static class SpringExt implements Extension {
    private volatile ApplicationContext applicationContext;

    /**
     * Used to initialize the Spring application context for the extension.
     * @param applicationContext
     */
    public void initialize(ApplicationContext applicationContext) {
      this.applicationContext = applicationContext;
    }

    /**
     * Create a Props for the specified actorBeanName using the
     * SpringActorProducer class.
     *
     * @param actorBeanName  The name of the actor bean to create Props for
     * @return a Props that will create the named actor bean using Spring
     */
    public Props props(WebSocketSession wsSession, String actorBeanName) {
      return Props.create(SpringWebSocketActorProducer.class, applicationContext, wsSession, actorBeanName);
    }
  }
}