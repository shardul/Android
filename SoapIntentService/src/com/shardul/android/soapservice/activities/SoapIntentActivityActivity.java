package com.shardul.android.soapservice.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

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

	private final static String SOAP_FEED = "soap_feed.xml";

	private static final int DIALOG_RESPONSE = 9961;
	private static final int HEADERS = 99980;
	private static final int PROPERTIES = 98980;

	private int headerCount = 0;
	private int propertiesCount = 0;

	private EditText nameSpace;
	private EditText action;
	private EditText url;
	private EditText method;
	private ViewGroup properties;
	private ViewGroup headers;
	private ResponseReceiver responseReceiver;
	private boolean isRegistered;

	private HashMap<String, Object> responseMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		nameSpace = (EditText) findViewById(R.id.et_namespace);
		action = (EditText) findViewById(R.id.et_action);
		url = (EditText) findViewById(R.id.et_url);
		method = (EditText) findViewById(R.id.et_method);
		properties = (ViewGroup) findViewById(R.id.proerties);
		headers = (ViewGroup) findViewById(R.id.headers);

		responseReceiver = new ResponseReceiver();

		File file = new File(Environment.getExternalStorageDirectory()
				.toString() + File.separator + SOAP_FEED);

		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file)));

				String line = "";
				StringBuffer feedBuffer = new StringBuffer();

				while ((line = reader.readLine()) != null) {
					feedBuffer.append(line);
				}

				addProperties(null);

				EditText view = (EditText) (properties.findViewById(PROPERTIES))
						.findViewById(R.id.value);
				view.setText(feedBuffer.toString());

				Toast.makeText(this, SOAP_FEED + " feeded!", Toast.LENGTH_LONG)
						.show();

			} catch (Exception e) {
				e.printStackTrace();
			}
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

		headers.addView(nameValueView);

		headerCount++;
	}

	public void addProperties(View view) {

		View nameValueView = getLayoutInflater().inflate(R.layout.namevalue,
				null);
		nameValueView.setId(PROPERTIES + propertiesCount);

		properties.addView(nameValueView);

		propertiesCount++;
	}

	public void doIt(View view) {
		isRegistered = true;

		SOAPRequestHelper soapRequestHelper = new SOAPRequestHelper(nameSpace
				.getText().toString().trim(), action.getText().toString()
				.trim(), method.getText().toString().trim(), url.getText()
				.toString().trim());

		if (propertiesCount > 0) {

			SOAPNameValuePair[] props = new SOAPNameValuePair[propertiesCount];

			for (int i = 0; i < propertiesCount; i++) {

				View nameValueView = properties.findViewById(PROPERTIES + i);

				EditText name = (EditText) nameValueView
						.findViewById(R.id.name);
				EditText value = (EditText) nameValueView
						.findViewById(R.id.value);

				props[i] = new SOAPNameValuePair(name.getText().toString()
						.trim(), value.getText().toString().trim());

			}
			soapRequestHelper.setProperties(props);
		}

		if (headerCount > 0) {

			SOAPNameValuePair[] heads = new SOAPNameValuePair[headerCount];

			for (int i = 0; i < headerCount; i++) {

				View nameValueView = headers.findViewById(PROPERTIES + i);

				EditText name = (EditText) nameValueView
						.findViewById(R.id.name);
				EditText value = (EditText) nameValueView
						.findViewById(R.id.value);

				heads[i] = new SOAPNameValuePair(name.getText().toString()
						.trim(), value.getText().toString().trim());

			}
			soapRequestHelper.setHeaders(heads);
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

}