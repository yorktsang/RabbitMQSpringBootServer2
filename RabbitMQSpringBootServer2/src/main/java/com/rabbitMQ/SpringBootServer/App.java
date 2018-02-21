package com.rabbitMQ.SpringBootServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Hello world!
 *
 */
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan("com.rabbitMQ.SpringBootServer")
public class App 
{

	
    public static void main( String[] args ) throws InterruptedException
    {
        SpringApplication.run(App.class, args);
    }
}
