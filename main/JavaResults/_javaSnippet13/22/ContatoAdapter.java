package br.com.cmendesce.springrest.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.com.cmendesce.springrest.R;
import br.com.cmendesce.springrest.models.Contato;

public class ContatoAdapter extends ArrayAdapter<Contato> {
	
	public ContatoAdapter(Context context, Contato[] list) {
		super(context, 0, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.row_contato, null);
		
		TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
		TextView txtTelefone = (TextView) view.findViewById(R.id.txtTelefone);
		
		Contato item = getItem(position);
		
		txtNome.setText(item.getNome());
		txtTelefone.setText(item.getTelefone());
		
		return view;
	}
}