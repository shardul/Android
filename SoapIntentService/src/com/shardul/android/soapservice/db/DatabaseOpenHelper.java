package com.shardul.android.soapservice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper{
	
	public static String DATABASE_NAME="SOAPFailureCache.db3";

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+DatabaseTableHelper.CachedRecords.TABLE_NAME+" (" +
				DatabaseTableHelper.CachedRecords._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				DatabaseTableHelper.CachedRecords.Namespace+" TEXT NOT NULL, " +
				DatabaseTableHelper.CachedRecords.Action+" TEXT NOT NULL" +
				DatabaseTableHelper.CachedRecords.MethodName+" TEXT NOT NULL, " +
				DatabaseTableHelper.CachedRecords.Url+" TEXT NOT NULL" +
				DatabaseTableHelper.CachedRecords.Headers+" TEXT, " +
				DatabaseTableHelper.CachedRecords.Properties+" TEXT" +
				DatabaseTableHelper.CachedRecords.Timestamp+" INTEGER" +
				")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS "+DatabaseTableHelper.CachedRecords.TABLE_NAME);
		onCreate(db);
	}

}
