package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public abstract class AbstractRabbitConfiguration {
	public static final String direct_marketdata_queue = "marketdata.rabbit.queue";
	public static final String direct_request_stock_queue = "request.stock.rabbit.queue";
	public static final String direct_request_option_queue = "request.option.rabbit.queue";
	public static final String direct_request_exchange = "direct_request";
	public static final String routingkey_stock = "request_stock";
	public static final String routingkey_option = "request_option";
	public static final String direct_marketdata_exchange = "direct_marketdata";
	public static final String fanout_exchange = "fanout_all";
	
	private int port = 5672;
	protected abstract void configureRabbitTemplate(RabbitTemplate template);
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory temp = new CachingConnectionFactory("localhost");
		temp.setUsername("test");
		temp.setPassword("test");
		temp.setConnectionLimit(20);
		return temp;
	}
	

	@Bean 
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		//template.setMessageConverter(jsonMessageConverter());
		configureRabbitTemplate(template);
		return template;
	}
	
	@Bean
	public MessageProperties messageProperties() {
		MessageProperties prop = new MessageProperties();
		prop.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		return prop;
	}

	@Bean
	public AmqpTemplate amqpTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		//template.setMessageConverter(jsonMessageConverter());
		configureRabbitTemplate(template);
		return template;
	}
	
	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public Queue directMarketdataQueue() {
		return new Queue(direct_marketdata_queue);
	}
	
	@Bean 
	public Queue directRequestStockQueue() {
		return new Queue(direct_request_stock_queue);
	}
	
	@Bean 
	public Queue directRequestOptionQueue() {
		return new Queue(direct_request_option_queue);
	}
	
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(fanout_exchange);
	}
	
	@Bean
	public TopicExchange directRequestExchange() {
		return new TopicExchange(direct_request_exchange);
	}
	
	//@Bean
	//public Binding directWebappBinding() {
	//	return BindingBuilder.bind(directWebappQueue()).to(directWebappExchange()).with(direct_webapp_queue);
	//}
	
	//@Bean
	//public Binding fanoutBinding() {
	//	return BindingBuilder.bind(directInvoiceQueue()).to(fanoutExchange());
	//}
}
