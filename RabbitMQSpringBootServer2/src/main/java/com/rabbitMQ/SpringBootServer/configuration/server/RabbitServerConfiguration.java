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
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
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
import com.rabbitMQ.SpringBootServer.handler.OptionHandler;
import com.rabbitmq.client.Channel;

@Configuration
@Import(AbstractRabbitConfiguration.class)
@Scope("prototype")
public class RabbitServerConfiguration extends AbstractRabbitConfiguration{

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		//template.setMessageConverter(jsonMessageConverter());
		configureRabbitTemplate(template);
		return template;
	}


	@Override
	protected void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		//rabbitTemplate.setMessageConverter(jsonMessageConverter());
	}
	
	@Bean
	public MessageConverter jsonMessageConverter(){
	    return new Jackson2JsonMessageConverter();
	}
	
	@Bean MessageConverter simpleMessageConverter() {
		return new SimpleMessageConverter();
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
    	//this bean will start listening the queue after springbootapplication starts
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());  
        container.setQueues(optionOrderQueue());  
        container.setExposeListenerChannel(true);  
        container.setMessageConverter(simpleMessageConverter());
        container.setMaxConcurrentConsumers(1);  
        container.setConcurrentConsumers(1);  
        container.setAcknowledgeMode(AcknowledgeMode.AUTO); //设置确认模式手工确认  
        container.setMessageListener(new MessageListenerAdapter(new OptionHandler()));  
        return container;  
    }  
    
    /*
     * new ChannelAwareMessageListener() {  
            @Override  
            public void onMessage(Message message, Channel channel) throws Exception {  
                byte[] body = message.getBody();  
                System.out.println("SimpleMessageListenerContainer listening optionOrderQueue: " + new String(body));  
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费  
            }
        }
     */
    
}
