package org.arasthel.almeribus.interfaces;

import java.util.ArrayList;

import org.arasthel.almeribus.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListaTarifasAdapter extends ArrayAdapter<String>{
	
	private ArrayList<String> tarifas;
	private ArrayList<String> precios;
	
	public ListaTarifasAdapter(Context context, ArrayList<String> nombres, ArrayList<String> precios) {
		super(context, 0, 0);
		setDatos(nombres, precios);
	}
	
	public void setDatos(ArrayList<String> nombres, ArrayList<String> precios) {
		this.tarifas = (ArrayList<String>) nombres.clone();
		this.precios = (ArrayList<String>) precios.clone();
		notifyDataSetInvalidated();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = View.inflate(getContext(), R.layout.fila_lista_tarifas, null);
			holder = new ViewHolder();
			holder.precioTV = (TextView) convertView.findViewById(android.R.id.text2);
			holder.nombreTarifaTV = (TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.precioTV.setText(precios.get(position));
		holder.nombreTarifaTV.setText(tarifas.get(position));
		return convertView;
	}
	
	@Override
	public int getCount() {
		return tarifas.size();
	}
	
	private static class ViewHolder {
		public TextView precioTV;
		public TextView nombreTarifaTV;
		
	}

}
