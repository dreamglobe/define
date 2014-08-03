@Configuration
@EnableWebSecurity
@Order(Ordered.LOWEST_PRECEDENCE - 9)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
