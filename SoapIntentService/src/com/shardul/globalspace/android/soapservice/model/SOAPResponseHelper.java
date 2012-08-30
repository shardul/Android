package com.shardul.globalspace.android.soapservice.model;

import java.util.HashMap;

import android.content.Intent;

public class SOAPResponseHelper {
	
	private boolean isException;
	private int errorCode;
	private Exception exception;
	private HashMap<String, Object> responseMap;
	private HashMap<String, String> headers=new HashMap<String, String>();
	
	public SOAPResponseHelper(Intent intent){
		responseMap=(HashMap<String, Object>) intent.getSerializableExtra(SOAPServiceConstants.ResponseProperties.EXTRA_RESPONSE);
		exception=(Exception) intent.getSerializableExtra(SOAPServiceConstants.ResponseProperties.EXTRA_EXCEPTION);
		
		isException=(responseMap==null&&exception!=null);
		
		if(isException){
			errorCode=intent.getIntExtra(SOAPServiceConstants.ResponseProperties.EXTRA_EXCEPTION_CODE, SOAPServiceConstants.ERROR_CODES.UNKNOWN_EXCEPTION);
		}
		
		Object[] headersResponse=(Object[]) intent.getSerializableExtra(SOAPServiceConstants.ResponseProperties.EXTRA_RESPONSE_HEADER);
		
		if(headersResponse!=null){
			for(int i=0;i<headersResponse.length;i++){
				SOAPNameValuePair soapNameValuePair=(SOAPNameValuePair) headersResponse[i];
				headers.put(soapNameValuePair.getName(),soapNameValuePair.getValue());
			}
		}
	}

	public boolean isException() {
		return isException;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Exception getException() {
		return exception;
	}

	public HashMap<String, Object> getResponseMap() {
		return responseMap;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	

}
