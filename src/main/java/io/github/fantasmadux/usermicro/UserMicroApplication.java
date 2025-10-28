package io.github.fantasmadux.usermicro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "io.github.fantasmadux.usermicro")
public class UserMicroApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserMicroApplication.class,
                args);
    }

}
