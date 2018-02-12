package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitConfiguration {
	public static final String direct_webapp_queue = "webapp.rabbit.queue";
	public static final String direct_invoice_queue = "invoice.rabbit.queue";
	public static final String direct_webapp_exchange = "direct_webapp";
	public static final String fanout_exchange = "fanout_all";
	private static CachingConnectionFactory connectionFactory = null;
	
	@Bean
	public ConnectionFactory connectionFactory() {
		if(connectionFactory == null) {
			CachingConnectionFactory temp = new CachingConnectionFactory("localhost");
			temp.setUsername("test");
			temp.setPassword("test");
			temp.setConnectionLimit(20);
			connectionFactory = temp;
		}
		return connectionFactory;
	}
	
	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}
	
	@Bean
	public AmqpTemplate amqpTemplate() {
		return new RabbitTemplate(connectionFactory());
	}
	
	@Bean
	public Queue directInvoiceQueue() {
		return new Queue(direct_invoice_queue);
	}
	
	@Bean 
	public Queue directWebappQueue() {
		return new Queue(direct_webapp_queue);
	}
	
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(fanout_exchange);
	}
	
	@Bean
	public TopicExchange directWebappExchange() {
		return new TopicExchange(direct_webapp_exchange);
	}
	
	@Bean
	public Binding directWebappBinding() {
		return BindingBuilder.bind(directWebappQueue()).to(directWebappExchange()).with(direct_webapp_queue);
	}
	
	@Bean
	public Binding fanoutBinding() {
		return BindingBuilder.bind(directInvoiceQueue()).to(fanoutExchange());
	}
}
