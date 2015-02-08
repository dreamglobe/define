package com.kamomileware.define.term;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.kamomileware.define.term","com.kamomileware.define.web"})
@EnableAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class })
public class TermRestApp {

    public static void main(String[] args) {
        SpringApplication.run(TermRestApp.class, args);
    }
}
