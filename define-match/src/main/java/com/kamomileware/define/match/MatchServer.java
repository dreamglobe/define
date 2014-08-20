package com.kamomileware.define.match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.kamomileware.define.match")
@EnableAutoConfiguration
public class MatchServer {
    public static void main(String[] args) {
        SpringApplication.run(MatchServer.class, args);
    }
}