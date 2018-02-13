package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.logging.Log;
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

import ch.qos.logback.classic.Logger;

@Controller
@SessionAttributes("name")
public class ClientController {
	
	@Autowired
	private RabbitConfiguration rabbitConfiguration;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@RequestMapping (value = "/client", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		sendMsgToQueue();
		return "client";
	}
	
	@RequestMapping (value = "/client", method = RequestMethod.POST)
	public String client(final ModelMap model, @RequestParam final String topic) {
		model.put("topic", topic+"_amend");
		return "client";
	}
	
	public String sendMsgToQueue() {
		String sendMsg = getCurrentLocalDateTimeStamp() + "|" +"test";
		MessageProperties prop = new MessageProperties();
		prop.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		Message amqpMessage = new SimpleMessageConverter().toMessage(sendMsg, prop);
		String routingKey = AbstractRabbitConfiguration.routingkey_stock;
		rabbitTemplate.convertAndSend(routingKey, amqpMessage);
		System.out.println(sendMsg);
		return "done";
	}
	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS"));
	}
}
