package com.cet.pq.pqgovernanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan("com.cet")
public class PqGovernanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PqGovernanceServiceApplication.class, args);
    }

}
