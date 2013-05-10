package org.arasthel.almeribus.fragments;

import java.util.ArrayList;

import org.arasthel.almeribus.Principal;
import org.arasthel.almeribus.R;
import org.arasthel.almeribus.interfaces.ResultTiempo;
import org.arasthel.almeribus.utils.DataStorage;
import org.arasthel.almeribus.utils.LoadFromWeb;
import org.arasthel.almeribus.utils.ParadaNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class InfoParadaFragment extends Fragment {
	
	private int mNumParada;
	private ArrayList<Integer> mLineas;
	private View content;
	
	public static InfoParadaFragment newInstance(int numParada, ArrayList<Integer> lineas) {
		InfoParadaFragment ipf = new InfoParadaFragment();
		Bundle b = new Bundle();
		b.putInt("parada", numParada);
		b.putSerializable("lineas", lineas);
		ipf.setArguments(b);
		return ipf;
	}
	
	public InfoParadaFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mNumParada = getArguments().getInt("parada");
		mLineas = (ArrayList<Integer>) getArguments().getSerializable("lineas");
		content = inflater.inflate(R.layout.info_parada_content, null);
		TextView nombreParada = (TextView) content.findViewById(R.id.nombreParada);
		nombreParada.setText(DataStorage.paradas.get(mNumParada).getNombre());
		TextView numeroParada = (TextView) content.findViewById(R.id.numeroParada);
		numeroParada.setText(String.valueOf(mNumParada));
		return content;
	}
	
	@Override
	public void onStart() {
		ImageView favorito = (ImageView) getView().findViewById(R.id.favorito);
		favorito.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ImageView favorito = (ImageView) getView().findViewById(R.id.favorito);
				if(favorito.isSelected()) {
					DataStorage.DBHelper.removeFavorito(mNumParada);
					favorito.setImageResource(R.drawable.favorite_button);
				} else {
					DataStorage.DBHelper.addFavorito(mNumParada);
					favorito.setImageResource(R.drawable.ic_menu_favorite_on);
				}
				favorito.setSelected(!favorito.isSelected());
				Intent i = new Intent(ListaParadasFragment.UPDATE_FILTER);
				getActivity().sendBroadcast(i);
			}
		});
		boolean isFavorito = DataStorage.DBHelper.isFavorito(mNumParada);
		if(isFavorito) {
			favorito.setSelected(true);
			favorito.setImageResource(R.drawable.ic_menu_favorite_on);
		} else {
			favorito.setSelected(false);
			favorito.setImageResource(R.drawable.favorite_button);
		}
		
		ImageView verEnMapa = (ImageView) getView().findViewById(R.id.abrir_mapa);
		verEnMapa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				double[] coord = DataStorage.paradas.get(mNumParada).getCoord();
				if(coord != null) {
					((Principal) getActivity()).abrirMapaParadas(new LatLng(coord[1], coord[0]), true);
					((MostrarInfoParada) getParentFragment()).dismiss();
				}
			}
		});
		
		LinearLayout listaTiempos = (LinearLayout) content.findViewById(R.id.tiemposEspera);
		listaTiempos.removeAllViews();
		if(mLineas.isEmpty()) {
			TextView tv = new TextView(getActivity());
			tv.setGravity(Gravity.CENTER);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.rightMargin = 10;
			lp.leftMargin = 10;
			lp.topMargin = 20;
			tv.setLayoutParams(lp);
			tv.setText("No hay conexión de internet.\n\nPor favor, compruebe que dispone de conexión y vuelva a intentarlo.");
			listaTiempos.addView(tv);
		} else {
			calcularTiempos();
		}
		super.onStart();
	}
	
	/*private class CalcularTiempoParada extends AsyncTask<Context, Void, ResultTiempo> {

		private int parada;
		private int linea;
		private View content;
		
		public CalcularTiempoParada(int parada, int linea, View content) {
			this.parada = parada;
			this.linea = linea;
			this.content = content;
		}
		
		@Override
		protected ResultTiempo doInBackground(Context... arg0) {
			return LoadFromWeb.calcularTiempo(getActivity(), parada, linea);
		}
		
		@Override
		protected void onPostExecute(ResultTiempo result) {
			if(getActivity() != null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View v = inflater.inflate(R.layout.fila_tiempo_espera, null);
				TextView tiempoEspera = (TextView) v.findViewById(R.id.tiempoEspera);
				String tiempoTexto = "";
				if(result.getTiempo() >= 0) {
					tiempoTexto = result.getTiempoTexto();
					int tiempo = result.getTiempo();
					if (tiempo % 60 < 0) {
						tiempoTexto = "Error al cargar";
					}
					
					try{
						int siguienteID = DataStorage.paradas.get(parada).getSiguiente(linea);
						tiempoTexto += "\nHacia "+DataStorage.paradas.get(siguienteID).getNombre();
					}catch (Exception e) {
						tiempoTexto += "\nNo se pudo encontrar la siguiente parada";
					}
				} else if(result.getTiempo() == LoadFromWeb.ERROR_IO) {
					tiempoTexto = "Hubo un error al cargar los datos.\nPor favor, vuelva a intenarlo.";
				} else if(result.getTiempo() == LoadFromWeb.NO_DATOS) {
					tiempoTexto = "No hay información.\nConsulte si hoy se usa la parada.";
				}
				
				tiempoEspera.setText(tiempoTexto);
				
				int resId = getResources().getIdentifier("linea"+linea, "drawable", getActivity().getPackageName());
				Drawable iconoLinea = getResources().getDrawable(resId);
				ImageView icono = (ImageView) v.findViewById(R.id.imageLinea);
				icono.setImageDrawable(iconoLinea);
				LinearLayout listaTiempos = (LinearLayout) content.findViewById(R.id.tiemposEspera);
				listaTiempos.addView(v);
				if(linea == mLineas.get(mLineas.size()-1)){
					View progress = content.findViewById(R.id.progress);
					progress.setVisibility(View.GONE);
					ImageView refrescar = (ImageView) content.findViewById(R.id.refrescar);
					refrescar.setImageResource(R.drawable.refresh_button);
					refrescar.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							calcularTiempos();
						}
					});
				}
			}
			super.onPostExecute(result);
		}
		
	}*/
	
	public void calcularTiempos() {
		ImageView refrescar = (ImageView) content.findViewById(R.id.refrescar);
		refrescar.setImageResource(R.drawable.ic_menu_refresh_disabled);
		refrescar.setOnClickListener(null);
		LinearLayout listaTiempos = (LinearLayout) content.findViewById(R.id.tiemposEspera);
		listaTiempos.removeAllViews();
		View progress = content.findViewById(R.id.progress);
		progress.setVisibility(View.VISIBLE);
		for(int numLinea : mLineas) {
			//new CalcularTiempoParada(mNumParada, numLinea, content).execute(getActivity());
			mostrarLinea(numLinea, mNumParada);
		}
	}
	
	public void mostrarLinea(int numLinea, int numParada) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.fila_tiempo_espera, null);
		TextView tiempoEspera = (TextView) v.findViewById(R.id.tiempoEspera);
		int resId = getResources().getIdentifier("linea"+numLinea, "drawable", getActivity().getPackageName());
		Drawable iconoLinea = getResources().getDrawable(resId);
		ImageView icono = (ImageView) v.findViewById(R.id.imageLinea);
		icono.setImageDrawable(iconoLinea);
		LinearLayout listaTiempos = (LinearLayout) content.findViewById(R.id.tiemposEspera);
		listaTiempos.addView(v);
		try {
			final int siguienteID = DataStorage.paradas.get(numParada).getSiguiente(numLinea);
			if(DataStorage.paradas.get(siguienteID) == null) {
				throw new ParadaNotFoundException();
			}
			String tiempoTexto = "Hacia "+DataStorage.paradas.get(siguienteID).getNombre();
			tiempoEspera.setText(tiempoTexto);
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					double[] coord = DataStorage.paradas.get(siguienteID).getCoord();
					if(coord != null) {
						((Principal) getActivity()).abrirMapaParadas(new LatLng(coord[1], coord[0]), false);
						MostrarInfoParada info = MostrarInfoParada.newInstance(siguienteID);
						((MostrarInfoParada) getParentFragment()).dismiss();
						info.show(getActivity().getSupportFragmentManager(), "TIEMPO_ESPERA");
						
					}
				}
			});
			if(numLinea == mLineas.get(mLineas.size()-1)){
				View progress = content.findViewById(R.id.progress);
				progress.setVisibility(View.GONE);
				ImageView refrescar = (ImageView) content.findViewById(R.id.refrescar);
				refrescar.setImageResource(R.drawable.refresh_button);
				refrescar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						calcularTiempos();
					}
				});
			}
		} catch (ParadaNotFoundException e) {
			tiempoEspera.setText("No se ha podido encontrar la siguiente parada");
		}
	}

}
