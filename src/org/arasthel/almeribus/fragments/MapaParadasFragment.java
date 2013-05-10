/*******************************************************************************
 * Copyright (c) 2013 Jorge Mart�n Espinosa (Arasthel).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jorge Mart�n Espinosa (Arasthel) - initial API and implementation
 ******************************************************************************/
package org.arasthel.almeribus.fragments;

import java.util.Observable;
import java.util.Observer;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.utils.DataStorage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapaParadasFragment extends SherlockFragment implements Observer{
	
	private CustomMapFragment map;
	
    private View distanciaBtn;
    private View terrenoBtn;
    private View paradasBtn;
    private LatLng coordenadas;
    
    public static MapaParadasFragment newInstance() {
    	MapaParadasFragment map = new MapaParadasFragment();
    	Bundle b = new Bundle();
		b.putDouble("latitud", 36.839292);
		b.putDouble("longitud", -2.459334);
    	map.setArguments(b);
    	return map;
    }
	
    public static MapaParadasFragment newInstance(LatLng coord) {
    	MapaParadasFragment map = new MapaParadasFragment();
    	Bundle b = new Bundle();
    	b.putDouble("latitud", coord.latitude);
    	b.putDouble("longitud", coord.longitude);
    	map.setArguments(b);
    	return map;
    }
    
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		coordenadas = new LatLng(getArguments().getDouble("latitud"), getArguments().getDouble("longitud"));
		View v = inflater.inflate(R.layout.buscar_parada, null);
		return v;
	}
	
	@Override
	public void onStart() {
		View errorMapa = getView().findViewById(R.id.error_mapa);
		View controlesMapa = getView().findViewById(R.id.controles_mapa);
		if(isGoogleMapsInstalled()) {
			if(Build.VERSION.SDK_INT < 8) {
				DataStorage.filtro.addObserver(this);
				map = CustomMapFragment.newInstance(coordenadas.longitude, coordenadas.latitude);
				getChildFragmentManager().beginTransaction().add(R.id.container_map, map).commit();
				//getSherlockActivity().getSupportFragmentManager().beginTransaction().add(R.id.container_map, map).commit();
			} else if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS) {
				DataStorage.filtro.addObserver(this);
				map = CustomMapFragment.newInstance(coordenadas.longitude, coordenadas.latitude);
				getChildFragmentManager().beginTransaction().add(R.id.container_map, map).commit();
				//getSherlockActivity().getSupportFragmentManager().beginTransaction().add(R.id.container_map, map).commit();
				
			} else {
				Button errorBtn = (Button) getView().findViewById(R.id.error_mapa_button);
				errorBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						try{
							Intent i = new Intent(android.content.Intent.ACTION_VIEW);
							i.setData(Uri.parse("market://details?id=com.google.android.gms"));
							startActivity(i);
						} catch (ActivityNotFoundException e) {
							Button errorBtn = (Button) getView().findViewById(R.id.error_mapa_button);
							errorBtn.setVisibility(View.GONE);
							TextView errorText = (TextView) getView().findViewById(R.id.error_mapa_text);
							errorText.setText("No tiene instalado Google Services ni Google Play. Por desgracia, esta funcionalidad de la aplicación hace uso de Google Maps, que a su vez depende de los anteriores.\n\nNo es posible usar esta funcionalidad.");
						}
					}
				});
				TextView errorText = (TextView) getView().findViewById(R.id.error_mapa_text);
				errorText.setText("Debe descargar Google Play Services de Google Play para poder usar esta funcionalidad.");
				errorMapa.setVisibility(View.VISIBLE);
				controlesMapa.setVisibility(View.GONE);
			}
		} else {
			Button errorBtn = (Button) getView().findViewById(R.id.error_mapa_button);
			errorBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try{
						Intent i = new Intent(android.content.Intent.ACTION_VIEW);
						i.setData(Uri.parse("market://details?id=com.google.android.apps.maps"));
						startActivity(i);
					} catch (ActivityNotFoundException e) {
						Button errorBtn = (Button) getView().findViewById(R.id.error_mapa_button);
						errorBtn.setVisibility(View.GONE);
						TextView errorText = (TextView) getView().findViewById(R.id.error_mapa_text);
						errorText.setText("No tiene instalado Google Services ni Google Play. Por desgracia, esta funcionalidad de la aplicación hace uso de Google Maps, que a su vez depende de los anteriores.\n No es posible usar esta funcionalidad.");
					}
				}
			});
			TextView errorText = (TextView) getView().findViewById(R.id.error_mapa_text);
			errorText.setText("No tiene instalado Google Maps, por favor, descárguelo y vuelva a iniciar la aplicación.");
			errorMapa.setVisibility(View.VISIBLE);
			controlesMapa.setVisibility(View.GONE);
		}
		super.onStart();
	}
	
	public boolean isGoogleMapsInstalled()
	{
	    try
	    {
	        ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        return true;
	    } 
	    catch(PackageManager.NameNotFoundException e)
	    {
	        return false;
	    }
	}
	
	@Override
	public void onResume() {
		getActivity().setTitle("Mapa de paradas");
		
		if(map != null && map.getMap() != null) {
			buscarParadas();
		}
		activarBotones();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if(map != null && map.getMap() != null) {
			map.getMap().setMyLocationEnabled(false);
		}
		super.onPause();
	}

	public void activarBotones() {
		distanciaBtn = getView().findViewById(R.id.filtro_distancia_btn);
		paradasBtn = getView().findViewById(R.id.filtro_paradas_btn);
		terrenoBtn = getView().findViewById(R.id.mostrar_satelite);
		
		distanciaBtn.setEnabled(true);
		distanciaBtn.setClickable(true);
		distanciaBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				FiltrarDistanciaFragment distanciaFragment = new FiltrarDistanciaFragment();
				distanciaFragment.show(getSherlockActivity().getSupportFragmentManager(), "Distancia_fragment");
			}
		});
		
		paradasBtn.setEnabled(true);
		paradasBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				FiltrarParadasFragment paradasFragment = new FiltrarParadasFragment();
				paradasFragment.show(getSherlockActivity().getSupportFragmentManager(), "Paradas_fragment");
			}
		});
		
		terrenoBtn.setEnabled(true);
		terrenoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(map.getMap().getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
					map.getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
				} else {
					map.getMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
				}
			}
		});
	}

	public void buscarParadas(){
		map.buscarParadas();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		buscarParadas();
	}
	
	public void centrarMapa(LatLng centrarCoord) {
		if(map != null && map.getMap() != null) {
			map.getMap().moveCamera(CameraUpdateFactory.newLatLng(centrarCoord));
		}
	}
	
	
}
