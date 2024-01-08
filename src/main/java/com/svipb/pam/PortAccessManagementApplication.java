package com.svipb.pam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class PortAccessManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PortAccessManagementApplication.class, args);
    }
}
