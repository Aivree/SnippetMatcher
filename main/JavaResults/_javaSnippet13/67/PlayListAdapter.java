package fr.louisbl.helloechonest.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.echonest.api.v4.Song;

/**
 * Created by louis on 10/13/14.
 */
public class PlayListAdapter extends ArrayAdapter<Song> {
    private final LayoutInflater mInflater;

    public PlayListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        } else {
            view = convertView;
        }

        Song item = getItem(position);
        ((TextView) view.findViewById(android.R.id.text1)).setText(item.getTitle());
        ((TextView) view.findViewById(android.R.id.text2)).setText(item.getArtistName());

        return view;
    }
}
