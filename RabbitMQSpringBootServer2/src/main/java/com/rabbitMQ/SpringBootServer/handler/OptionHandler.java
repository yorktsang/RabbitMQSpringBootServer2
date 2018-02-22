package com.rabbitMQ.SpringBootServer.handler;

import java.sql.Timestamp;
import java.util.Random;

import org.apache.log4j.Logger;

import com.rabbitMQ.SpringBootServer.domain.OrderRequest;
import com.rabbitMQ.SpringBootServer.domain.OrderResponse;

public class OptionHandler {

	private static Logger log = Logger.getLogger(OptionHandler.class);
	private static Random randomGenerator = new Random();
	
	public OrderResponse handleMessage(OrderRequest optionRequest) {
		log.info("Received Option Request:"+ optionRequest.toString());
		try {
			Thread.sleep(randomGenerator.nextInt(5000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		OrderResponse result = new OrderResponse();
		result.setOrderType(optionRequest.getOrderType());
		result.setPrice(optionRequest.getPrice());
		result.setQuantity(optionRequest.getQuantity());
		result.setRequestId(optionRequest.getId());
		result.setTicker(optionRequest.getTicker());
		result.setUserName(optionRequest.getUserName());
		result.setBuyRequest(optionRequest.isBuyRequest());
		result.setTimestamp(timestamp.getTime());
		result.setConfirmationNumber(result.getId());
		
		return result;
	}
}
