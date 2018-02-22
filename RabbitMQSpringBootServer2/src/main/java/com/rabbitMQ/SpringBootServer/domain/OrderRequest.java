package com.rabbitMQ.SpringBootServer.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderRequest {

	private String ticker;

    private long quantity;

    private double price;

    private String orderType;

    private boolean buyRequest;

    private String userName;
    
    private String dateTime;
    
    private String id = UUID.randomUUID().toString();
    
    public String getId() {
    	return id;
    }
    
    @Override
    public String toString() {
    	return String.format(id+ ": "+ticker+" "+quantity+" "+price+" "+ orderType +" "+buyRequest+" "+userName+" "+dateTime);
    }

    public void setDateTime(String dateTime) {
    	this.dateTime = dateTime;
    }
    
    public String getDateTime() {
    	return dateTime;
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

	public boolean isBuyRequest() {
		return buyRequest;
	}

	public void setBuyRequest(boolean buyRequest) {
		this.buyRequest = buyRequest;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
