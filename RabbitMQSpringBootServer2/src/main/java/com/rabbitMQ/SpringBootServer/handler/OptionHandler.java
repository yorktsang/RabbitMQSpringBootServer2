package com.rabbitMQ.SpringBootServer.handler;

import org.apache.log4j.Logger;

public class OptionHandler {

	private static Logger log = Logger.getLogger(OptionHandler.class);
	
	public String handleMessage(String optionRequest) {
		log.info("Received Option Request:"+ optionRequest);
		return "Option Ordered";
	}
}
