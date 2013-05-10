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

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.utils.DataStorage;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FiltrarDistanciaFragment extends DialogFragment{
	
	private SeekBar distanciaSeekBar;
	private String[] distanciasTexto;
	private int[] distanciasInt;
	private TextView distancia;
	
	public FiltrarDistanciaFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Filtrar por distancia");
		View v = inflater.inflate(R.layout.filtro_distancia, null);
		distanciaSeekBar = (SeekBar) v.findViewById(R.id.distancia_seekbar);
		distancia = (TextView) v.findViewById(R.id.distancia);
		distanciasTexto = getResources().getStringArray(R.array.distancia_filtro);
		distanciasInt = getResources().getIntArray(R.array.distancia_filtro_values);
		distanciaSeekBar.setMax(distanciasInt.length-1);
		int posicion = -1;
		int distanciaActual = DataStorage.filtro.getDistancia();
		if(distanciaActual == Integer.MAX_VALUE) {
			distanciaActual = 0;
		}
		for(int i = 0; i < distanciasInt.length; i++) {
			if(distanciaActual == distanciasInt[i]) {
				posicion = i;
				break;
			}
		}
		distanciaSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				DataStorage.filtro.setDistacia(distanciasInt[arg1]);
				if(DataStorage.filtro.getDistancia() == 0) {
					DataStorage.filtro.setDistacia(Integer.MAX_VALUE);
				}
				distancia.setText(distanciasTexto[arg1]);
			}
		});
		distanciaSeekBar.setProgress(posicion);
		return v;
	}

}
