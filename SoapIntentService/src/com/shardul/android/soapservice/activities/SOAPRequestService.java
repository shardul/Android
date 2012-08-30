package com.shardul.android.soapservice.activities;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.shardul.android.soapservice.SoapIntentService;
import com.shardul.android.soapservice.db.CachedRecord;
import com.shardul.globalspace.android.soapservice.model.IntentParamExpectedException;
import com.shardul.globalspace.android.soapservice.model.SOAPNameValuePair;
import com.shardul.globalspace.android.soapservice.model.SOAPRequestHelper;
import com.shardul.globalspace.android.soapservice.model.SOAPServiceConstants;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SOAPRequestService extends IntentService {

	private final String TAG = getClass().getSimpleName();

	public boolean isWaitingForAuthentication = false;

	private boolean doCacheOnNetworkFailure = false;

	public SOAPRequestService() {
		super("SoapRequestService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			Log.e(TAG, "requested with NULL intent " + new Date().toString());
			return;
		}

		String id = null;
		String namespace = null;
		String url = null;
		String methodName = null;
		String action = null;
		SOAPNameValuePair[] headers = null;
		SOAPNameValuePair[] properties = null;
		try {

			id = intent
					.getStringExtra(SOAPServiceConstants.RequestProperties.EXTRA_ID);

			isNull(id, SOAPServiceConstants.RequestProperties.EXTRA_ID);

			namespace = intent
					.getStringExtra(SOAPServiceConstants.RequestProperties.EXTRA_NAMESPACE);

			isNull(namespace,
					SOAPServiceConstants.RequestProperties.EXTRA_NAMESPACE);

			url = intent
					.getStringExtra(SOAPServiceConstants.RequestProperties.EXTRA_SOAP_URL);

			isNull(url, SOAPServiceConstants.RequestProperties.EXTRA_SOAP_URL);

			methodName = intent
					.getStringExtra(SOAPServiceConstants.RequestProperties.EXTRA_METHOD_NAME);

			isNull(methodName,
					SOAPServiceConstants.RequestProperties.EXTRA_METHOD_NAME);

			action = intent
					.getStringExtra(SOAPServiceConstants.RequestProperties.EXTRA_SOAP_ACTION);

			isNull(action,
					SOAPServiceConstants.RequestProperties.EXTRA_SOAP_ACTION);

			doCacheOnNetworkFailure = intent.getIntExtra(
					SOAPServiceConstants.RequestProperties.EXTRA_REQUEST_TYPE,
					SOAPRequestHelper.REQUEST_DEFAULT) == SOAPRequestHelper.REQUEST_TYPE_POST;

			Object[] headerobject = (Object[]) intent
					.getSerializableExtra(SOAPServiceConstants.RequestProperties.EXTRA_HEADERS);
			;

			if (headerobject != null) {
				headers = new SOAPNameValuePair[headerobject.length];
				for (int i = 0; i < headerobject.length; i++) {
					headers[i] = (SOAPNameValuePair) headerobject[i];
				}
			}

			Object[] propertiesObject = (Object[]) intent
					.getSerializableExtra(SOAPServiceConstants.RequestProperties.EXTRA_PROPERTIES);

			if (propertiesObject != null) {
				properties = new SOAPNameValuePair[propertiesObject.length];
				for (int i = 0; i < propertiesObject.length; i++) {
					properties[i] = (SOAPNameValuePair) propertiesObject[i];
				}
			}

			SoapObject request = new SoapObject(namespace, methodName);

			if (properties != null) {

				for (SOAPNameValuePair nameValuePair : properties) {

					request.addProperty(nameValuePair.getName(),
							nameValuePair.getValue());
				}
			}

			List<HeaderProperty> reqheaders = new ArrayList<HeaderProperty>();

			if (headers != null) {
				for (SOAPNameValuePair nameValuePair : headers) {
					reqheaders.add(new HeaderProperty(nameValuePair.getName(),
							nameValuePair.getValue()));
				}
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
			androidHttpTransport.debug = true;

			List<HeaderProperty> responseHeaders = null;
			if (reqheaders.size() > 1)
				responseHeaders = androidHttpTransport.call(action, envelope,
						reqheaders);
			else
				responseHeaders = androidHttpTransport.call(action, envelope,
						null);

			String responseStr = envelope.getResponse().toString();

			SoapObject response = (SoapObject) envelope.getResponse();

			int count = response.getPropertyCount();

			HashMap<String, Object> responseMap = new HashMap<String, Object>();

			PropertyInfo pi = new PropertyInfo();

			for (int i = 0; i < count; i++) {
				response.getPropertyInfo(i, pi);
				responseMap.put(pi.name, response.getProperty(i));
			}

			SOAPNameValuePair[] responseHeadersNameValuePair = null;

			if (responseHeaders != null) {
				responseHeadersNameValuePair = new SOAPNameValuePair[responseHeaders
						.size()];

				for (int i = 0; i < responseHeaders.size(); i++) {
					HeaderProperty current = responseHeaders.get(i);

					responseHeadersNameValuePair[i] = new SOAPNameValuePair(
							current.getKey(), current.getValue());
				}
			}

			Intent responseIntent = new Intent();
			responseIntent.setAction(SOAPServiceConstants.ACTION_SOAP_RESPONSE
					+ id);
			responseIntent.putExtra(
					SOAPServiceConstants.ResponseProperties.EXTRA_RESPONSE,
					(Serializable) responseMap);

			if (responseHeadersNameValuePair != null)
				responseIntent
						.putExtra(
								SOAPServiceConstants.ResponseProperties.EXTRA_RESPONSE_HEADER,
								(Serializable) responseHeadersNameValuePair);

			sendBroadcast(responseIntent);

		} catch (IntentParamExpectedException e) {
			sendBroadcast(getExceptionIntent(id,
					SOAPServiceConstants.ERROR_CODES.EXPECTED_PARAMETER, e));
		} catch (NullPointerException e) {
			sendBroadcast(getExceptionIntent(id,
					SOAPServiceConstants.ERROR_CODES.NULL_POINTER, e));
		} catch (IOException e) {
			if (doCacheOnNetworkFailure) {
				CachedRecord cachedRecord = new CachedRecord(namespace, action,
						methodName, url);
				cachedRecord.addPropertiesFromNameValuePair(headers);
				cachedRecord.addPropertiesFromNameValuePair(properties);
				cachedRecord.insert((SoapIntentService) getApplication());
				sendBroadcast(getExceptionIntent(
						id,
						SOAPServiceConstants.ERROR_CODES.COMMUNICATION_EXCEPTION,
						new IOException(cachedRecord.toString() + "cached ")));
			}
			sendBroadcast(getExceptionIntent(id,
					SOAPServiceConstants.ERROR_CODES.COMMUNICATION_EXCEPTION, e));
		} catch (XmlPullParserException e) {
			sendBroadcast(getExceptionIntent(id,
					SOAPServiceConstants.ERROR_CODES.BAD_RESPONSE, e));
		} catch (Exception e) {
			sendBroadcast(getExceptionIntent(id,
					SOAPServiceConstants.ERROR_CODES.UNKNOWN_EXCEPTION, e));
		}

	}

	private boolean isNull(Object object, String extra) {
		if (object == null) {
			Log.e(TAG, extra + " with null value");
			throw new IntentParamExpectedException(extra + " with expected");
		} else
			return false;
	}

	private Intent getExceptionIntent(String id, int errorCode, Exception e) {
		Intent result = new Intent();
		result.setAction(SOAPServiceConstants.ACTION_SOAP_EXCEPTION + id);
		result.putExtra(
				SOAPServiceConstants.ResponseProperties.EXTRA_EXCEPTION, e);
		result.putExtra(
				SOAPServiceConstants.ResponseProperties.EXTRA_EXCEPTION_CODE,
				errorCode);
		return result;
	}

}
