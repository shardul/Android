package com.shardul.globalspace.android.soapservice.model;

import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SOAPRequestHelper {
	
	private String id;
	
	public static final int REQUEST_TYPE_GET=0;
	public static final int REQUEST_TYPE_POST=1;
	public static final int REQUEST_DEFAULT=REQUEST_TYPE_GET;
	
	
	private String nameSpace;
	private String soapAction;
	private String methodName;
	private String url;
	private int requestType=REQUEST_TYPE_GET;
	private SOAPNameValuePair[] headers;
	private SOAPNameValuePair[] properties;
	
	
	
	public SOAPRequestHelper(String nameSpace, String soapAction,
			String methodName, String url) {
		this.nameSpace = nameSpace;
		this.soapAction = soapAction;
		this.methodName = methodName;
		this.url = url;
		
		String validation=validate();
		
		if(validation!=null && validation.length()>1){
			throw new NullPointerException(validation);
		}
	}
	
	

	public SOAPRequestHelper(String nameSpace, String soapAction,
			String methodName, String url, SOAPNameValuePair[] properties) {
		this.nameSpace = nameSpace;
		this.soapAction = soapAction;
		this.methodName = methodName;
		this.url = url;
		this.properties = properties;
		
		String validation=validate();
		
		if(validation!=null && validation.length()>1){
			throw new NullPointerException(validation);
		}
	}



	public String getNameSpace() {
		return nameSpace;
	}
	
	public SOAPRequestHelper setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
		return this;
	}
	public String getSoapAction() {
		return soapAction;
	}
	public SOAPRequestHelper setSoapAction(String soapAction) {
		this.soapAction = soapAction;
		return this;
	}
	public String getMethodName() {
		return methodName;
	}
	public SOAPRequestHelper setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public SOAPRequestHelper setUrl(String url) {
		this.url = url;
		return this;
	}
	public SOAPNameValuePair[] getHeaders() {
		return headers;
	}
	public SOAPRequestHelper setHeaders(SOAPNameValuePair[] headers) {
		this.headers = headers;
		return this;
	}
	public SOAPNameValuePair[] getProperties() {
		return properties;
	}
	public SOAPRequestHelper setProperties(SOAPNameValuePair[] properties) {
		this.properties = properties;
		return this;
	}
	
	public SOAPRequestHelper setRequestType(int requestType) {
		switch (requestType) {
		case REQUEST_TYPE_GET:
		case REQUEST_TYPE_POST:
			this.requestType=REQUEST_TYPE_POST;
			break;
		default:
			this.requestType=REQUEST_TYPE_GET;
		}
		return this;
	}
	public int getRequestType() {
		return requestType;
	}
	
	private String validate(){
		String msg="";
		if(nameSpace==null)
			msg+="namespace";
		if(url==null)
			msg+=" url";
		if(soapAction==null)
			msg+="soapAction";
		if(methodName==null)
			msg+="methodName";
		if(msg.length()>1)
			msg+=" cannot be NULL";
		return msg;
	}
	
	public void performSOAPRequest(Context context){
		if(context==null)
			return;
		
		Intent serviceIntent=getCallIntent(this);
		context.startService(serviceIntent);
	}
	
	public void performSOAPRequest(Context context,BroadcastReceiver responseReceiver){
		performSOAPRequest(context);
		
		if(responseReceiver!=null){
			IntentFilter intentFilter=new IntentFilter();
			intentFilter.addAction(SOAPServiceConstants.ACTION_SOAP_RESPONSE+getID());
			intentFilter.addAction(SOAPServiceConstants.ACTION_SOAP_EXCEPTION+getID());
			
			context.registerReceiver(responseReceiver, intentFilter);
		}
	}
	
	public static Intent getCallIntent(SOAPRequestHelper soapRequestHelper){
		if(soapRequestHelper==null)return null;
		
		Intent serviceIntent = new Intent();
		serviceIntent.setComponent(new ComponentName(SOAPServiceConstants.SERVICE_PACAKGE, SOAPServiceConstants.SERVICE_COMPONENT));
		
		serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_ID, soapRequestHelper.getID());
		serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_NAMESPACE, soapRequestHelper.getNameSpace());
		serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_METHOD_NAME, soapRequestHelper.getMethodName());
		serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_SOAP_ACTION, soapRequestHelper.getSoapAction());
		serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_SOAP_URL, soapRequestHelper.getUrl());
		serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_REQUEST_TYPE, soapRequestHelper.getRequestType());
		
		if(soapRequestHelper.getProperties()!=null)
		
			serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_PROPERTIES, soapRequestHelper.getProperties());
		
		if(soapRequestHelper.getHeaders()!=null)
			
			serviceIntent.putExtra(SOAPServiceConstants.RequestProperties.EXTRA_HEADERS, soapRequestHelper.getHeaders());
		
		return serviceIntent;
	}
	
	private String getID(){
		if(id==null){
			String uuid=UUID.randomUUID().toString();
			id=uuid.substring(0,uuid.indexOf("-"));
		}
		return id;
	}
	
	
	

}
