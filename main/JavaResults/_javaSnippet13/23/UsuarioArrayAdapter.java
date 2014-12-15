package br.com.getlucky.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.com.getlucky.R;
import br.com.getlucky.entity.Usuario;

public class UsuarioArrayAdapter extends ArrayAdapter<Usuario> {
	
	public UsuarioArrayAdapter(Context context, int viewResourceId, List<Usuario> usuarios) {
		super(context, viewResourceId, usuarios);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Usuario usuario = getItem(position);
		
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = layoutInflater.inflate(R.layout.list_usuario, parent, false);
		
		TextView textView = (TextView) view.findViewById(R.id.textView_usuario_nome);
		textView.setText(usuario.getNome());
		
		return view;
		
	}

}
