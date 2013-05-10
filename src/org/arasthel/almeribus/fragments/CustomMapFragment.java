/*******************************************************************************
 * Copyright (c) 2013 Jorge Mart’n Espinosa (Arasthel).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jorge Mart’n Espinosa (Arasthel) - initial API and implementation
 ******************************************************************************/
package org.arasthel.almeribus.fragments;

import java.util.ArrayList;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.interfaces.Parada;
import org.arasthel.almeribus.utils.DataStorage;
import org.arasthel.almeribus.utils.LoadFromWeb;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomMapFragment extends SupportMapFragment implements OnMyLocationChangeListener{
	
	private boolean centered = false;
	private double[] misCoordenadas;
	private LatLng posUltimaBusqueda;
	private BitmapDescriptor bitmapParada;
	private BuscarParadasTask buscarTask;
	private Circle distanciaCirculo;
	private LatLng coordenadas;
	private boolean mustCenter = true;
	
	public static CustomMapFragment newInstance(double longitud, double latitud) {
		CustomMapFragment map = new CustomMapFragment();
		Bundle b = new Bundle();
		b.putDouble("latitud", latitud);
		b.putDouble("longitud", longitud);
		map.setArguments(b);
		map.mustCenter = false;
		return map;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 coordenadas = new LatLng(getArguments().getDouble("latitud"), getArguments().getDouble("longitud"));
		 View view = super.onCreateView(inflater, container, savedInstanceState);
		 setMapTransparent((ViewGroup) view);
		 return view;
	 };
	
	private void setMapTransparent(ViewGroup group) {
		int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup) {
				setMapTransparent((ViewGroup) child);
			} else if (child instanceof SurfaceView) {
				child.setBackgroundColor(0x00000000);
				break;
			}
		}
	}
	
	@Override
	public void onResume() {
		if(getMap() != null && getView() != null) {
			// Ponemos que inicie el mapa en el Kiosko de Surbus
			getMap().moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
			getMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
			getMap().setMyLocationEnabled(true);
			getMap().setOnMyLocationChangeListener(this);
			getMap().moveCamera(CameraUpdateFactory.zoomTo(15));
			getMap().setOnMarkerClickListener(new OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker marker) {
					synchronized (DataStorage.paradas) {
						Parada p = DataStorage.paradas.get(Integer.parseInt(marker.getTitle()));
						DialogFragment f = MostrarInfoParada.newInstance(p.getId());
						f.show(getActivity().getSupportFragmentManager(), "Info");
					}
					return true;
				}
			});
			bitmapParada = BitmapDescriptorFactory.fromResource(R.drawable.bus_stop);
			buscarParadas();
		}
		super.onResume();
	}

	@Override
	public void onMyLocationChange(Location location) {
		misCoordenadas = new double[] {location.getLongitude(), location.getLatitude()};
    	
    	if(!centered && mustCenter && getMap() != null){
    		getMap().moveCamera(CameraUpdateFactory.newLatLng(new LatLng(misCoordenadas[1], misCoordenadas[0])));
    		centered = true;
    		buscarParadas();
    	}
    	
    	if(posUltimaBusqueda == null) {
    		posUltimaBusqueda = new LatLng(misCoordenadas[0], misCoordenadas[1]);
    	} else {
    		if(DataStorage.filtro.getDistancia() == Integer.MAX_VALUE) {
    			return;
    		}
    		float[] distancia = new float[1];
    		Location.distanceBetween(posUltimaBusqueda.latitude, posUltimaBusqueda.longitude, misCoordenadas[0], misCoordenadas[1], distancia);
    		if(distancia[0] > 10) {
    			buscarParadas();
    			posUltimaBusqueda = new LatLng(misCoordenadas[0], misCoordenadas[1]);
    		}
    	}
	}
	
	public void buscarParadas(){
		if(buscarTask != null && !buscarTask.isCancelled()) {
			buscarTask.cancel(true);
		}
		buscarTask = new BuscarParadasTask();
		buscarTask.execute();
	}
	
	public void dibujarDistancia() {
	   if(getMap() != null && getMap().isMyLocationEnabled() && getMap().getMyLocation() != null) {
		   CircleOptions dOptions = new CircleOptions();
		   dOptions.center(new LatLng(getMap().getMyLocation().getLatitude(), getMap().getMyLocation().getLongitude()));
		   dOptions.radius(DataStorage.filtro.getDistancia());
		   dOptions.strokeColor(0xFFFF0000);
		   dOptions.fillColor(0x22FF0000);
		   if(distanciaCirculo != null) {
			   distanciaCirculo.remove();
		   }
		   distanciaCirculo = getMap().addCircle(dOptions);
		}
   }

	private class BuscarParadasTask extends AsyncTask<Void, Void, ArrayList<MarkerOptions>> {

		@Override
		protected ArrayList<MarkerOptions> doInBackground(Void... arg0) {
			ArrayList<MarkerOptions> markerOptionsParadas = new ArrayList<MarkerOptions>();
			MarkerOptions parada;
			ArrayList<Integer> paradas = LoadFromWeb.buscarParadas(misCoordenadas);
			synchronized (DataStorage.paradas) {
		    	for(Integer idParada : paradas) {
						Parada p = DataStorage.paradas.get(idParada);
		   	    		parada = new MarkerOptions()
			    				.title(String.valueOf(p.getId()))
			    				.snippet(p.getNombre())
			    				.position(new LatLng(p.getCoord()[1], p.getCoord()[0]))
			    				.icon(bitmapParada);
			    		markerOptionsParadas.add(parada);
		    	}
			}
			return markerOptionsParadas;
		}
		
		@Override
		protected void onPostExecute(ArrayList<MarkerOptions> result) {
			ArrayList<Integer> paradasAgregadas = new ArrayList<Integer>();
			if(getMap() != null) {
				getMap().clear();
				dibujarDistancia();
			}
			for(MarkerOptions mo : result) {
				synchronized (DataStorage.paradas) {
					Parada p = DataStorage.paradas.get(Integer.parseInt(mo.getTitle()));
					if(p.getRepetida() != null) {
						if(paradasAgregadas.contains(p.getRepetida().getId())) {
							continue;
						}
					}
					paradasAgregadas.add(Integer.parseInt(mo.getTitle()));
					if(getMap() != null) {
						getMap().addMarker(mo);
					}
				}
			}
			super.onPostExecute(result);
		}
		
	}
	
	
}
