package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.server;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.AbstractRabbitConfiguration;

@Configuration
@Import(AbstractRabbitConfiguration.class)
public class RabbitServerConfiguration extends AbstractRabbitConfiguration{

	@Override
	protected void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		rabbitTemplate.setExchange(direct_marketdata_exchange); 
	}

	@Bean
	public Queue stockRequestQueue() {		
		return new Queue(direct_request_stock_queue);	
	}
	
	@Bean
	public Queue optionRequestQueue() {		
		return new Queue(direct_request_option_queue);	
	}
}
