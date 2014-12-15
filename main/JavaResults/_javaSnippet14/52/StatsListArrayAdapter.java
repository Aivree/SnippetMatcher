/**
 * 
 */
package de.fmieting.dominusarce.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.fmieting.dominusarce.R;

/**
 * @author fmieting
 * 
 */
public class StatsListArrayAdapter extends ArrayAdapter<StatsListItem> {

	Context context;
	int layoutResourceId;
	StatsListItem data[] = null;

	public StatsListArrayAdapter(Context context, int layoutResourceId, StatsListItem[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View currentItem = convertView;
		StatsListItemHolder holder = null;
		LayoutInflater inflater = null;
		StatsListItem statsListItem = data[position];

		// Get current item
		if (currentItem == null) {
			inflater = ((Activity) context).getLayoutInflater();
			currentItem = inflater.inflate(layoutResourceId, parent, false);

			holder = new StatsListItemHolder();
			holder.title = (TextView) currentItem.findViewById(R.id.statstitle);
			holder.content = (LinearLayout) currentItem.findViewById(R.id.statscontent);

			if (statsListItem.items != null) {
				for (Pair<String, String> p : statsListItem.items) {
					View cur = inflater.inflate(R.layout.statslistitementry_layout, null);
					((TextView) cur.findViewById(R.id.statsentrykey)).setText(p.a);
					((TextView) cur.findViewById(R.id.statsentryvalue)).setText(p.b);
					holder.content.addView(cur);
				}
			}

			currentItem.setTag(holder);
		} else {
			holder = (StatsListItemHolder) currentItem.getTag();
		}

		// Fill item with data

		holder.title.setText(statsListItem.title);
		if (statsListItem.icon != StatsListItem.NO_ICON)
			holder.title.setCompoundDrawablesWithIntrinsicBounds(statsListItem.icon, 0, 0, 0);

		return currentItem;
	}

	static class StatsListItemHolder {
		TextView title;
		LinearLayout content;
		ImageView icon;

	}
}
