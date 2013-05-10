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
package org.arasthel.almeribus.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.arasthel.almeribus.interfaces.Linea;
import org.arasthel.almeribus.interfaces.Parada;
import org.arasthel.almeribus.interfaces.ResultTiempo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class LoadFromWeb {
	
	private static String cookie;
	
	private static String QUERY_ADDRESS_PARADAS_LINEA = "http://m.surbus.com/tiempo-espera/obtener-paradas/";
	private static String QUERY_ADDRESS_TIEMPO_PARADA = "http://m.surbus.com/tiempo-espera/calcular/";
	
	public final static int MANTENIMIENTO = -3;
	public final static int NO_DATOS = -2;
	public final static int ERROR_IO = -1;
	
	public final static int SIN_CONEXION = 0;
	public final static int TODO_OK = 1;
	
	public static ArrayList<String> claves;
	
	public static int cargarParadasLinea(Context context, int numeroLinea){
		if(!isConnectionEnabled(context)) {
			return SIN_CONEXION;
		}
		try {
			if(cookie == null) {
				if(loadCookie() == MANTENIMIENTO) {
					return MANTENIMIENTO;
				}
			}
			URL url = new URL(QUERY_ADDRESS_PARADAS_LINEA + numeroLinea);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(30000);
			connection.setRequestProperty("Cookie", "ASP.NET_SessionId="+cookie);
			connection.setRequestProperty("REFERER", "http://m.surbus.com/tiempo-espera");
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			connection.connect();
			Scanner scan = new Scanner(connection.getInputStream());
			StringBuilder strBuilder = new StringBuilder();
			while(scan.hasNextLine()){
				strBuilder.append(scan.nextLine());
			}
			scan.close();
			JSONObject json = new JSONObject(strBuilder.toString());
			Log.d("Almeribus",strBuilder.toString());
			boolean isSuccessful = json.getBoolean("success");
			if(isSuccessful){
				DataStorage.DBHelper.eliminarParadasLinea(numeroLinea);
				Linea l = new Linea(numeroLinea);
				JSONArray list = json.getJSONArray("list");
				Parada primeraParada = null;
				Parada paradaAnterior = null;
				for(int i = 0; i < list.length(); i++){
					JSONObject paradaJSON = list.getJSONObject(i);
					int numeroParada = paradaJSON.getInt("IdBusStop");
					String nombreParada = paradaJSON.getString("Name");
					Parada p = null;
					if(DataStorage.paradas.containsKey(numeroParada)){
						p = DataStorage.paradas.get(numeroParada);
						p.setNombre(nombreParada);
					} else {
						p = new Parada(numeroParada, nombreParada);
					}
					synchronized (DataStorage.DBHelper) {
						DataStorage.DBHelper.addInfoParada(numeroParada, nombreParada);
					}
					p.addLinea(l.getNumero());
					if(paradaAnterior != null) {
						p.setAnterior(paradaAnterior.getId(), numeroLinea);
					}
					
					if(i == 0) {
						primeraParada = p;
					} else if (i == list.length()-1) {
						primeraParada.setAnterior(p.getId(), numeroLinea);
						p.setSiguiente(primeraParada.getId(), numeroLinea);
					}
					
					if(paradaAnterior != null) {
						paradaAnterior.setSiguiente(p.getId(), numeroLinea);
					}
					
					paradaAnterior = p;
					synchronized (DataStorage.paradas) {
						if(DataStorage.paradas.containsKey(numeroParada)) {
							DataStorage.paradas.remove(numeroParada);
						}
						DataStorage.paradas.put(numeroParada, p);
					}
					
					l.addParada(p);
				}
				DataStorage.lineas.put(numeroLinea, l);
				for(Parada parada : l.getParadas()){
					synchronized (DataStorage.DBHelper) {
						try {
							DataStorage.DBHelper.addParadaLinea(parada.getId(), numeroLinea, parada.getSiguiente(numeroLinea));
						} catch (ParadaNotFoundException e) {
							DataStorage.DBHelper.addParadaLinea(parada.getId(), numeroLinea, 0);
						}
					}
				}
				return TODO_OK;
			} else {
				return ERROR_IO;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			return ERROR_IO;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ERROR_IO;
	}
	
	public static ResultTiempo calcularTiempo (Context context, int parada, int linea) {
		ResultTiempo result = new ResultTiempo();
		if(!isConnectionEnabled(context)) {
			Log.d("AlmeriBus", "No hay conexiÃ³n");
			result.setTiempo(ERROR_IO);
		}
		try {
			loadCookie();
			URL url = new URL(QUERY_ADDRESS_TIEMPO_PARADA + linea + "/" + parada + "/" +"3B5579C8FFD6");
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(15000);
			connection.setRequestProperty("REFERER", "http://m.surbus.com/tiempo-espera/");
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			connection.setRequestProperty("Cookie", "ASP.NET_SessionId="+cookie);
			Scanner scan = new Scanner(connection.getInputStream());
			StringBuilder strBuilder = new StringBuilder();
			while(scan.hasNextLine()){
				strBuilder.append(scan.nextLine());
			}
			scan.close();
			Log.d("Almeribus",strBuilder.toString());
			JSONObject json = new JSONObject(strBuilder.toString());
			boolean isSuccessful = json.getBoolean("success");
			int type = json.getInt("waitTimeType");
			if(isSuccessful && type > 0){
				int time = json.getInt("waitTime");
				if(time == Integer.MAX_VALUE) {
					time = NO_DATOS;
				}
				if(time <= 0) {
					time = 0;
				}
				result.setTiempo(time);
				result.setTiempoTexto(json.getString("waitTimeString"));
			} else {
				result.setTiempo(NO_DATOS);
			}
		} catch (Exception e) {
			Log.d("Almeribus",e.toString());
			result.setTiempo(ERROR_IO);
			return result;
		}
		return result;
	}
	
	public static String getMensajeDesarrollador() {
		String mensaje = null;
		try {
			URL url = new URL("http://arasthel.byethost14.com/almeribus/message.html?token="+new Random().nextInt(Integer.MAX_VALUE));
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(15000);
			Scanner scan = new Scanner(connection.getInputStream());
			StringBuilder strBuilder = new StringBuilder();
			while(scan.hasNextLine()){
				strBuilder.append(scan.nextLine());
			}
			scan.close();
			mensaje = strBuilder.toString();
		} catch (Exception e) {
			
		}
		return mensaje;
	}
	
	public static String getUltimaVersion() {
		String mensaje = null;
		try {
			URL url = new URL("http://arasthel.byethost14.com/almeribus/version.html?token="+new Random().nextInt(Integer.MAX_VALUE));
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(15000);
			Scanner scan = new Scanner(connection.getInputStream());
			StringBuilder strBuilder = new StringBuilder();
			while(scan.hasNextLine()){
				strBuilder.append(scan.nextLine());
			}
			scan.close();
			mensaje = strBuilder.toString();
		} catch (Exception e) {
			
		}
		return mensaje;
	}

	
	public static boolean isConnectionEnabled(Context context){
		boolean connected = false;
		if(context != null) {
			ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] infos = conManager.getAllNetworkInfo();
			for(NetworkInfo netInfo : infos){
				if(netInfo.isConnectedOrConnecting()){
					connected = true;
				}
			}
		}
		return connected;
	}
	
	public static ArrayList<Integer> buscarParadas(double[] misCoordenadas) {
		ArrayList<Integer> listaParadas = new ArrayList<Integer>();
		synchronized (DataStorage.paradas) {
			for(Entry<Integer,Parada> p : DataStorage.paradas.entrySet()){
	    		if(!p.getValue().getLineas().isEmpty() && p.getValue().getCoord() != null) {
					float[] results = new float[1];
					if(misCoordenadas == null) {
						results[0] = Integer.MAX_VALUE;
					} else {
						Location.distanceBetween(misCoordenadas[0], misCoordenadas[1], p.getValue().getCoord()[0], p.getValue().getCoord()[1], results);
					}
					if(results[0] > DataStorage.filtro.getDistancia()) {
						continue;
					}
					
					boolean contieneLinea = false;
						for(int j = 0; j < p.getValue().getLineas().size(); j++) {
	    					if(DataStorage.filtro.getLineas().contains(p.getValue().getLineas().get(j))) {
	    						contieneLinea = true;
	    						break;
	    					}
						}
					if(!contieneLinea) {
						continue;
					}
					listaParadas.add(p.getKey());
					
	    		}
			}
		}
		return listaParadas;
	}
	
	public static ArrayList<Integer> buscarParadas() {
		ArrayList<Integer> listaParadas = new ArrayList<Integer>();
		synchronized (DataStorage.paradas) {
			for(Entry<Integer,Parada> p : DataStorage.paradas.entrySet()){
	    		if(!p.getValue().getLineas().isEmpty() && p.getValue().getCoord() != null) {
					listaParadas.add(p.getKey());
	    		}
			}
		}
		return listaParadas;
	}
	
	public static ArrayList<Integer> buscarParadas(String texto) {
		ArrayList<Integer> listaParadas = new ArrayList<Integer>();
		synchronized (DataStorage.paradas) {
			for(Entry<Integer,Parada> p : DataStorage.paradas.entrySet()){
	    		if(!p.getValue().getLineas().isEmpty() && p.getValue().getCoord() != null) {
	    			if(p.getValue().getNombre().toLowerCase().contains(texto.toLowerCase())) {
	    				listaParadas.add(p.getKey());
	    			}
	    		}
			}
		}
		return listaParadas;
	}
	
	public static ArrayList<Integer> buscarParadasFavoritas() {
		ArrayList<Integer> listaParadas = new ArrayList<Integer>();
		listaParadas = DataStorage.DBHelper.buscarParadasFavoritas();
		return listaParadas;
	}
	
	public static ArrayList<Integer> buscarParadasFavoritas(String texto) {
		ArrayList<Integer> listaParadas = new ArrayList<Integer>();
		listaParadas = DataStorage.DBHelper.buscarParadasFavoritas();
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int parada : listaParadas) {
			synchronized (DataStorage.paradas) {
				if(DataStorage.paradas.get(parada).getNombre().toLowerCase().contains(texto.toLowerCase())) {
					result.add(parada);
				}
			}
		}
		return result;
	}
	
	private static int loadCookie() throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://m.surbus.com/tiempo-espera");
		HttpResponse response = httpClient.execute(get);
		HttpEntity entity = response.getEntity();
		
		if(entity == null) {
			return ERROR_IO;
		}
		
		Scanner scan = new Scanner(entity.getContent());
		StringBuilder strBuilder = new StringBuilder();
		while(scan.hasNextLine()){
			strBuilder.append(scan.nextLine());
		}
		scan.close();
		if(!strBuilder.toString().contains("id=\"blockResult\" class=\"messageResult\"")) {
			return MANTENIMIENTO;
		}
		
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		for(Cookie c : cookies) {
			if(c.getName().contains("ASP.NET_SessionId")) {
				cookie = c.getValue();
			}
		}
		return TODO_OK;
	}
}
