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
import org.arasthel.almeribus.interfaces.ListaParadasPagerAdapter;
import org.arasthel.almeribus.utils.LoadFromWeb;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.viewpagerindicator.TabPageIndicator;

public class MostrarListadosParadasFragment extends SherlockFragment {
	
	private ViewPager vPager;
	
	public MostrarListadosParadasFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.mostrar_listas_paradas, null);
		ListaParadasPagerAdapter adapter = new ListaParadasPagerAdapter(getChildFragmentManager());
		adapter.addItem(ListaParadasFragment.newInstance(ListaParadasFragment.FAVORITAS), "Favoritas");
		adapter.addItem(ListaParadasFragment.newInstance(ListaParadasFragment.TODAS), "Todas");
		vPager = (ViewPager) v.findViewById(R.id.paradas_view_pager);
		
		vPager.setAdapter(adapter);
		TabPageIndicator indicator = (TabPageIndicator) v.findViewById(R.id.indicator);
		indicator.setViewPager(vPager,0);
		getActivity().setTitle("Listado de paradas");
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
					
					@Override
					public void onPageSelected(int position) {
						TabPageIndicator indicator = (TabPageIndicator) getView().findViewById(R.id.indicator);
						if(position == 0) {
							((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
						} else {
							((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
						}
						indicator.setCurrentItem(position);
						
					}
					
					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onPageScrollStateChanged(int arg0) {
						
					}
				});
		if(vPager.getAdapter().getCount() > 0) {
			((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if(Build.VERSION.SDK_INT >= 8) {
			getSherlockActivity().getSupportMenuInflater().inflate(R.menu.buscar_parada_menu, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(Build.VERSION.SDK_INT >= 8) {
			SearchView searchView = new SearchView(getSherlockActivity().getSupportActionBar().getThemedContext());
		    searchView.setQueryHint("Buscar paradasâ€¦");
		    searchView.setIconified(true);
		    searchView.setOnQueryTextListener(new OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String query) {
					buscarParadas(query.toString());
					return false;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) {
					buscarParadas(newText);
					return false;
				}
			});
			MenuItem buscar = menu.findItem(R.id.buscar);
			buscar.setVisible(true);
			buscar.setActionView(searchView);
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	public void buscarParadas(String newText) {
		ArrayList<Integer> paradas = LoadFromWeb.buscarParadas(newText);
		ArrayList<Integer> paradasFavoritas = LoadFromWeb.buscarParadasFavoritas(newText);
		ListaParadasFragment lista = (ListaParadasFragment) ((ListaParadasPagerAdapter) vPager.getAdapter()).getItem(1);
		ListaParadasFragment listaFavoritos = (ListaParadasFragment) ((ListaParadasPagerAdapter) vPager.getAdapter()).getItem(0);  
		lista.setParadas(paradas);
		listaFavoritos.setParadas(paradasFavoritas);
	}
}
