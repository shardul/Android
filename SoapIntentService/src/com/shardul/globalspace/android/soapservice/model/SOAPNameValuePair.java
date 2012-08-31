package com.shardul.globalspace.android.soapservice.model;

import java.io.Serializable;

public class SOAPNameValuePair implements Serializable{

	private static final long serialVersionUID = -6885103474402759611L;
	
	private String name;
	private String value;
	
	
	
	public SOAPNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return getName()+"|"+getValue();
	}
	

}
