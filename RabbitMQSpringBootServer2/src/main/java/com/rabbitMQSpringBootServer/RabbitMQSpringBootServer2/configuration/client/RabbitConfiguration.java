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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.AbstractRabbitConfiguration;

@Configuration
@Import(AbstractRabbitConfiguration.class)
public class RabbitConfiguration extends AbstractRabbitConfiguration{

	@Override
	protected void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		rabbitTemplate.setExchange(direct_request_exchange);	
	}
	
	@Bean
	public Queue marketDataQueue() {		
		return new AnonymousQueue();
	}
	
	/**
	 * Binds to the market data exchange. Interested in any stock quotes.
	 */	
	@Bean
	public Binding requestStockBinding() {		
		return BindingBuilder.bind(directRequestStockQueue()).to(directRequestExchange()).with(routingkey_stock);
	}
	
	@Bean
	public Binding requestOptionBinding() {		
		return BindingBuilder.bind(directRequestOptionQueue()).to(directRequestExchange()).with(routingkey_option);
	}

	@Bean
	public AmqpAdmin rabbitClientAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

}
