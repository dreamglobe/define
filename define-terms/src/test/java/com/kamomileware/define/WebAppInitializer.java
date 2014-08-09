package com.kamomileware.define;

import com.kamomileware.define.config.RestConfig;
import com.kamomileware.define.config.SecurityConfig;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

/**
 * Created by pepe on 6/08/14.
 */
public class WebAppInitializer {
    private WebApplicationContext createRootContext(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(RestConfig.class, SecurityConfig.class);
        rootContext.refresh();

        servletContext.addListener(new ContextLoaderListener(rootContext));
        servletContext.setInitParameter("defaultHtmlEscape", "true");

        return rootContext;
    }

    private void configureSpringSecurity(ServletContext servletContext, WebApplicationContext rootContext) {
        FilterRegistration.Dynamic springSecurity = servletContext.addFilter("springSecurityFilterChain",
                new DelegatingFilterProxy("springSecurityFilterChain", rootContext));
        springSecurity.addMappingForUrlPatterns(null, true, "/*");
    }

    //@Override
    public void onStartup(ServletContext servletContext) {
        WebApplicationContext rootContext = createRootContext(servletContext);

        //configureSpringMvc(servletContext, rootContext);

        configureSpringSecurity(servletContext, rootContext);
    }
}
