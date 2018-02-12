package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.client;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.AbstractRabbitConfiguration;


public class RabbitConfiguration extends AbstractRabbitConfiguration{

	@Value("${stocks.quote.pattern}")
	private String marketDataRoutingKey;
	
	@Override
	protected void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		rabbitTemplate.setRoutingKey(direct_request_exchange);	
	}
	
	@Bean
	public Queue marketDataQueue() {		
		return new AnonymousQueue();
	}
	
	/**
	 * Binds to the market data exchange. Interested in any stock quotes.
	 */	
	@Bean
	public Binding marketDataBinding() {		
		return BindingBuilder.bind(marketDataQueue()).to(marketDataExchange()).with(marketDataRoutingKey);
	}

	/**
	 * This queue does not need a binding, since it relies on the default exchange.
	 */	
	@Bean
	public Queue traderJoeQueue() {	
		return new AnonymousQueue();
	}
	
	@Bean
	public AmqpAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

}
