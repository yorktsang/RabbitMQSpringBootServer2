package com.rabbitMQ.SpringBootServer.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.context.JavaConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.rabbitMQ.SpringBootServer.configuration.AbstractRabbitConfiguration;
import com.rabbitMQ.SpringBootServer.configuration.client.RabbitClientConfiguration;
import com.rabbitMQ.SpringBootServer.controller.ClientNoCallbackController.CompleteMessageCorrelationData;
import com.rabbitMQ.SpringBootServer.domain.OrderRequest;
import com.rabbitMQ.SpringBootServer.domain.OrderResponse;


@Controller
@Scope("session")
public class ClientController implements RabbitTemplate.ConfirmCallback, ReturnCallback{
	
	private static Logger log = Logger.getLogger(ClientController.class);
	
	@Autowired
	private RabbitClientConfiguration rabbitConfiguration;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private MessageProperties prop;
	
	private boolean callbackConfig = true;
	
	//private final CountDownLatch listenLatch = new CountDownLatch(1);

	//private final CountDownLatch confirmLatch = new CountDownLatch(1);

	//private final CountDownLatch returnLatch = new CountDownLatch(1);
	
	@PostConstruct
	public void init() {
		rabbitTemplate.setConfirmCallback(this);
		rabbitTemplate.setReturnCallback(this);
	}
	
	@RequestMapping (value = "/client", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		//test();
		return "client";
	}
	
	@RequestMapping (value = "/client", method = RequestMethod.POST)
	public String client(final ModelMap model, @RequestParam final String topic
			, @RequestParam final String route
			, @RequestParam final String numOfMsg) throws InterruptedException {
		int count = 1;
		String errorMessage ="";

		if(numOfMsg.matches("[0-9]+")) {
			count = Integer.parseInt(numOfMsg);
		}else {
			errorMessage += "Failed to parse Number, set msg number as 1.<br/>";
		}
		
		for(int i = 0; i < count; i++) {
			String correlationId = UUID.randomUUID().toString();
			Object message = null;
			if("topicExchange_Order".equalsIgnoreCase(topic) && "option".equalsIgnoreCase(route)) {
				OrderRequest orderRequest = createOptionOrderRequest();
				message = orderRequest;
			}else {
				String sendMsg = getCurrentLocalDateTimeStamp();
				message = sendMsg;
			}
			
			MessageProperties newProp = new MessageProperties();
			Message amqpMessage = new SimpleMessageConverter().toMessage(message, newProp);
			Object receive = null;
			try {
				if(topic.isEmpty()) {
					if(route.isEmpty()) {
						receive = rabbitTemplate.convertSendAndReceive(route, amqpMessage, new CorrelationData(correlationId));
						log.info("rabbitTemplate sent message with correlationId:" +route + ">>"+ correlationId);
					}
				}else {
					if(route.isEmpty()) {
						//not feasible
					}else {
						receive = rabbitTemplate.convertSendAndReceive(topic, route, amqpMessage, new CorrelationData(correlationId));
						log.info("rabbitTemplate sent message with correlationId:" +topic+"|"+route + ">>"+ correlationId);
					}
				}
			}catch(Exception e) {
				errorMessage += "Failed to send Msg <br/>"+e.getMessage()+"<br/>";
			}
			
			if (receive instanceof OrderResponse) {
				log.info(((OrderResponse)receive).toString());
			}else if(receive instanceof String) {
				log.info((String)receive);
			}

		}
		model.put("errorMessage", errorMessage);
		return "client";
	}


	private void setupCallbacks() {
		/*
		 * Confirms/returns enabled in application.properties - add the callbacks here.
		 */
		if(!callbackConfig) {
			log.info("Set Call back");
			//ConfirmCallback to provide implementation for 
			//org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback.confirm(correlation, ack, reason)
			rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
				if (correlation != null) {
					log.info("setConfirmCallback received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
				}
				//this.confirmLatch.countDown();
			});
			rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
				log.info("setReturnCallback Returned: " + message + "\nreplyCode: " + replyCode
						+ "\nreplyText: " + replyText + "\nexchange/rk: " + exchange + "/" + routingKey);
				//this.returnLatch.countDown();
			});
			/*
			 * Replace the correlation data with one containing the converted message in case
			 * we want to resend it after a nack.
			 */
			
			//rabbitTemplate.setCorrelationDataPostProcessor((message, correlationData) ->
			//new CompleteMessageCorrelationData(correlationData != null ? correlationData.getId() : null, message));
			
			callbackConfig = true;
		}
	}
	
	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS"));
	}
	
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		log.info("Returned: " + message + "\nreplyCode: " + replyCode
				+ "\nreplyText: " + replyText + "\nexchange/rk: " + exchange + "/" + routingKey);
		
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		log.info("setConfirmCallback received " + (ack ? " ack " : " nack ") + "for correlation: " + correlationData);
	}
	
	public OrderRequest createOptionOrderRequest() {
		Random  randomGenerator = new Random();
		List<String> optionList = Arrays.asList("12345 hk","12346 hk", "12347 hk", "12348 hk");
		
		OrderRequest or = new OrderRequest();
		or.setTicker(optionList.get(randomGenerator.nextInt(optionList.size())));
		or.setQuantity(randomGenerator.nextInt(10)*100);
		or.setPrice(randomGenerator.nextDouble());
		or.setOrderType("Normal");
		or.setBuyRequest(randomGenerator.nextInt(2) == 0 ? true: false);
		or.setUserName("york");
		or.setDateTime(getCurrentLocalDateTimeStamp());
		return or;
	}
	
	public void test() {
		  final RabbitTemplate template = new RabbitTemplate(rabbitConfiguration.connectionFactory()); 
		  template.setExchange(rabbitConfiguration.testExchange().getName());
		  template.setRoutingKey("test"); 
		  template.setReplyTimeout(2000);

		  
		  final RabbitTemplate replyTemplate = new RabbitTemplate(rabbitConfiguration.connectionFactory()); 
		  replyTemplate.setQueue(rabbitConfiguration.testQueue().getName()); 
		  ExecutorService executor = Executors.newFixedThreadPool(1); 
		  // Set up a consumer to respond to our producer 
		  Future<String> received = executor.submit(new Callable<String>() { 
		 
		   public String call() throws Exception { 
		    Message message = null; 
		    for (int i = 0; i < 10; i++) { 
		     message = replyTemplate.receive(); 
			  
		     if (message != null) { 
		    	 log.error("!!!message received.");
		      break; 
		     } 
		     Thread.sleep(100L); 
		    } 
		    log.error("ReplyToAddr:"+message.getMessageProperties().getReplyTo());

		    replyTemplate.send(message.getMessageProperties().getReplyTo(), message); 
		    return (String) replyTemplate.getMessageConverter().fromMessage(message); 
		   } 
		 
		  }); 
		  
		  log.error("about to send message");
		  String result = (String) template.convertSendAndReceive("message"); 
		  try {
			log.error("Future:"+received.get());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		  log.error("result:" +result);

	}
}
