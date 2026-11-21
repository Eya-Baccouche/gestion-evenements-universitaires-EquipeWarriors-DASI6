package com.esprit.competitions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CompetitionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompetitionsApplication.class, args);
    }
}
