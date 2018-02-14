package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.server;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.configuration.AbstractRabbitConfiguration;

@Configuration
@Import(AbstractRabbitConfiguration.class)
public class RabbitServerConfiguration extends AbstractRabbitConfiguration{

	@Override
	protected void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		rabbitTemplate.setExchange(direct_marketdata_exchange); 
	}
	
	@Bean
	public MessageConverter jsonMessageConverter(){
	    return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Queue stockRequestQueue() {		
		return new Queue(direct_request_stock_queue);	
	}
	
	@Bean
	public Queue optionRequestQueue() {		
		return new Queue(direct_request_option_queue);	
	}
	
    @Bean
    public SimpleRabbitListenerContainerFactory jsaFactory(ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
