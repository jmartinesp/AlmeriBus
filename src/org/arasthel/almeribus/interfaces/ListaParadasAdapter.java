package org.arasthel.almeribus.interfaces;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.utils.DataStorage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListaParadasAdapter extends ArrayAdapter<Integer>{
	
	public ListaParadasAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int paradaId = getItem(position);
		ViewHolder holder;
		if(convertView == null) {
			convertView = View.inflate(getContext(), R.layout.fila_lista_paradas, null);
			holder = new ViewHolder();
			holder.idParadaTV = (TextView) convertView.findViewById(android.R.id.text1);
			holder.nombreParadaTV = (TextView) convertView.findViewById(android.R.id.text2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.idParadaTV.setText(String.valueOf(DataStorage.paradas.get(paradaId).getId()));
		holder.nombreParadaTV.setText(DataStorage.paradas.get(paradaId).getNombre());
		return convertView;
	}
	
	private static class ViewHolder {
		public TextView idParadaTV;
		public TextView nombreParadaTV;
		
	}

}
