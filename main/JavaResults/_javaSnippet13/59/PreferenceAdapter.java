/**
 * 
 */
package org.adaikiss.xun.android.loadermanagerillustration.preferencesloader;

import java.util.List;

import org.adaikiss.xun.android.loadermanagerillustration.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author HuLingwei
 * 
 */
public class PreferenceAdapter extends ArrayAdapter<Preference> {

	private LayoutInflater inflater;

	public PreferenceAdapter(Context context) {
		super(context, R.layout.list_preference);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_preference, parent, false);
		} else {
			view = convertView;
		}
		Preference p = getItem(position);
		((TextView) view.findViewById(R.id.key)).setText(p.getKey());
		((TextView) view.findViewById(R.id.value)).setText(String.valueOf(p
				.getValue()));
		return view;
	}

	public void setData(List<Preference> data) {
		clear();
		for (Preference p : data) {
			add(p);
		}
	}
}
