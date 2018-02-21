package com.rabbitMQ.SpringBootServer.configuration.server;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import com.rabbitMQ.SpringBootServer.configuration.AbstractRabbitConfiguration;
import com.rabbitmq.client.Channel;

@Configuration
@Import(AbstractRabbitConfiguration.class)
@Scope("prototype")
public class RabbitServerConfiguration extends AbstractRabbitConfiguration{

	@Bean 
	@Primary
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
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		rabbitTemplate.setExchange(direct_marketdata_exchange); 
	}
	
	@Bean
	public MessageConverter jsonMessageConverter(){
	    return new Jackson2JsonMessageConverter();
	}
	
	@Bean MessageConverter simpleMessageConverter() {
		return new SimpleMessageConverter();
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
        factory.setMessageConverter(simpleMessageConverter());
        return factory;
    }
    
    @Bean  
    public SimpleMessageListenerContainer messageContainer() {  
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());  
        container.setQueues(simpleMessageListenerContainerQueue());  
        container.setExposeListenerChannel(true);  
        container.setMessageConverter(simpleMessageConverter());
        container.setMaxConcurrentConsumers(1);  
        container.setConcurrentConsumers(1);  
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认  
        container.setMessageListener(new ChannelAwareMessageListener() {  
            @Override  
            public void onMessage(Message message, Channel channel) throws Exception {  
                byte[] body = message.getBody();  
                System.out.println("SimpleMessageListenerContainer listening simpleMessageListenerContainerQueue: " + new String(body));  
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费  
            }
        });  
        return container;  
    }  
    
}
