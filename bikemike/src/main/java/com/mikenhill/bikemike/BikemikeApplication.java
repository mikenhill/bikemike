package com.mikenhill.bikemike;

import ac.simons.biking2.misc.Coordinate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
public class BikemikeApplication extends SpringBootServletInitializer {

    @Bean
    public Coordinate home(
            final @Value("${biking2.home.longitude}") String longitude,
            final @Value("${biking2.home.latitude}") String latitude
    ) {
        return new Coordinate(longitude, latitude);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BikemikeApplication.class);
    }

    public static void main(String... args) {
        System.setProperty("spring.profiles.default", System.getProperty("spring.profiles.default", "dev"));
        final ApplicationContext applicationContext = SpringApplication.run(BikemikeApplication.class, args);
    }
}
