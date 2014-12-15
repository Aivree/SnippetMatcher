/*
 * (c) Copyright LifeLine 2013
 */

package fr.lifeline.android.ui.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import fr.lifeline.android.R;
import fr.lifeline.android.model.Hospitalisation;
import fr.lifeline.android.model.Observation;
import fr.lifeline.android.model.PieceJointe;

/*
    Non utilisé
 */
public class ServiceAdapter extends ArrayAdapter<Hospitalisation> {

	public ServiceAdapter(Context context,List<Hospitalisation> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = 
					(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.service_list_entry, parent, false);
		}
		Hospitalisation hospitalisation = getItem(position);

		/*
		TextView service_name = (TextView)view.findViewById(R.id.bloc_service_name);
		service_name.setText(hospitalisation.getId());
		*/
		
		TextView int_ext = (TextView)view.findViewById(R.id.field_int_ext);
		int_ext.setText(2);
		
		TextView entry_date = (TextView)view.findViewById(R.id.field_entry_date_value);
		entry_date.setText(1);
		
		TextView exit_date = (TextView)view.findViewById(R.id.field_exit_date_value);
		exit_date.setText(3);
		
		TextView assistant = (TextView)view.findViewById(R.id.field_assistant_value);
		assistant.setText(5);
		
		return view;
	}
}
