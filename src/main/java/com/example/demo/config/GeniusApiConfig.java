package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeniusApiConfig {

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    @Bean
    public String geniusAccessToken(Dotenv dotenv) {
        return dotenv.get("CLIENT_TOKEN", "");
    }
}

