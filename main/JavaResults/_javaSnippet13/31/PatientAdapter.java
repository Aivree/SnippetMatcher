package medata.adapter;

import java.util.List;

import medata.database.Patient;

import com.example.medata.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PatientAdapter extends ArrayAdapter<Patient> {

	public PatientAdapter(Context context, List<Patient> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater
					.inflate(R.layout.list_patients_entry, parent, false);
		}

		Patient patient = getItem(position);
		TextView nom = (TextView) view.findViewById(R.id.patient_name);
		nom.setText(patient.getNom() + " " + patient.getPrenom());
		TextView num_dossier = (TextView) view
				.findViewById(R.id.patient_folder_number);
		num_dossier.setText(patient.getNumeroDossier());
		return view;
	}

}
