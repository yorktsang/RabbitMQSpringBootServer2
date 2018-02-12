package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerBackgroundService implements CommandLineRunner {
	private static int counter = -1;
	
	public static int getCounter() {
		return counter;
	}
	
	public void run(String... arg0) throws Exception {
		System.out.println("ServiceBackground commandlineRunner start as a component");
		while (counter < 10000) {
			counter = counter +1;
			Thread.sleep(5000);
		}
		
	}
}
