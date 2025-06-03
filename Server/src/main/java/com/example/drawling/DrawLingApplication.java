package com.example.drawling;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;





@SpringBootApplication(scanBasePackages = "com.example.drawling", exclude = { SecurityAutoConfiguration.class })
@EnableTransactionManagement
public class DrawLingApplication {

    static Dotenv dotenv = Dotenv.load();

    public static void main(String[] args) {
        System.setProperty("JDBC_CONNECTION", dotenv.get("JDBC_CONNECTION"));
        System.setProperty("JDBC_USERNAME", dotenv.get("JDBC_USERNAME"));
        System.setProperty("JDBC_PASSWORD", dotenv.get("JDBC_PASSWORD"));
        System.setProperty("JDBC_DRIVER_NAME", dotenv.get("JDBC_DRIVER_NAME"));

        System.setProperty("MUTIPART_MAX_SIZE", dotenv.get("MUTIPART_MAX_SIZE"));

        System.setProperty("FLYWAY_URL", dotenv.get("FLYWAY_URL"));
        System.setProperty("FLYWAY_USER", dotenv.get("FLYWAY_USER"));
        System.setProperty("FLYWAY_PASSWORD", dotenv.get("FLYWAY_PASSWORD"));

        System.setProperty("DIR_IMAGES", dotenv.get("DIR_IMAGES"));



        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRE", dotenv.get("JWT_EXPIRE"));
        System.setProperty("REFRESH_TOKEN_EXPIRE", dotenv.get("REFRESH_TOKEN_EXPIRE"));
        SpringApplication.run(DrawLingApplication.class, args);
    }

}
