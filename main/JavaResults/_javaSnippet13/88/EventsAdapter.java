package eu.tamere.jeudidulibre;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class EventsAdapter extends ArrayAdapter<Event> {

    private LayoutInflater inflater;

    public EventsAdapter(Context context) {
        super(context, R.layout.list_item_event);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_event, null);
        }

        Event event = getItem(position);

        TextView eventTitle = (TextView) convertView.findViewById(R.id.event_title);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.event_image);

        Picasso.with(getContext())
                .load(event.pictureUrl)
                .into(imageView);

        eventTitle.setText(event.title);

        return convertView;
    }
}
