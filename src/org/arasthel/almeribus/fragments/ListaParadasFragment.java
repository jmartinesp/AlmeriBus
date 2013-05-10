package org.arasthel.almeribus.fragments;

import java.util.ArrayList;
import java.util.List;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.interfaces.ListaParadasAdapter;
import org.arasthel.almeribus.utils.LoadFromWeb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class ListaParadasFragment extends SherlockFragment{
	
	public static ListaParadasFragment newInstance(int tipo) {
		ListaParadasFragment f = new ListaParadasFragment();
		f.setTipoListaParadas(tipo);
		return f;
		
	}
	
	public static int TODAS = 0;
	public static int FAVORITAS = 1;
	public static String UPDATE_FILTER = "org.arasthel.almeribus.ACTUALIZAR_LISTA";
	
	private int tipoListaParadas;
	
	private BroadcastReceiver updateFavReceiver;
	
	private ListView listaParadas;
	
	public ListaParadasFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.lista_paradas, null);
		updateFavReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				actualizarLista();
			}
		};
		return v;
	}
	
	@Override
	public void onResume() {
		actualizarLista();
		super.onResume();
	}
	
	@Override
	public void onStart() {
		getActivity().registerReceiver(updateFavReceiver,new IntentFilter(UPDATE_FILTER));
		super.onStart();
	}
	
	@Override
	public void onStop() {
		if(updateFavReceiver != null) {
			getActivity().unregisterReceiver(updateFavReceiver);
		}
		super.onStop();
	}
	
	public void actualizarLista() {
		ListaParadasAdapter adapter = new ListaParadasAdapter(getActivity(), 0, 0);
		ArrayList<Integer> paradas;
		if(tipoListaParadas == TODAS) {
			paradas = LoadFromWeb.buscarParadas();
		} else {
			paradas = LoadFromWeb.buscarParadasFavoritas();
		}
		for(Integer parada : paradas) {
			adapter.add(parada);
		}
		listaParadas = (ListView) getView().findViewById(android.R.id.list);
		TextView emptyView = (TextView) getView().findViewById(R.id.empty_view);
		if(tipoListaParadas == FAVORITAS) {
			emptyView.setText("No tiene paradas favoritas");
		} else {
			emptyView.setText("No se han cargado paradas. Comuníqueselo al desarrollador de la aplicación, por favor");
		}
		listaParadas.setEmptyView(emptyView);
		listaParadas.setAdapter(adapter);
		listaParadas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int idParada = ((ListaParadasAdapter) (listaParadas.getAdapter())).getItem(arg2);
				DialogFragment dialog = MostrarInfoParada.newInstance(idParada);
				dialog.show(getChildFragmentManager(), "Info_Parada");
			}
		});
	}

	public int getTipoListaParadas() {
		return tipoListaParadas;
	}

	public void setTipoListaParadas(int tipoListaParadas) {
		this.tipoListaParadas = tipoListaParadas;
	}
	
	public void setParadas(List<Integer> paradas) {
		if(getActivity() != null) {
			ListaParadasAdapter adapter = new ListaParadasAdapter(getActivity(), 0, 0);
			for(Integer parada : paradas) {
				adapter.add(parada);
			}
			listaParadas.setAdapter(adapter);
		}
	}

	
	
}
