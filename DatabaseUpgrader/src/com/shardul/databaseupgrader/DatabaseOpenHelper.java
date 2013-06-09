package com.shardul.databaseupgrader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public final static String DATABASE_NAME = "DatabaseUpgrader.sqlite3";
	public final static String CACHE_DEBUG_DIRECTORY = Environment
			.getExternalStorageDirectory().toString();
	public static String CACHE_DIRECTORY;
	public static String CACHE_DIRECTORY_SECURE;

	private static final String TAG = "DatabaseOpenHelper";
	private int version = 1;
	private Context context;

	public DatabaseOpenHelper(Context context, int version) {
		super(context, DATABASE_NAME, null, version);
		this.version = Math.max(this.version, version);
		this.context = context;
		CACHE_DIRECTORY_SECURE = context.getCacheDir().toString();

		CACHE_DIRECTORY = CACHE_DEBUG_DIRECTORY;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < version; i++) {
			builder.append(",field" + i + " INTEGER ");
		}

		db.execSQL("CREATE TABLE Table1 (_id INTEGER PRIMARY KEY AUTOINCREMENT"
				+ builder.toString() + ");");

		if (this.version == 1) {
			for (int i = 0; i < 10; i++) {
				db.execSQL("INSERT INTO Table1 ('field" + (version - 1)
						+ "') VALUES (" + version + ")");
			}
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		log("Database upgrading to : " + newVersion + ", oldVersion : "
				+ oldVersion);
		final long starTimeMillis = System.currentTimeMillis();
		log("Upgrade init on : " + new Date().toString());

		log("Caching old database", true);
		if (cacheOldDB()) {
			log("Caching old completed ", false);

			log("Dropping old tables before recreating schema", true);

			ArrayList<String> oldTableNames = getTableNamesFromDB(db);

			for (String tableName : oldTableNames) {
				db.execSQL("DROP TABLE " + tableName);
				log("dropped : " + tableName, true);
			}

			log("Dropping tables completed", false);

			this.version = newVersion;
			log("Creating new schema, version : " + newVersion, true);
			onCreate(db);
			log("Schema creation completed", true);

			File oldDatabase = new File(CACHE_DIRECTORY + File.separator
					+ DatabaseOpenHelper.DATABASE_NAME + ".old");

			SQLiteDatabase oldDb = SQLiteDatabase.openDatabase(
					oldDatabase.toString(), null, SQLiteDatabase.OPEN_READONLY);

			SQLiteDatabase newDb = db;

			ArrayList<String> newTableNames = getTableNamesFromDB(newDb);

			for (String newTableName : newTableNames) {
				if (oldTableNames.contains(newTableName)) {
					Cursor oldTableDataCursor = oldDb.query(newTableName, null,
							null, null, null, null, null);

					if (oldTableDataCursor.moveToNext()) {
						oldTableDataCursor.moveToPrevious();

						Cursor columnsCursor = newDb.rawQuery(
								"PRAGMA table_info(" + newTableName + ");",
								null);

						if (columnsCursor.moveToNext()) {
							columnsCursor.moveToPrevious();

							final int columnNameIndex = columnsCursor
									.getColumnIndex("name");

							ArrayList<String> newColumnNames = new ArrayList<String>(
									columnsCursor.getCount());

							while (columnsCursor.moveToNext()) {
								newColumnNames.add(columnsCursor
										.getString(columnNameIndex));
							}

							String[] oldTableCols = oldTableDataCursor
									.getColumnNames();

							ArrayList<Integer> validIndexes = new ArrayList<Integer>();

							for (int i = 0; i < oldTableCols.length; i++) {
								String oldtableCol = oldTableCols[i];
								if (newColumnNames.contains(oldtableCol)) {
									validIndexes.add(i);
								}
							}

							if (validIndexes.size() > 0) {

								while (oldTableDataCursor.moveToNext()) {

									ContentValues recordValues = new ContentValues();
									for (int i = 0; i < validIndexes.size(); i++) {
										Integer validIndex = validIndexes
												.get(i);

										if (oldTableDataCursor
												.isNull(validIndex))
											continue;

										recordValues.put(
												oldTableCols[validIndex],
												oldTableDataCursor
														.getString(validIndex));
									}

									newDb.insert(newTableName, null,
											recordValues);

								}
							}
						}

					}

					oldTableDataCursor.close();
				}
			}

			// oldDatabase.delete();

		} else {
			log("Caching old database FAILED");
		}

		log("Total time for upgrading: "
				+ (System.currentTimeMillis() - starTimeMillis) + " ms");
		log("Upgrade finished on : " + new Date().toString());
	}

	private void log(String msg) {
		Log.d(TAG, msg);
	}

	private static long logStartTime = 0;

	private void log(String msg, boolean startedOrRunning) {

		if (startedOrRunning) {
			logStartTime = System.currentTimeMillis();
		} else {
			msg += ", " + (System.currentTimeMillis() - logStartTime) + " ms";
			logStartTime = 0;
		}

		log(msg);
	}

	private boolean cacheOldDB() {
		InputStream localDB = null;
		try {
			localDB = new FileInputStream(new File("data/data/"
					+ context.getPackageName() + "/databases/"
					+ DatabaseOpenHelper.DATABASE_NAME));
			OutputStream dbOut = new FileOutputStream(CACHE_DIRECTORY
					+ File.separator + DatabaseOpenHelper.DATABASE_NAME
					+ ".old");

			byte[] buffer = new byte[1024];
			int length;
			while ((length = localDB.read(buffer)) > 0) {
				dbOut.write(buffer, 0, length);
			}

			dbOut.flush();
			dbOut.close();
			localDB.close();
			return true;

		} catch (IOException e) {
		}

		return false;
	}

	private ArrayList<String> getTableNamesFromDB(SQLiteDatabase db) {
		ArrayList<String> result = null;

		Cursor tableNameCursor = db
				.query("sqlite_master",
						new String[] { "name" },
						"type = ? AND tbl_name != 'android_metadata' AND tbl_name!='sqlite_sequence' ",
						new String[] { "table" }, null, null, null);

		result = new ArrayList<String>(tableNameCursor.getCount());

		while (tableNameCursor.moveToNext()) {
			result.add(tableNameCursor.getString(0));
		}

		tableNameCursor.close();

		return result;

	}
}
