package com.shardul.android.chromestylehelppopups;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.shardul.android.chromestylehelppopups.R;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void clicked(View view) {
		ChromeHelpPopup chromeHelpPopup = new ChromeHelpPopup(MainActivity.this,"Hello!");
		chromeHelpPopup.show(view);
	}

}
