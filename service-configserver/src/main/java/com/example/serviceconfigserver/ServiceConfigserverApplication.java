package com.example.serviceconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ServiceConfigserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceConfigserverApplication.class, args);
    }

}
