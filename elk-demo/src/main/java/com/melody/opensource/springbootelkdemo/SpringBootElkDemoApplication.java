package com.melody.opensource.springbootelkdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class SpringBootElkDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootElkDemoApplication.class, args);
    }

}
