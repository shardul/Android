package com.shardul.android.soapservice.db;

import com.shardul.android.soapservice.SoapIntentService;
import com.shardul.globalspace.android.soapservice.model.SOAPNameValuePair;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CachedRecord {
	
	private static final String TAG = "CachedRecord";
	public static String RECORD_SERPERATOR=";";
	public static String PROPERTY_SEPERATOR=",";
	
	private long id=0;
	private String namespace=null;
	private String action=null;
	private String methodname=null;
	private String url=null;
	private String headers=null;
	private String properties=null;
	private long timestamp=0;
	
	
	public CachedRecord(String namespace, String action, String methodname,
			String url) {
		this.namespace = checkNullOrThrow("namespace",namespace);
		this.action = checkNullOrThrow("action",action);
		this.methodname = checkNullOrThrow("methodname",methodname);
		this.url = checkNullOrThrow("url",url);
		this.timestamp=System.currentTimeMillis();
	}
	
	private String checkNullOrThrow(String propertyName,String propertyValue){
		if(propertyValue==null){
			throw new NullPointerException(propertyName+"  cannot be null");
		}else
			return propertyValue;
	}

	public String getNamespace() {
		return namespace;
	}

	public CachedRecord setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getAction() {
		return action;
	}

	public CachedRecord setAction(String action) {
		this.action = action;
		return this;
	}

	public String getMethodname() {
		return methodname;
	}

	public CachedRecord setMethodname(String methodname) {
		this.methodname = methodname;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public CachedRecord setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getHeaders() {
		return headers;
	}
	
	public void addHeader(String name,String value){
		if(headers==null)
			headers="";
		headers+=name+PROPERTY_SEPERATOR+value+RECORD_SERPERATOR;
	}
	
	public void addProperty(String name,String value){
		if(properties==null)
			properties="";
		properties+=name+PROPERTY_SEPERATOR+value+RECORD_SERPERATOR;
	}
	
	public SOAPNameValuePair[] getHeadersAsNameValuePair(){
		if(getHeaders()!=null){
			final int headerLength=getHeaders().length();
			SOAPNameValuePair[] result=new SOAPNameValuePair[headerLength];
			
			String [] hedersRecordStr=getHeaders().split(CachedRecord.RECORD_SERPERATOR);
			
			for(int i=0;i<headerLength;i++){
				String[] nameValue=hedersRecordStr[i].split(CachedRecord.PROPERTY_SEPERATOR);
				if(nameValue.length>=2){
					result[i]=new SOAPNameValuePair(nameValue[0], nameValue[1]);
				}else
					Log.w(TAG,"header length mismatched : "+hedersRecordStr[i]);
			}
			
			return result;
		}
		else return null;
	}
	
	public void addHeadersFromNameValuePair(SOAPNameValuePair[] headersNameValuePair){
		if(headersNameValuePair!=null){
			for(SOAPNameValuePair soapNameValuePair:headersNameValuePair){
				addHeader(soapNameValuePair.getName(), soapNameValuePair.getValue());
			}
		}
	}
	
	public void addPropertiesFromNameValuePair(SOAPNameValuePair[] headersNameValuePair){
		if(headersNameValuePair!=null){
			for(SOAPNameValuePair soapNameValuePair:headersNameValuePair){
				addProperty(soapNameValuePair.getName(), soapNameValuePair.getValue());
			}
		}
	}
	
	public SOAPNameValuePair[] getPropertiesAsNameValuePair(){
		if(getProperties()!=null){
			final int propLength=getProperties().length();
			SOAPNameValuePair[] result=new SOAPNameValuePair[propLength];
			
			String [] propsRecordStr=getProperties().split(CachedRecord.RECORD_SERPERATOR);
			
			for(int i=0;i<propLength;i++){
				String[] nameValue=propsRecordStr[i].split(CachedRecord.PROPERTY_SEPERATOR);
				if(nameValue.length>=2){
					result[i]=new SOAPNameValuePair(nameValue[0], nameValue[1]);
				}else
					Log.w(TAG,"property length mismatched : "+propsRecordStr[i]);
			}
			
			return result;
		}
		else return null;
	}

	public CachedRecord setHeaders(String headers) {
		this.headers = headers;
		return this;
	}

	public String getProperties() {
		return properties;
	}

	public CachedRecord setProperties(String properties) {
		this.properties = properties;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public CachedRecord setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	@Override
	public String toString() {
		
		return "namespace"+namespace+", "+
		"action"+action+", "+
		"methodname"+methodname+", "+
		"url"+url+", "+
		"headers"+headers+", "+
		"properties"+properties+", "+
		"timestamp"+timestamp+", "
		;
	}
	
	public long insert(SoapIntentService application) {
		SQLiteDatabase db=application.getCacheRecordFailureDb();
		return insertToDB(db);
	}
	
	public long insertToDB(SQLiteDatabase db) {
		ContentValues cv=new ContentValues();
		cv.put(DatabaseTableHelper.CachedRecords.Namespace, namespace);
		cv.put(DatabaseTableHelper.CachedRecords.Action, action);
		cv.put(DatabaseTableHelper.CachedRecords.Url, url);
		cv.put(DatabaseTableHelper.CachedRecords.MethodName, methodname);
		if(headers!=null)
			cv.put(DatabaseTableHelper.CachedRecords.Headers, headers);
		else
			 cv.putNull(DatabaseTableHelper.CachedRecords.Headers);
		
		if(headers!=null)
			cv.put(DatabaseTableHelper.CachedRecords.Properties, properties);
		else
			 cv.putNull(DatabaseTableHelper.CachedRecords.Properties);
		
		id=db.insert(DatabaseTableHelper.CachedRecords.TABLE_NAME, DatabaseTableHelper.CachedRecords.Headers, cv);
		
		return id;
	}
	
	public int delete(SoapIntentService application){
		SQLiteDatabase db=application.getCacheRecordFailureDb();
		return deleteFromDb(db);
		
	}
	
	public int deleteFromDb(SQLiteDatabase db){
		return db.delete(DatabaseTableHelper.CachedRecords.TABLE_NAME, DatabaseTableHelper.CachedRecords._ID+"= "+id, null);
		
	}
	
	
}
