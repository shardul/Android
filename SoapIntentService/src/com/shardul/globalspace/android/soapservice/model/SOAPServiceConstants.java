package com.shardul.globalspace.android.soapservice.model;

public interface SOAPServiceConstants {
	
	String SERVICE_PERMISSION="in.globalspace.android.permission.SOAP_REQUEST";
	
	String SERVICE_PACAKGE="com.shardul.android.soapservice";
	String SERVICE_COMPONENT="com.shardul.android.soapservice.activities.SOAPRequestService";
	
	public interface RequestProperties{
		String EXTRA_ID="_id";
		String EXTRA_NAMESPACE="namespace";
		String EXTRA_METHOD_NAME="method_name";
		String EXTRA_SOAP_ACTION="soap_action";
		String EXTRA_SOAP_URL="soap_url";
		String EXTRA_HEADERS="headers";
		String EXTRA_PROPERTIES="properties";
		String EXTRA_REQUEST_TYPE="request_type";
	}
	
	public interface ResponseProperties{
		String EXTRA_RESPONSE="response";
		String EXTRA_RESPONSE_HEADER="response_header";
		String EXTRA_EXCEPTION="exception";
		String EXTRA_EXCEPTION_CODE="codes";
	}
	
	public interface ERROR_CODES{
		int AUTHENTICATION_FAILURE=401;
		int COMMUNICATION_EXCEPTION=404;
		int EXPECTED_PARAMETER=517;
		int NULL_POINTER=518;
		int BAD_RESPONSE=400;
		int UNKNOWN_EXCEPTION=600;
	}
	
	String ACTION_SOAP_RESPONSE="in.globalspace.android.soapservice.soapresp";
	String ACTION_SOAP_EXCEPTION="in.globalspace.android.soapservice.exception";
	String ACTION_UNBLOCK_AFTER_AUTHENTICATE="in.globalspace.android.soapservice.UNBLOCK_AFTER_AUTHENTICATE";

}
