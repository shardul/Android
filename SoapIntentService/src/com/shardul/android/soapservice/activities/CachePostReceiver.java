package com.shardul.android.soapservice.activities;

import com.shardul.android.soapservice.db.CachedRecord;
import com.shardul.android.soapservice.db.DatabaseOpenHelper;
import com.shardul.android.soapservice.db.DatabaseTableHelper;
import com.shardul.globalspace.android.soapservice.model.SOAPRequestHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CachePostReceiver extends BroadcastReceiver {

	private static final String TAG = "CachePostReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo info = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		boolean hasConnectivity = (info != null && info.isConnected()) ? true
				: false;
		if (hasConnectivity) {
			try{
				SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();
				synchronized (db) {
					
					Cursor cursor=db.query(DatabaseTableHelper.CachedRecords.TABLE_NAME, null, null, null, null, null, null);
					
					if(cursor!=null&&cursor.moveToNext()){
						cursor.moveToPrevious();
						
						int nameSpaceIndex=cursor.getColumnIndex(DatabaseTableHelper.CachedRecords.Namespace);
						int actionIndex=cursor.getColumnIndex(DatabaseTableHelper.CachedRecords.Action);
						int urlIndex=cursor.getColumnIndex(DatabaseTableHelper.CachedRecords.Url);
						int methodNameIndex=cursor.getColumnIndex(DatabaseTableHelper.CachedRecords.MethodName);
						int headersIndex=cursor.getColumnIndex(DatabaseTableHelper.CachedRecords.Headers);
						int propertiesIndex=cursor.getColumnIndex(DatabaseTableHelper.CachedRecords.Properties);
						
						while(cursor.moveToNext()){
							CachedRecord cachedRecord=new CachedRecord(
									cursor.getString(nameSpaceIndex), 
									cursor.getString(actionIndex), 
									cursor.getString(methodNameIndex), 
									cursor.getString(urlIndex));
							
							if(cursor.isNull(headersIndex)){
								cachedRecord.setHeaders(cursor.getString(headersIndex));
							}
							
							if(cursor.isNull(propertiesIndex)){
								cachedRecord.setHeaders(cursor.getString(propertiesIndex));
							}
							
							SOAPRequestHelper soapRequestHelper=new SOAPRequestHelper(cachedRecord.getNamespace(), 
									cachedRecord.getAction(), cachedRecord.getMethodname(), cachedRecord.getUrl());
							soapRequestHelper.setRequestType(SOAPRequestHelper.REQUEST_TYPE_POST);
							
							soapRequestHelper.setProperties(cachedRecord.getPropertiesAsNameValuePair());
							soapRequestHelper.setHeaders(cachedRecord.getHeadersAsNameValuePair());
							
							soapRequestHelper.performSOAPRequest(context);
							
							cachedRecord.deleteFromDb(db);
						}
						
					}
					
					if(cursor!=null)
						cursor.close();
					
				}
			}catch(Exception ex){
				Log.e(TAG,"exception occured",ex);
			}
		}
	}
}
