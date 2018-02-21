package com.rabbitMQ.SpringBootServer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.rabbitMQ.SpringBootServer.configuration.AbstractRabbitConfiguration;
import com.rabbitMQ.SpringBootServer.configuration.client.RabbitConfiguration;
import com.rabbitMQ.SpringBootServer.controller.ClientController.CompleteMessageCorrelationData;

@Controller
@Scope("session")
public class ClientNoCallbackController {
	
	private static Logger log = Logger.getLogger(ClientNoCallbackController.class);
	
	@Autowired
	private RabbitConfiguration rabbitConfiguration;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private MessageProperties prop;
	
	private boolean callbackConfig = false;
	
	private final CountDownLatch listenLatch = new CountDownLatch(1);

	private final CountDownLatch confirmLatch = new CountDownLatch(1);

	private final CountDownLatch returnLatch = new CountDownLatch(1);
	
	@RequestMapping (value = "/client_nocallback", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		return "client";
	}
	
	@RequestMapping (value = "/client_nocallback", method = RequestMethod.POST)
	public String client(final ModelMap model, @RequestParam final String topic
			, @RequestParam final String route
			, @RequestParam final String numOfMsg) throws InterruptedException {
		int count = 1;
		String errorMessage ="";
		if(numOfMsg.matches("[0-9]+")) {
			count = Integer.parseInt(numOfMsg);
		}else {
			errorMessage += "Failed to parse Number, set msg number as 1.<br/>";
		}
		
		for(int i = 0; i < count; i++) {
			String sendMsg = getCurrentLocalDateTimeStamp();
			Message amqpMessage = new SimpleMessageConverter().toMessage(sendMsg, prop);
			try {
				if(topic.isEmpty()) {
					if(route.isEmpty()) {
						rabbitTemplate.convertAndSend(route, amqpMessage);
						//rabbitTemplate.convertAndSend(route,amqpMessage);
					}
				}else {
					if(route.isEmpty()) {
						//not feasible
					}else {
						rabbitTemplate.convertAndSend(topic,route,amqpMessage);
					}
				}
			}catch(Exception e) {
				errorMessage += "Failed to send Msg: "+ sendMsg +"<br/>";
			}
			
		}
		model.put("errorMessage", errorMessage);
		return "client";
	}


	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS"));
	}
	
	static class CompleteMessageCorrelationData extends CorrelationData {

		private final Message message;
		CompleteMessageCorrelationData(String id, Message message) {
			super(id);
			this.message = message;
		}
		public Message getMessage() {
			return this.message;
		}

		@Override
		public String toString() {
			return "CompleteMessageCorrelationData [id=" + getId() + ", message=" + this.message + "]";
		}

	}
}
