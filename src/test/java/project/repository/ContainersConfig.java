package project.repository;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

   @Bean
   @ServiceConnection
   @RestartScope
   public MySQLContainer<?> database() {
       return new MySQLContainer<>("mysql:5.7");
   }
}