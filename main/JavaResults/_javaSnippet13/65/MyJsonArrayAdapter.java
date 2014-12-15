package example.flockers;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MyJsonArrayAdapter extends ArrayAdapter<JSONObject>{
    private final Context context;
    public MyJsonArrayAdapter(Context context, ArrayList<JSONObject> items) {        
        super(context, R.layout.rowlayout, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);

        JSONObject obj;
        try {
            obj = getItem(position);
            textView.setText(obj.get("ename").toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rowView;
    }
}
