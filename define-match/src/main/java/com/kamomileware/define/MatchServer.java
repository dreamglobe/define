package com.kamomileware.define;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.kamomileware.define")
@EnableAutoConfiguration
//EnableGlobalMethodSecurity(securedEnabled = false, prePostEnabled = false)
public class MatchServer {

    public static void main(String[] args) {
        SpringApplication.run(TermRestApp.class, args);
    }

}