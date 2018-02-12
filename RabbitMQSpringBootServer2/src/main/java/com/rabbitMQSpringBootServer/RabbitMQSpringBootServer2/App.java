package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Hello world!
 *
 */
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan("com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2")
public class App 
{

	
    public static void main( String[] args ) throws InterruptedException
    {
        SpringApplication.run(App.class, args);
    }
}
