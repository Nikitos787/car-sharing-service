package project;

import org.springframework.boot.SpringApplication;
import project.repository.ContainersConfig;

public class TestApplication {
   public static void main(String[] args) {
       SpringApplication
         .from(CarSharingProjectApplication::main) //Application is main entrypoint class
         .with(ContainersConfig.class)
         .run(args);
   }
}
