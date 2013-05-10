package org.arasthel.almeribus.interfaces;

import java.util.ArrayList;

import org.arasthel.almeribus.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListaAvisosAdapter extends ArrayAdapter<String>{
	
	private ArrayList<String> fechas;
	private ArrayList<String> nombres;
	private ArrayList<String> textos;
	
	public ListaAvisosAdapter(Context context, ArrayList<String> fechas, ArrayList<String> nombres, ArrayList<String> textos) {
		super(context, 0, 0);
		setDatos(fechas, nombres, textos);
	}
	
	public void setDatos(ArrayList<String> fechas, ArrayList<String> nombres, ArrayList<String> textos) {
		this.fechas = (ArrayList<String>) fechas.clone();
		this.nombres = (ArrayList<String>) nombres.clone();
		this.textos = (ArrayList<String>) textos.clone();
		notifyDataSetInvalidated();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = View.inflate(getContext(), R.layout.fila_lista_avisos, null);
			holder = new ViewHolder();
			holder.fechaAviso = (TextView) convertView.findViewById(android.R.id.text2);
			holder.nombreAviso = (TextView) convertView.findViewById(android.R.id.text1);
			holder.moreInfo = convertView.findViewById(R.id.mas_info);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.fechaAviso.setText(nombres.get(position));
		holder.nombreAviso.setText(fechas.get(position));
		holder.moreInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder alertBuilder = new Builder(getContext());
				alertBuilder.setTitle("Información del aviso");
				alertBuilder.setMessage(textos.get(position));
				alertBuilder.setPositiveButton("Aceptar", null);
				alertBuilder.show();
			}
		});
		return convertView;
	}
	
	@Override
	public int getCount() {
		return fechas.size();
	}
	
	private static class ViewHolder {
		public TextView fechaAviso;
		public TextView nombreAviso;
		public View moreInfo;
		
	}

}
