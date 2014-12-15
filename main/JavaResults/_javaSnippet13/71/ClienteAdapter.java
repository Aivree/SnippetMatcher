package com.example.vendedores;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dominio.Cliente;

public class ClienteAdapter extends ArrayAdapter<Cliente> {
	    	    
	    public ClienteAdapter(Context context, int resource, ArrayList<Cliente> data) {
	        super(context, resource, data);
	    }
	    	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	if (convertView == null) {
		        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        convertView = inflater.inflate(R.layout.cliente_adapter, null);
	        }
	        
	        ((TextView)convertView.findViewById(R.id.cliente_text)).setText(getItem(position).toString());
	        
	        /*
	        if (clientes.get(position).getTiene_mensajes()) {
	        	convertView.findViewById(R.id.mensaje_icon).setVisibility(View.VISIBLE);
	        } else {
	        	convertView.findViewById(R.id.mensaje_icon).setVisibility(View.INVISIBLE);
	        }
	         */
	        return convertView;
	    }
	}
