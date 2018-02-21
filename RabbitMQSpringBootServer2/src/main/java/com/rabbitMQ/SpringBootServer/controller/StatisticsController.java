package com.rabbitMQ.SpringBootServer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {
	@RequestMapping("/showMaxHeap")
	public String showMaxHeap() {
		long heapSize = (long) (Runtime.getRuntime().totalMemory()/1024./1024.);
		long heapMaxSize = (long) (Runtime.getRuntime().maxMemory()/1024./1024.);
		long heapFreeSize = (long) (Runtime.getRuntime().freeMemory()/1024./1024.);
		
		String result = "<html>heap: "+ String.valueOf(heapSize) +"MB<br/>"
						+"heapMaxSize: " + String.valueOf(heapMaxSize) +"MB<br/>"
						+"heapFreeSize: " + String.valueOf(heapFreeSize) +"MB<br/></html>";
		
		return result;
	}
}