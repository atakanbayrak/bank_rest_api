package org.app.sekom_java_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootApplication
public class SekomJavaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SekomJavaApiApplication.class, args);
    }


}
