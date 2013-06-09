package com.shardul.databaseupgrader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new DatabaseOpenHelper(this, 2).getWritableDatabase();

		InputStream localDB = null;
		try {
			localDB = new FileInputStream(new File("data/data/"
					+ getPackageName() + "/databases/"
					+ DatabaseOpenHelper.DATABASE_NAME));
			OutputStream dbOut = new FileOutputStream(
					DatabaseOpenHelper.CACHE_DEBUG_DIRECTORY + File.separator
							+ DatabaseOpenHelper.DATABASE_NAME);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = localDB.read(buffer)) > 0) {
				dbOut.write(buffer, 0, length);
			}

			dbOut.flush();
			dbOut.close();
			localDB.close();

		} catch (IOException e) {
		}

	}

}
