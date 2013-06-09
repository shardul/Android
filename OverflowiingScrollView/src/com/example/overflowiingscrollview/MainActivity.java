package com.example.overflowiingscrollview;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private OverflowListParentLayout overflowListParentLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		overflowListParentLayout = (OverflowListParentLayout) findViewById(R.id.parent_layout);

		ArrayList<String> items = new ArrayList<String>();

		for (int i = 0; i < 20; i++) {
			items.add("item " + i);
		}

		overflowListParentLayout.setOverflowList(getListView());

		getListView().setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1,
						android.R.id.text1, items){
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						view.setBackgroundResource(R.drawable.list_selector);
						return view;
					}
				});

	}

	public void buttonClicked(View view) {
		setTitle("buttonClicked");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		setTitle(getListView().getItemAtPosition(position).toString());
	}

}
