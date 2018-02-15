package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.controller;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.AbstractRabbitConfiguration;
import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.client.RabbitConfiguration;
import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.server.RabbitServerConfiguration;
import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.service.ServerBackgroundService;

@Controller
public class ServerController {
	
	private static Logger log = Logger.getLogger(ServerController.class);
	
	
	@Autowired
	ServerBackgroundService serverBackground;
	
	@Autowired
	private RabbitServerConfiguration rabbitConfiguration;
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@RequestMapping(value ="/server", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		String currentTime = String.valueOf(serverBackground.getCounter());
		model.put("currentTime", currentTime);
		return "server";
	}
	
	@RequestMapping(value ="/server/retreive", method = RequestMethod.GET)
	public String retreive(final ModelMap model) {
		String currentTime = String.valueOf(serverBackground.getCounter());
		String message = (String) rabbitTemplate.receiveAndConvert(AbstractRabbitConfiguration.direct_request_stock_queue);
		
		model.put("currentTime", currentTime);
		model.put("message", message);
		return "server";
	}
	@RequestMapping(value ="/server/retreive_reply", method = RequestMethod.GET)
	public String retreiveAndReply(final ModelMap model) {
		String currentTime = String.valueOf(serverBackground.getCounter());
		//String message = (String) rabbitTemplate.receiveAndConvert(AbstractRabbitConfiguration.direct_request_stock_queue);
		String replyRoute = "test";
		//boolean received = rabbitTemplate.receiveAndReply(AbstractRabbitConfiguration.direct_request_stock_queue, new ReceiveAndReplyCallback<String,String>(){
		boolean received = rabbitTemplate.receiveAndReply(AbstractRabbitConfiguration.direct_request_stock_queue, new ReceiveAndReplyCallback<String,String>(){ 
			@Override
			public String handle(String payload) {
				log.info("retreiveAndReply():" + payload);
				String currentTime2 = String.valueOf(serverBackground.getCounter());
				return ("receive ok at " + currentTime2);
			}}, "reply", "");
		
		model.put("currentTime", currentTime);
		model.put("message", "receive ok at "+currentTime);
		return "server";
	}
	

	
	@RabbitListener(queues="request.option.rabbit.queue", containerFactory="jsaFactory")
    public void recievedMessage(String company) {
        log.info("Recieved Message: " + company);
    }
}
