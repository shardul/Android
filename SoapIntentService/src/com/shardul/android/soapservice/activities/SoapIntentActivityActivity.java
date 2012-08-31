package com.shardul.android.soapservice.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.shardul.android.soapservice.R;
import com.shardul.globalspace.android.soapservice.model.SOAPNameValuePair;
import com.shardul.globalspace.android.soapservice.model.SOAPRequestHelper;
import com.shardul.globalspace.android.soapservice.model.SOAPResponseHelper;

public class SoapIntentActivityActivity extends Activity {

	private static final String PROP_NAMESPACE = "prop_namespace";
	private static final String PROP_ACTION = "prop_action";
	private static final String PROP_URL = "prop_url";
	private static final String PROP_METHOD = "prop_method";
	private static final String PROP_SOAP_PROPERTIES = "prop_properties";
	private static final String PROP_SOAP_HEADERS = "prop_headers";

	private final static String SOAP_FEED = Environment
			.getExternalStorageDirectory().toString()
			+ File.separator
			+ "soap_feed.properties";

	private static final int DIALOG_RESPONSE = 9961;
	private static final int HEADERS = 99980;
	private static final int PROPERTIES = 98980;

	private int headerCount = 0;
	private int propertiesCount = 0;

	private EditText etNameSpace;
	private EditText etAction;
	private EditText etUrl;
	private EditText etMethod;
	private ViewGroup vgProperties;
	private ViewGroup vgHeaders;
	private ResponseReceiver responseReceiver;
	private boolean isRegistered;

	private HashMap<String, Object> responseMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		etNameSpace = (EditText) findViewById(R.id.et_namespace);
		etAction = (EditText) findViewById(R.id.et_action);
		etUrl = (EditText) findViewById(R.id.et_url);
		etMethod = (EditText) findViewById(R.id.et_method);
		vgProperties = (ViewGroup) findViewById(R.id.proerties);
		vgHeaders = (ViewGroup) findViewById(R.id.headers);

		responseReceiver = new ResponseReceiver();

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(SOAP_FEED));
			String nameSpace = (String) properties.get(PROP_NAMESPACE);
			String action = (String) properties.get(PROP_ACTION);
			String url = (String) properties.get(PROP_URL);
			String method = (String) properties.get(PROP_METHOD);

			etNameSpace.setText(nameSpace);
			etAction.setText(action);
			etMethod.setText(method);
			etUrl.setText(url);

			SOAPNameValuePair[] props = getNameValuesFromStoredString(properties
					.getProperty(PROP_SOAP_PROPERTIES));

			for (SOAPNameValuePair soapNameValuePair : props) {
				addProperties(null);
				View nameValueView = vgProperties.findViewById(PROPERTIES
						+ (propertiesCount - 1));
				EditText name = (EditText) nameValueView
						.findViewById(R.id.name);
				EditText value = (EditText) nameValueView
						.findViewById(R.id.value);
				name.setText(soapNameValuePair.getName());
				value.setText(soapNameValuePair.getValue());
			}

			SOAPNameValuePair[] heads = getNameValuesFromStoredString(properties
					.getProperty(PROP_SOAP_HEADERS));
			for (SOAPNameValuePair soapNameValuePair : heads) {
				addHeaders(null);
				View nameValueView = vgHeaders.findViewById(HEADERS
						+ (headerCount - 1));
				EditText name = (EditText) nameValueView
						.findViewById(R.id.name);
				EditText value = (EditText) nameValueView
						.findViewById(R.id.value);
				name.setText(soapNameValuePair.getName());
				value.setText(soapNameValuePair.getValue());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isRegistered)
			unregisterReceiver(responseReceiver);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_RESPONSE:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SoapIntentActivityActivity.this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setPositiveButton(android.R.string.ok, null);
			return builder.create();

		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_RESPONSE:
			((AlertDialog) dialog).setMessage(responseMap.toString());
			break;
		}
		super.onPrepareDialog(id, dialog);
	}

	public void addHeaders(View view) {

		View nameValueView = getLayoutInflater().inflate(R.layout.namevalue,
				null);
		nameValueView.setId(HEADERS + headerCount);

		vgHeaders.addView(nameValueView);

		headerCount++;
	}

	public void addProperties(View view) {

		View nameValueView = getLayoutInflater().inflate(R.layout.namevalue,
				null);
		nameValueView.setId(PROPERTIES + propertiesCount);

		vgProperties.addView(nameValueView);

		propertiesCount++;
	}

	public void doIt(View view) {
		isRegistered = true;

		final String namespace = etNameSpace.getText().toString().trim();
		final String action = etAction.getText().toString().trim();
		final String methodName = etMethod.getText().toString().trim();
		final String url = etUrl.getText().toString().trim();

		SOAPRequestHelper soapRequestHelper = new SOAPRequestHelper(namespace,
				action, methodName, url);

		Properties properties = new Properties();
		properties.setProperty(PROP_NAMESPACE, namespace);
		properties.setProperty(PROP_ACTION, action);
		properties.setProperty(PROP_METHOD, methodName);
		properties.setProperty(PROP_URL, url);

		if (propertiesCount > 0) {

			SOAPNameValuePair[] props = new SOAPNameValuePair[propertiesCount];

			for (int i = 0; i < propertiesCount; i++) {

				View nameValueView = vgProperties.findViewById(PROPERTIES + i);

				EditText name = (EditText) nameValueView
						.findViewById(R.id.name);
				EditText value = (EditText) nameValueView
						.findViewById(R.id.value);

				props[i] = new SOAPNameValuePair(name.getText().toString()
						.trim(), value.getText().toString().trim());

			}
			soapRequestHelper.setProperties(props);
			properties.setProperty(PROP_SOAP_PROPERTIES,
					getNameValueStoreString(props));

		}

		if (headerCount > 0) {

			SOAPNameValuePair[] heads = new SOAPNameValuePair[headerCount];

			for (int i = 0; i < headerCount; i++) {

				View nameValueView = vgHeaders.findViewById(HEADERS + i);

				EditText name = (EditText) nameValueView
						.findViewById(R.id.name);
				EditText value = (EditText) nameValueView
						.findViewById(R.id.value);

				heads[i] = new SOAPNameValuePair(name.getText().toString()
						.trim(), value.getText().toString().trim());

			}
			soapRequestHelper.setHeaders(heads);
			properties.setProperty(PROP_SOAP_HEADERS,
					getNameValueStoreString(heads));
		}

		try {
			properties.store(new FileOutputStream(SOAP_FEED), null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		soapRequestHelper.performSOAPRequest(this, responseReceiver);

	}

	private class ResponseReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			SOAPResponseHelper soapResponseHelper = new SOAPResponseHelper(
					intent);
			if (!soapResponseHelper.isException()) {
				responseMap = soapResponseHelper.getResponseMap();
				showDialog(DIALOG_RESPONSE);
			} else {
				Toast.makeText(SoapIntentActivityActivity.this,
						soapResponseHelper.getException().toString(),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	private String getNameValueStoreString(SOAPNameValuePair[] nameValuesPairs) {
		StringBuilder builder = new StringBuilder();

		for (SOAPNameValuePair namevaluepair : nameValuesPairs) {
			if (namevaluepair.getName().length() > 0)
				builder.append(namevaluepair.toString() + ",");
		}

		return builder.toString();

	}

	private SOAPNameValuePair[] getNameValuesFromStoredString(
			String storedString) {
		String[] nameValues = storedString.split(",");
		SOAPNameValuePair[] result = new SOAPNameValuePair[nameValues.length];

		for (int i = 0; i < result.length; i++) {
			String nameValue = nameValues[i];
			result[i] = new SOAPNameValuePair(nameValue.split(":")[0],
					nameValue.split("|")[1]);
		}

		return result;

	}

}