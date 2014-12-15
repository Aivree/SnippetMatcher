package sam.com.beaconsconfigapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sam.com.beaconsconfigapp.R;
import sam.com.beaconsconfigapp.Utils;
import sam.com.beaconsconfigapp.storage.entities.BeaconEntity;

/**
 *
 */
public class BeaconsListAdapter extends ArrayAdapter<BeaconEntity> {
    public BeaconsListAdapter(Context context, List<BeaconEntity> objects) {
        super(context, R.layout.beacon_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.beacon_list_item, parent, false);
        }
        BeaconEntity beacon = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.beacon_list_item_name_textview);
        TextView idTextView = (TextView) convertView.findViewById(R.id.beacon_list_item_id_textview);
        TextView majorTextView = (TextView) convertView.findViewById(R.id.beacon_list_item_major_textview);
        TextView minorTextView = (TextView) convertView.findViewById(R.id.beacon_list_item_minor_textview);

        String uuid = beacon.getUuid();
        String major = beacon.getMajor();
        String minor = beacon.getMinor();

        nameTextView.setText(beacon.getName());
        idTextView.setText(uuid);
        majorTextView.setText(major);
        minorTextView.setText(minor);

        return convertView;
    }

}
