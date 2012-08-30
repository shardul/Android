package com.shardul.globalspace.android.soapservice.model;

public class IntentParamExpectedException extends NullPointerException{

	private static final long serialVersionUID = 5984265956398329340L;
	
	public IntentParamExpectedException(String detailedMsg){
		super(detailedMsg);
	}
}
