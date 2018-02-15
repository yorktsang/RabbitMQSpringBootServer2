package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.context.JavaConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.AbstractRabbitConfiguration;
import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.client.RabbitConfiguration;


@Controller
@SessionAttributes("name")
public class ClientController {
	
	private static Logger log = Logger.getLogger(ClientController.class);
	
	@Autowired
	private RabbitConfiguration rabbitConfiguration;
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Autowired
	private MessageProperties prop;
	
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
			, @RequestParam final String numOfMsg) {
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
						rabbitTemplate.convertAndSend(amqpMessage);
					}else {
						rabbitTemplate.convertAndSend(route,amqpMessage);
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
	
	private void setupCallbacks() {
		/*
		 * Confirms/returns enabled in application.properties - add the callbacks here.
		 */
		((RabbitTemplate) rabbitTemplate).setConfirmCallback((correlation, ack, reason) -> {
			if (correlation != null) {
				System.out.println("Received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
			}
			this.confirmLatch.countDown();
		});
		((RabbitTemplate) rabbitTemplate).setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
			System.out.println("Returned: " + message + "\nreplyCode: " + replyCode
					+ "\nreplyText: " + replyText + "\nexchange/rk: " + exchange + "/" + routingKey);
			this.returnLatch.countDown();
		});
		/*
		 * Replace the correlation data with one containing the converted message in case
		 * we want to resend it after a nack.
		 */
		((RabbitTemplate) rabbitTemplate).setCorrelationDataPostProcessor((message, correlationData) ->
				new CompleteMessageCorrelationData(correlationData != null ? correlationData.getId() : null, message));
	}
	
	
	
	public String sendMsgToQueue() {
		String sendMsg = getCurrentLocalDateTimeStamp();
		MessageProperties prop = new MessageProperties();
		prop.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		Message amqpMessage = new SimpleMessageConverter().toMessage(sendMsg, prop);
		String routingKey = AbstractRabbitConfiguration.routingkey_stock;
		rabbitTemplate.convertAndSend(routingKey, amqpMessage);
		log.info(sendMsg);
		return "done";
	}
	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS"));
	}
}
