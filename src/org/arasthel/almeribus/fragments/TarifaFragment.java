package org.arasthel.almeribus.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.interfaces.ListaTarifasAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class TarifaFragment extends SherlockFragment{
	
	private ArrayList<String> nombresTarifas;
	private ArrayList<String> preciosTarifas;
	private ListView listaTarifas;
	
	public TarifaFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tarifas_layout, null);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getActivity().setTitle("Tarifas del servicio");
		((SlidingFragmentActivity) getSherlockActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		nombresTarifas = new ArrayList<String>();
		preciosTarifas = new ArrayList<String>();
		listaTarifas = (ListView) getView().findViewById(R.id.lista_tarifas);
		listaTarifas.setEmptyView(getView().findViewById(R.id.empty_view));
		ListaTarifasAdapter adapter = new ListaTarifasAdapter(getActivity(), nombresTarifas, preciosTarifas);
		listaTarifas.setAdapter(adapter);
		new CargarTarifas().execute();
		super.onActivityCreated(savedInstanceState);
	}
	
	private class CargarTarifas extends AsyncTask<Void, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			nombresTarifas = new ArrayList<String>();
			preciosTarifas = new ArrayList<String>();
			try {
				String urlWeb = "http://www.surbus.com/tarifas.aspx";
				Document doc = Jsoup.connect(urlWeb).timeout(10000).get();
				Elements tarifasElements = doc.getElementsByClass("tarifas");
				for(int i = 1; i < tarifasElements.size(); i++) {
					Element filaTarifas = tarifasElements.get(i);
					Elements rows = filaTarifas.getElementsByTag("tbody").first().getElementsByTag("tr");
					for(Element row : rows) {
						Elements columns = row.getElementsByTag("td");
						if(columns.get(0).text().length() == 0) {
							continue;
						}
						nombresTarifas.add(columns.get(0).text());
						preciosTarifas.add(columns.get(1).text());
					}
				}
				
				
			} catch (MalformedURLException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result && getActivity() != null && getView() != null) {
				listaTarifas.setVisibility(View.VISIBLE);
				ListaTarifasAdapter adapter = new ListaTarifasAdapter(getActivity(), nombresTarifas, preciosTarifas);
				listaTarifas.setAdapter(adapter);
				ocultarError();
				View emptyView = getView().findViewById(R.id.empty_view);
				emptyView.setVisibility(View.GONE);
			} else {
				mostrarError();
			}
			super.onPostExecute(result);
		}
		
		public void mostrarError() {
			if(getActivity() != null && getView() != null) {
				View emptyView = getView().findViewById(R.id.empty_view);
				emptyView.setVisibility(View.GONE);
				View errorLoading = getView().findViewById(R.id.error_loading);
				errorLoading.setVisibility(View.VISIBLE);
				View retry = errorLoading.findViewById(R.id.retry);
				retry.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						ocultarError();
						new CargarTarifas().execute();
					}
				});
			}
		}
		
		public void ocultarError() {
			if(getActivity() != null && getView() != null) {
				View errorLoading = getView().findViewById(R.id.error_loading);
				errorLoading.setVisibility(View.GONE);
				View emptyView = getView().findViewById(R.id.empty_view);
				emptyView.setVisibility(View.VISIBLE);
				listaTarifas.setVisibility(View.VISIBLE);
			}
		}
	}

}
