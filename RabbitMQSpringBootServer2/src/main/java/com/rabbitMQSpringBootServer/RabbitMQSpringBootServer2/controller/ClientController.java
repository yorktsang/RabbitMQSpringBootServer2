package com.rabbitMQSpringBootServer.RabbitMQSpringBootServer2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("name")
public class ClientController {
	@RequestMapping (value = "/client", method = RequestMethod.GET)
	public String index(final ModelMap model) {
		return "client";
	}
	
	@RequestMapping (value = "/client", method = RequestMethod.POST)
	public String client(final ModelMap model, @RequestParam final String topic) {
		model.put("topic", topic+"_amend");
		return "client";
	}
}
