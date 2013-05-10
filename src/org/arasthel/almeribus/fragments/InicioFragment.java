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

import org.arasthel.almeribus.Principal;
import org.arasthel.almeribus.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class InicioFragment extends SherlockFragment {
	
	private boolean updated;
	private BroadcastReceiver mReceiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		updated = false;
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if(intent.getAction().equals("org.arasthel.UPDATE_FINISHED")) {
					updated = true;
					View progress = getView().findViewById(R.id.progress);
					progress.setVisibility(View.GONE);
					TextView info = (TextView) getView().findViewById(R.id.info);
					info.setText("Datos cargados, por favor, dislice el dedo hacia la derecha o pulse \"Atrás\" para ir al menú principal.");
					View slide = getView().findViewById(R.id.slide);
					slide.setVisibility(View.VISIBLE);
					((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
					//((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
				} else if (intent.getAction().equals("org.arasthel.UPDATE_FAILED")) {
					View progress = getView().findViewById(R.id.progress);
					progress.setVisibility(View.GONE);
					TextView info = (TextView) getView().findViewById(R.id.info);
					info.setText("No se pudieron cargar los datos, revise su conexión a internet y vuelva a intentarlo. También puede intentar usar la aplicación, pero es probable que haya errores.");
					View slide = getView().findViewById(R.id.slide);
					slide.setVisibility(View.GONE);
					((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
					Button retry = (Button) getView().findViewById(R.id.retry);
					retry.setVisibility(View.VISIBLE);
					retry.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							arg0.setVisibility(View.GONE);
							View progress = getView().findViewById(R.id.progress);
							progress.setVisibility(View.VISIBLE);
							((Principal) getActivity()).cargarParadas();
						}
					});
				}
				
			}
		};
		View v = inflater.inflate(R.layout.main, null);
		return v;
	}
	
	@Override
	public void onStart() {
		getActivity().registerReceiver(mReceiver, new IntentFilter("org.arasthel.UPDATE_FINISHED"));
		getActivity().registerReceiver(mReceiver, new IntentFilter("org.arasthel.UPDATE_FAILED"));
		super.onStart();
	}
	
	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mReceiver);
		super.onStop();
	}
	
	@Override
	public void onResume() {
		if(updated) {
			((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		super.onResume();
	}

}
