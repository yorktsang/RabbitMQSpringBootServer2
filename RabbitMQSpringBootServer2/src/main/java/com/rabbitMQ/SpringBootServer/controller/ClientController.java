package com.rabbitMQ.SpringBootServer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.context.JavaConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.rabbitMQ.SpringBootServer.configuration.AbstractRabbitConfiguration;
import com.rabbitMQ.SpringBootServer.configuration.client.RabbitClientConfiguration;
import com.rabbitMQ.SpringBootServer.controller.ClientNoCallbackController.CompleteMessageCorrelationData;


@Controller
@Scope("session")
public class ClientController {
	
	private static Logger log = Logger.getLogger(ClientController.class);
	
	@Autowired
	private RabbitClientConfiguration rabbitConfiguration;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private MessageProperties prop;
	
	private boolean callbackConfig = false;
	
	private final CountDownLatch listenLatch = new CountDownLatch(1);

	private final CountDownLatch confirmLatch = new CountDownLatch(1);

	private final CountDownLatch returnLatch = new CountDownLatch(1);
	
	@RequestMapping (value = "/client", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		return "client";
	}
	
	@RequestMapping (value = "/client", method = RequestMethod.POST)
	public String client(final ModelMap model, @RequestParam final String topic
			, @RequestParam final String route
			, @RequestParam final String numOfMsg) throws InterruptedException {
		setupCallbacks();
		int count = 1;
		String errorMessage ="";

		if(numOfMsg.matches("[0-9]+")) {
			count = Integer.parseInt(numOfMsg);
		}else {
			errorMessage += "Failed to parse Number, set msg number as 1.<br/>";
		}
		
		for(int i = 0; i < count; i++) {
			String correlationId = UUID.randomUUID().toString();
			String sendMsg = getCurrentLocalDateTimeStamp();
			Message amqpMessage = new SimpleMessageConverter().toMessage(sendMsg, prop);
			try {
				if(topic.isEmpty()) {
					if(route.isEmpty()) {
						String receive = "";
						//receive =(String)rabbitTemplate.convertSendAndReceive(route, amqpMessage, new CorrelationData(correlationId));
						rabbitTemplate.convertAndSend(route, amqpMessage, new CorrelationData(correlationId));
						log.info("rabbitTemplate sent message with correlationId:" + correlationId);
						log.info(receive);
						//rabbitTemplate.convertAndSend(route,amqpMessage);
					}
				}else {
					if(route.isEmpty()) {
						//not feasible
					}else {
						String receive = "";
						//receive = (String)rabbitTemplate.convertSendAndReceive(route, amqpMessage, new CorrelationData(correlationId));
						rabbitTemplate.convertAndSend(topic,route,amqpMessage, new CorrelationData(correlationId));
						log.info("rabbitTemplate sent message with correlationId:" + correlationId);
						log.info(receive);
					}
				}
			}catch(Exception e) {
				errorMessage += "Failed to send Msg: "+ sendMsg +"<br/>";
			}
			
			if (this.confirmLatch.await(10, TimeUnit.SECONDS)) {
				log.info("Confirm received");
			}
			else {
				log.info("Confirm NOT received");
			}

		}
		model.put("errorMessage", errorMessage);
		return "client";
	}


	private void setupCallbacks() {
		/*
		 * Confirms/returns enabled in application.properties - add the callbacks here.
		 */
		if(!callbackConfig) {
			log.info("Set Call back");
			//ConfirmCallback to provide implementation for 
			//org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback.confirm(correlation, ack, reason)
			rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
				if (correlation != null) {
					log.info("setConfirmCallback received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
				}
				this.confirmLatch.countDown();
			});
			rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
				log.info("Returned: " + message + "\nreplyCode: " + replyCode
						+ "\nreplyText: " + replyText + "\nexchange/rk: " + exchange + "/" + routingKey);
				this.returnLatch.countDown();
			});
			/*
			 * Replace the correlation data with one containing the converted message in case
			 * we want to resend it after a nack.
			 */
			
			//rabbitTemplate.setCorrelationDataPostProcessor((message, correlationData) ->
			//new CompleteMessageCorrelationData(correlationData != null ? correlationData.getId() : null, message));
			
			callbackConfig = true;
		}
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
