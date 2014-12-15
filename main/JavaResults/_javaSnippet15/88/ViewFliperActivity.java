package com.fastmanjohn.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ViewFliperActivity extends Activity {

	private static final String LIST_ITEM_STRING = "list_item_string";
	private static final String LIST_ITEM_PIC_ID = "list_item_pic_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlipContainer container = (FlipContainer) getLayoutInflater().inflate(
				R.layout.flip_container, null);

		View topView = createTopView();
		container.setTopView(topView);

		View bgView = createBackgroundView();
		container.setBacgroundView(bgView);

		setContentView(container);
	}

	private View createTopView() {
		LayoutInflater lf = getLayoutInflater();
		View topView = lf.inflate(R.layout.top_view, null);

		ListView list = (ListView) topView.findViewById(R.id.top_list);
		list.setAdapter(getCommonListAdapter(R.array.top_list_tag));

		return topView;
	}

	private View createBackgroundView() {
		LayoutInflater lf = getLayoutInflater();
		View bgView = lf.inflate(R.layout.bg_view, null);

		ListView list = (ListView) bgView.findViewById(R.id.bg_list);
		list.setAdapter(getCommonListAdapter(R.array.background_list_tag));

		return bgView;
	}

	private ListAdapter getCommonListAdapter(int arrayId) {
		String[] array = getResources().getStringArray(arrayId);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (String item : array) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put(LIST_ITEM_STRING, item);
			map.put(LIST_ITEM_PIC_ID, R.drawable.ic_launcher);

			list.add(map);
		}
		return new SimpleAdapter(getApplicationContext(), list,
				R.layout.common_list_item, new String[] { LIST_ITEM_STRING,
						LIST_ITEM_PIC_ID }, new int[] { R.id.item_string,
						R.id.item_img });
	}
}