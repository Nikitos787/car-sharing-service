package project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarSharingProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarSharingProjectApplication.class, args);
    }
}
