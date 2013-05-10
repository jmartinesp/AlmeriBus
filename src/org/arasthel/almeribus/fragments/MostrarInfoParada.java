package org.arasthel.almeribus.fragments;

import java.util.ArrayList;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.interfaces.InfoFragmentPagerAdapter;
import org.arasthel.almeribus.interfaces.Parada;
import org.arasthel.almeribus.utils.DataStorage;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.viewpagerindicator.CirclePageIndicator;

public class MostrarInfoParada extends SherlockDialogFragment{

	public static MostrarInfoParada newInstance(int parada) {
		MostrarInfoParada m = new MostrarInfoParada();
		Bundle b = new Bundle();
		b.putInt("parada", parada);
		m.setArguments(b);
		return m;
	}
	
	public MostrarInfoParada() {
	}

	private int numParada;
	private ViewPager vPager;
	private View content;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.numParada = getArguments().getInt("parada");
		content = inflater.inflate(R.layout.info_parada_dialog, null);
		vPager = (ViewPager) content.findViewById(R.id.view_pager);
		return content;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		InfoFragmentPagerAdapter adapter = new InfoFragmentPagerAdapter(getChildFragmentManager());
		ArrayList<Integer> lineas = new ArrayList<Integer>();
		for(Integer l : DataStorage.paradas.get(numParada).getLineas()) {
			lineas.add(l);
		}
		adapter.addItem(InfoParadaFragment.newInstance(numParada, lineas));
		Parada paradaActual = DataStorage.paradas.get(numParada);
		if(paradaActual.getRepetida() != null){
			
			int otraParada = paradaActual.getRepetida().getId();
			adapter.addItem(InfoParadaFragment.newInstance(otraParada, lineas));
		}
		vPager.setAdapter(adapter);
		CirclePageIndicator indicator = (CirclePageIndicator) content.findViewById(R.id.indicator);
		if(vPager.getAdapter().getCount() > 1) {
			indicator.setViewPager(vPager);
		} else {
			indicator.setVisibility(View.GONE);
		}
		super.onStart();
	}
	
	

}