/*******************************************************************************
 * Copyright (c) 2013 Jorge Martín Espinosa (Arasthel).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jorge Martín Espinosa (Arasthel) - initial API and implementation
 ******************************************************************************/
package org.arasthel.almeribus.fragments;

import java.util.HashSet;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.utils.DataStorage;
import org.arasthel.almeribus.widgets.FlowLayout;
import org.arasthel.almeribus.widgets.ImagenParada;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FiltrarParadasFragment extends DialogFragment{
	
	private FlowLayout contenedorLineas;
	
	public FiltrarParadasFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Filtrar por l√≠neas");
		View v = inflater.inflate(R.layout.filtro_paradas, null);
		contenedorLineas = (FlowLayout) v.findViewById(R.id.contenedor_lineas);
		contenedorLineas.removeAllViews();
		
		float density = getResources().getDisplayMetrics().density;
		for(int linea : DataStorage.numLineas) {
			ImagenParada ip = new ImagenParada(getActivity());
			ip.setId(linea);
			ip.setArchivoBase("linea"+linea);
			ip.setLayoutParams(new LinearLayout.LayoutParams((int) (42*density), (int) (42*density)));
			contenedorLineas.addView(ip);
		}
		return v;
	}
	
	@Override
	public void onResume() {
		View ninguna = getView().findViewById(R.id.ninguna_parada);
		ninguna.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(contenedorLineas == null) {
					return;
				}
				for(int i = 0; i < contenedorLineas.getChildCount(); i++) {
					ImagenParada ip = (ImagenParada) contenedorLineas.getChildAt(i);
					ip.setActivado(false);
				}
				DataStorage.filtro.setLineas(new HashSet<Integer>());
			}
		});
		
		View todas = getView().findViewById(R.id.todas_paradas);
		todas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(contenedorLineas == null) {
					return;
				}
				for(int i = 0; i < contenedorLineas.getChildCount(); i++) {
					ImagenParada ip = (ImagenParada) contenedorLineas.getChildAt(i);
					ip.setActivado(true);
				}
				HashSet<Integer> lineas = new HashSet<Integer>();
				for(int linea : DataStorage.numLineas) {
					lineas.add(linea);
				}
				DataStorage.filtro.setLineas(lineas);
			}
		});
		super.onResume();
	}

}
