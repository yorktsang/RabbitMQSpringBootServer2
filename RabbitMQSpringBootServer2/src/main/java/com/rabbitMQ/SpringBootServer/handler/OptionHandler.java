package com.rabbitMQ.SpringBootServer.handler;

import org.apache.log4j.Logger;

public class OptionHandler {

	private static Logger log = Logger.getLogger(OptionHandler.class);
	
	public void handleMessage(String optionRequest) {
		log.info("Received Option Request:"+ optionRequest);
	}
}
