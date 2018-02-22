package com.rabbitMQ.SpringBootServer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
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
		temp.setUsername("guest");
		temp.setPassword("guest");
		temp.setConnectionLimit(20);
		temp.setPublisherConfirms(true); // this must be set for publisher confirmation
		temp.setPublisherReturns(true);
		return temp;
	}
	

	@Bean
	public MessageProperties messageProperties() {
		MessageProperties prop = new MessageProperties();
		prop.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		return prop;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}
	@Bean
	public Queue testQueue() {
		return new Queue("request.test.queue");
	}
	@Bean
	public Queue testReplyQueue() {
		return new Queue("reply.test.queue");
	}
	@Bean
	public DirectExchange testExchange() {
		return new DirectExchange("directExchange_test");
	}
	@Bean
	public Binding testBinding() {
		return BindingBuilder.bind(testQueue()).to(testExchange()).with("test");
	}
	
	@Bean
	public Queue stockQuoteQueue() {
		return new Queue("stock.quote.queue");
	}
	@Bean
	public Queue stockOrderQueue() {
		return new Queue("stock.order.queue");
	}
	@Bean
	public Queue optionQuoteQueue() {
		return new Queue("option.quote.queue");
	}
	@Bean
	public Queue optionOrderQueue() {
		return new Queue("option.order.queue");
	}
	
	@Bean
	public TopicExchange topicQuoteExchange() {
		return new TopicExchange("topicExchange_Quote");
	}
	
	@Bean
	public Binding stockQuoteBinding() {
		return BindingBuilder.bind(stockQuoteQueue()).to(topicQuoteExchange()).with("stock");
	}
	@Bean
	public Binding optionQuoteBinding() {
		return BindingBuilder.bind(optionQuoteQueue()).to(topicQuoteExchange()).with("option");
	}
	
	@Bean
	public TopicExchange topicOrderExchange() {
		return new TopicExchange("topicExchange_Order");
	}
	@Bean
	public Binding stockOrderBinding() {
		return BindingBuilder.bind(stockOrderQueue()).to(topicOrderExchange()).with("stock");
	}
	@Bean
	public Binding optionOrderBinding() {
		return BindingBuilder.bind(optionOrderQueue()).to(topicOrderExchange()).with("option");
	}

	/*
	 * 	
	@Bean
	public Queue simpleMessageListenerContainerQueue() {		
		return new Queue("SimpleMessageListenerContainer.demo.rabbit.queue");	
	}
	
	@Bean
	public Queue rabbitListenerQueue() {		
		return new Queue("RabbitListener.demo.rabbit.queue");	
	}
	
	@Bean
	public Queue directMarketdataQueue() {
		return new Queue("marketdata.rabbit.queue");
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
	*/
}
