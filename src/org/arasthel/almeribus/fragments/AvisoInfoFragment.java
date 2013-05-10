package org.arasthel.almeribus.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.arasthel.almeribus.R;
import org.arasthel.almeribus.interfaces.ListaAvisosAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class AvisoInfoFragment extends SherlockFragment{
	
	private ArrayList<String> fechas;
	private ArrayList<String> titulos;
	private ArrayList<String> textos;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new CargarAvisosTask().execute();
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.avisos_layout, null);
	}
	
	private class CargarAvisosTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			boolean result = false;
			fechas = new ArrayList<String>();
			titulos = new ArrayList<String>();
			textos = new ArrayList<String>();
			String strURL = "http://m.surbus.com/avisos/pagina";
			String pageHtml = "";
			Document documento = null;
			int i = 1;
			try {
				URL url = new URL(strURL);
				do{
					documento = Jsoup.parse(url, 10000);
					pageHtml = documento.text();
					Elements fechasTags = documento.getElementsByTag("h2");
					Elements titulosTags = documento.getElementsByTag("h3");
					Elements textosTags = documento.getElementsByTag("p");
					for(int j = 0; j < titulosTags.size(); j++) {
						if(fechas.contains(fechasTags.get(j).text())) {
							break;
						}
						fechas.add(fechasTags.get(j).text());
						titulos.add(titulosTags.get(j).text());
						textos.add(textosTags.get(j).text());
						result = true;
					}
					i++;
					url = new URL(strURL+"/"+i);
				} while (pageHtml.contains("Siguiente"));
			} catch (Exception e) {
				return false;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result) {
				if(getActivity() != null && getView() != null) {
					ocultarError();
					ListView listaAvisos = (ListView) getView().findViewById(R.id.lista_avisos);
					listaAvisos.setEmptyView(getView().findViewById(R.id.empty_view));
					ListaAvisosAdapter adapter = new ListaAvisosAdapter(getActivity(), fechas, titulos, textos);
					listaAvisos.setAdapter(adapter);
				}
			} else {
				mostrarError();
			}
			super.onPostExecute(result);
		}
		
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
					new CargarAvisosTask().execute();
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
			ListView listaAvisos = (ListView) getView().findViewById(R.id.lista_avisos);
			listaAvisos.setVisibility(View.VISIBLE);
		}
	}
	
}
