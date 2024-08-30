package ru.practicum;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Import({StatsClient.class, RestTemplate.class})
public class EwmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EwmServiceApplication.class, args);
    }
}