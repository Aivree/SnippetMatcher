package br.schoollabs.mykeys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.schoollabs.mykeys.model.Registry;

public class SettingsAdapter extends ArrayAdapter<Registry> {
	private Context context;

	public SettingsAdapter(Context context) {
		super(context, R.layout.linear_settings);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// View de renderização de cada item
		View rowView = inflater.inflate(R.layout.linear_settings, parent, false);

		// TextView para o titulo
		TextView textView = (TextView) rowView.findViewById(R.id.labelSettings);

		// Objeto clicado
		Registry registry = this.getItem(position);
		// Seta no TextView do Titulo
		textView.setText(registry.getContent());

		return rowView;
	}
}
