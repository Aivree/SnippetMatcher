package pringle.robin.xero;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author robinp
 */
public class ContactArrayAdapter extends ArrayAdapter<Contact> {

    private Activity context;

    public ContactArrayAdapter(Activity context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(inflater);
            view = layoutInflater.inflate(R.layout.contact_data_row, null);
        }

        Contact contact = getItem(position);
        String name = contact.getName();

        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        nameTextView.setText(name);

        return view;
    }
}
