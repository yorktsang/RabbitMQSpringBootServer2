package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.service.ServerBackgroundService;

@Controller
public class ServerController {
	@Autowired
	ServerBackgroundService serverBackground;
	
	@RequestMapping(value ="/server", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		String currentTime = String.valueOf(serverBackground.getCounter());
		model.put("currentTime", currentTime);
		return "server";
	}
}
