package com.kamomileware.define.config;

import com.kamomileware.define.actor.Player.ClientHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        final HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor = new HttpSessionHandshakeInterceptor();
        registry.addHandler(CrossGateWebSocketHandler(), "/withoutsockjs/crossgate")
                .addInterceptors(httpSessionHandshakeInterceptor);
        registry.addHandler(CrossGateWebSocketHandler(), "/crossgate")
                .withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor)
                .setSessionCookieNeeded(true);
        //registry.addHandler(echoWebSocketHandler(), "/echo", "/echo-issue4");
        //registry.addHandler(echoWebSocketHandler(), "/sockjs/echo").withSockJS();
        //registry.addHandler(echoWebSocketHandler(), "/sockjs/echo-issue4").withSockJS().setHttpMessageCacheSize(20000);

    }

	/*@Bean
    public WebSocketHandler echoWebSocketHandler() {
		return test EchoWebSocketHandler(echoService());
	}*/

	/*@Bean
    public WebSocketHandler snakeWebSocketHandler() {
		return test PerConnectionWebSocketHandler(SnakeWebSocketHandler.class);
	}*/

    @Bean
    public WebSocketHandler CrossGateWebSocketHandler() {
        return new PerConnectionWebSocketHandler(ClientHandler.class);
    }

	/*@Bean
    public DefaultEchoService echoService() {
		return test DefaultEchoService("Did you say \"%s\"?");
	}*/

    // Allow serving HTML files through the default Servlet
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Configuration
    @EnableWebSecurity
    @Order(Ordered.LOWEST_PRECEDENCE - 9)
    static public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
//                .exceptionHandling()
//                    .authenticationEntryPoint(authenticationEntryPoint)
//                    .and()
//                .rememberMe()
//                    .rememberMeServices(rememberMeServices)
//                    .key(env.getProperty("jhipster.security.rememberme.key"))
//                    .and()
                .formLogin()
                    .loginProcessingUrl("/app/authentication")
//                    .successHandler(ajaxAuthenticationSuccessHandler)
//                    .failureHandler(ajaxAuthenticationFailureHandler)
                    .usernameParameter("name")
                    .passwordParameter("passwd")
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/app/logout")
//                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                    .and()
                .csrf()
                    .disable()
                    .headers()
                    .frameOptions().disable()
                .authorizeRequests()
                    .antMatchers("/app/rest/register").permitAll()
                    .antMatchers("/app/rest/activate").permitAll()
                    .antMatchers("/app/rest/authenticate").permitAll()
//                    .antMatchers("/app/rest/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/app/**").authenticated()
//                    .antMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/websocket/**").permitAll()
//                    .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
//                    .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .antMatchers("/protected/**").authenticated();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers("/bower_components/**")
                    .antMatchers("/fonts/**")
                    .antMatchers("/images/**")
                    .antMatchers("/scripts/**")
                    .antMatchers("/styles/**")
                    .antMatchers("/views/**")
                    .antMatchers("/swagger-ui/**")
                    .antMatchers("/console/**");
        }
        
        /*@Override
        protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
            authManagerBuilder.inMemoryAuthentication()
                    .withUser("test").password("test123").roles("USER");
        }*/
    }

}