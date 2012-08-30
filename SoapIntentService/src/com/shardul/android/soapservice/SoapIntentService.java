package com.shardul.android.soapservice;

import com.shardul.android.soapservice.db.DatabaseOpenHelper;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class SoapIntentService extends Application{
	
	private SQLiteDatabase db=null;
	
	public SQLiteDatabase getCacheRecordFailureDb(){
		if(db==null)
			db=new DatabaseOpenHelper(getApplicationContext()).getWritableDatabase();
		return db;
		
	}

}
