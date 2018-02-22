package com.rabbitMQ.SpringBootServer.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class OrderResponse {
	private String ticker;

    private long quantity;

    private double price;

    private String orderType;

    private boolean buyRequest;

    private String userName;
    
    private String id = UUID.randomUUID().toString();

	private String confirmationNumber;
	private long timestamp = new Date().getTime();
	
	private String requestId;
	
	public boolean isBuyRequest() {
		return buyRequest;
	}

	public void setBuyRequest(boolean buyRequest) {
		this.buyRequest = buyRequest;
	}
	
	
	public String getId() {
		return id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TradeResponse [requestId="
				+ requestId + ", confirmationNumber=" + confirmationNumber
				+ ", orderType=" + orderType + ", price=" + price
				+ ", quantity=" + quantity + ", ticker=" + ticker + "]";
	}
}
