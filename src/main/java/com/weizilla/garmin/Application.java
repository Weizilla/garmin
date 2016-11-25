package com.weizilla.garmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.weizilla.garmin")
public class Application
{
    public static void main(String[] args) throws Exception
    {
        SpringApplication application = new SpringApplication(Application.class);
        application.setWebEnvironment(false);
        application.run(args);
    }
}
