package es.edu.android.restdroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Adaptador para mostrar las puntuaciones mediante vistas
 * 
 * @author e.astolfi
 */
public class MyAdapter extends BaseAdapter {
	private final Activity activity;
//	private final ArrayList<String> listHeader;
//	private final ArrayList<String> listContent;
	private final ArrayList<HashMap<String, String>> lista;
	
	public MyAdapter(Activity activity, ArrayList<HashMap<String, String>> params) {
		super();
		this.activity = activity;
//		this.listHeader = listHeader;
//		this.listContent = listContent;
		this.lista = params;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = activity.getLayoutInflater();
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.result_element, null);
		
//		TextView txtFecha = (TextView) view.findViewById(R.id.txtHoraNoticia);
//		txtFecha.setText(listHeader.get(position));
		
//		TextView txtContenido = (TextView) view.findViewById(R.id.txtContenidoNoticia);
//		txtContenido.setText(listContent.get(position));
		
		TableLayout table = new TableLayout(activity);
		Set<Entry<String,String>> res = lista.get(position).entrySet();
		for (Iterator iterator = res.iterator(); iterator.hasNext();) {
			Entry<String, String> next = (Entry<String, String>) iterator.next();
			String txtCampo = next.getKey();
			String txtVal = next.getValue();
			
			TableRow row = new TableRow(activity);
			TextView valor = new TextView(activity);
			valor.setText(txtVal);
			TextView campo = new TextView(activity);
			campo.setText(txtCampo);
			row.addView(campo);
			row.addView(valor);
			table.addView(row);
			
//			view.addView(campo);
			
		}
		view.addView(table);
		return view;
	}
	
	@Override
	public int getCount() {
		return lista.size();
	}

	@Override
	public Object getItem(int arg0) {
		return lista.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
