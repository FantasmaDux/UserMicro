package io.github.pavelshe11.networkingmicro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetworkingMicroApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkingMicroApplication.class,
                args);
    }

}
