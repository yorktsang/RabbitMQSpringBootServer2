package com.rabbitMQ.SpringBootServer.configuration.client;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import com.rabbitMQ.SpringBootServer.configuration.AbstractRabbitConfiguration;

@Configuration
@Import(AbstractRabbitConfiguration.class)
@Scope("prototype")
public class RabbitClientConfiguration extends AbstractRabbitConfiguration{

	@Bean 
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		//template.setMessageConverter(jsonMessageConverter());
		configureRabbitTemplate(template);
		return template;
	}

	/*
	@Bean
	@Primary
	public AmqpTemplate amqpTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		//template.setMessageConverter(jsonMessageConverter());
		configureRabbitTemplate(template);
		return template;
	}*/
	
	@Override
	protected void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		//do nothing, default exchange depends on request type
	}
	
	@Bean
	public Queue marketDataQueue() {		
		return new AnonymousQueue();
	}

	@Bean
	public AmqpAdmin rabbitClientAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

}
