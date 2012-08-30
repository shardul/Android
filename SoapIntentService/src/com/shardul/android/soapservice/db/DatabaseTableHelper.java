package com.shardul.android.soapservice.db;

import android.provider.BaseColumns;

public interface DatabaseTableHelper {
	
	public interface CachedRecords extends BaseColumns{
		String TABLE_NAME="CachedRecords";
		String Namespace="namespace";
		String Action="action";
		String MethodName="methodname";
		String Url="url";
		String Headers="headers";
		String Properties="properties";
		String Timestamp="time";
	}

}
