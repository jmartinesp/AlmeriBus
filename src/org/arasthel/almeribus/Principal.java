/*******************************************************************************
 * Copyright (c) 2013 Jorge Mart抧 Espinosa (Arasthel).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jorge Mart抧 Espinosa (Arasthel) - initial API and implementation
 ******************************************************************************/
package org.arasthel.almeribus;

import java.sql.Date;

import org.arasthel.almeribus.fragments.AvisoInfoFragment;
import org.arasthel.almeribus.fragments.InfoFragment;
import org.arasthel.almeribus.fragments.InicioFragment;
import org.arasthel.almeribus.fragments.MapaParadasFragment;
import org.arasthel.almeribus.fragments.MostrarListadosParadasFragment;
import org.arasthel.almeribus.fragments.TarifaFragment;
import org.arasthel.almeribus.utils.DataStorage;
import org.arasthel.almeribus.utils.DatabaseHelper;
import org.arasthel.almeribus.utils.LoadFromWeb;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.model.LatLng;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class Principal extends SlidingFragmentActivity {
	
	private boolean necesitaActualizacion;
	private boolean loaded;
	private boolean active;
	private BroadcastReceiver receiver;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		// We ask ABS to have an indeterminate progress bar on the action bar
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_principal);
		setBehindContentView(R.layout.menu);
		
		//active = false;
		active = true;
		
		SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindScrollScale(1);
        //menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindWidth((int) (getWindowManager().getDefaultDisplay().getWidth()*0.9));
        menu.setFadeDegree(0.35f);
        
        menu.setOnOpenedListener(new OnOpenedListener() {
			
			@Override
			public void onOpened() {
				getSlidingMenu().invalidate();
				
			}
		});
        
        menu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				getSlidingMenu().invalidate();
			}
		});
        
        setCloseOnBack(true);
        setSlidingActionBarEnabled(true);
        
        
        LinearLayout abrirListadoParadas = (LinearLayout) menu.findViewById(R.id.abrir_listado_paradas);
        abrirListadoParadas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				abrirListadoParadas();
				
			}
		});
        
        LinearLayout abrirMapaParadas = (LinearLayout) menu.findViewById(R.id.abrir_mapa_paradas);
        abrirMapaParadas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				abrirMapaParadas();
				
			}
		});
        
        LinearLayout abrirTarifas = (LinearLayout) menu.findViewById(R.id.abrir_listado_tarifas);
        abrirTarifas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				abrirTarifas();
				
			}
		});
        
        View info = menu.findViewById(R.id.info);
        info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				abrirInfo();
			}
		});
        
        View avisos = menu.findViewById(R.id.avisos);
        avisos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				abrirAvisos();
			}
		});
		
        InicioFragment mFragment = new InicioFragment();
		if(savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragment).commit();
		} else {
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).commit();
		}
		
		//loaded = false;
		loaded = true;
		
		//new MostrarMensajeTask().execute();
		//new BuscarActualizacionesTask().execute();
	}
	
	public void cargarParadas() {
		new CargarInfoParadas().execute(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.buscar_parada_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		long ultimaActualizacion = sp.getLong("ultima_actualizacion", 0);
		Date fechaUltima = new Date(ultimaActualizacion);
		long nuevoTiempo = System.currentTimeMillis();
		Date nuevaFecha = new Date(nuevoTiempo);
		
		necesitaActualizacion = false;
		
		if(nuevaFecha.getDay() != fechaUltima.getDay() || 
				nuevaFecha.getMonth() != fechaUltima.getMonth() ||
				nuevaFecha.getYear() != fechaUltima.getYear()) {
			necesitaActualizacion = true;
		}
				
		// We load the bus stop info from Surbus server
		cargarParadas();
		if(savedInstanceState == null && !necesitaActualizacion && !loaded) {
        	savedInstanceState = new Bundle();
        	savedInstanceState.putBoolean("SlidingActivityHelper.open", true);
        }
		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			toggleSlidingMenu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class CargarInfoParadas extends AsyncTask<Context, Boolean, Boolean> {
		
		private boolean mantenimiento = false;

		@Override
		protected Boolean doInBackground(Context... arg0) {
			DataStorage.DBHelper = new DatabaseHelper(Principal.this);
			DataStorage.DBHelper.copyDB("DBParadas.sqlite");
			DataStorage.DBHelper.getDB();
			DataStorage.DBHelper.leerParadasDB();
			boolean result = false;
			if(necesitaActualizacion && !loaded) {
				DataStorage.DBHelper.eliminarParadasLineas();
				for(int i = 0; i < DataStorage.numLineas.length; i++) {
					int estado = LoadFromWeb.cargarParadasLinea(Principal.this, DataStorage.numLineas[i]);
					if(estado != LoadFromWeb.TODO_OK) {
						result = false;
					} else {
						result = result | true;
					}
					if(estado == LoadFromWeb.MANTENIMIENTO) {
						mantenimiento = true;
					}
				}
			} else {
				return true;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			if(result) {
				if(necesitaActualizacion) {
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Principal.this);
					long nuevoTiempo = System.currentTimeMillis();
					sp.edit().putLong("ultima_actualizacion", nuevoTiempo).commit();
					Toast.makeText(Principal.this, "Informaci贸n de las l铆neas cargada", Toast.LENGTH_SHORT).show();
				}
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				Intent i = new Intent();
				i.setAction("org.arasthel.UPDATE_FINISHED");
				sendBroadcast(i);
			} else {
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				Intent i = new Intent();
				i.setAction("org.arasthel.UPDATE_FAILED");
				sendBroadcast(i);
				active = true;
				if(mantenimiento) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Principal.this);
					alertBuilder.setTitle("Error recibiendo datos de Surbus");
					alertBuilder.setMessage("Parece que la p谩gina de \"Tiempos de espera\" de la web m贸vil de Surbus no funciona, es muy posible que la aplicaci贸n no funcione totalmente debido a esto.\n\nSi quiere, puede enviarles un correo para preguntarles qu茅 ocurre. Puede ayudar a que el problema se solucione antes.");
					alertBuilder.setPositiveButton("Enviar correo", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent i = new Intent(Intent.ACTION_SEND);
							i.setType("message/rfc822");
							i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@surbus.com"});
							i.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre el servicio");
							try{
								startActivity(Intent.createChooser(i, "Enviar correo con..."));
							} catch (ActivityNotFoundException e) {
								AlertDialog.Builder builder = new AlertDialog.Builder(Principal.this);
								
								builder.setMessage("No tiene clientes de correo electr贸nico instalados. El correo de Surbus es:\ninfo@surbus.com");
								builder.setPositiveButton("Aceptar", null);
								builder.show();
							}
						}
					});
					alertBuilder.setNegativeButton("Cerrar", null);
					if(Principal.this != null) {
						alertBuilder.show();
					}
				}
			}
			
			super.onPostExecute(result);
		}
		
	}
	
	
	private class MostrarMensajeTask extends AsyncTask<Void, Void, String> {
				
		@Override
		protected String doInBackground(Void... arg0) {
			String mensaje = LoadFromWeb.getMensajeDesarrollador();
			return mensaje;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result != null && result.length() > 0) {
				AlertDialog.Builder builder = new Builder(Principal.this);
				builder.setTitle("Mensaje del desarrollador");
				builder.setMessage(result);
				builder.setPositiveButton("Aceptar", null);
				if(Principal.this != null) {
					builder.show();
				}
			}
			super.onPostExecute(result);
		}
	}
	
	private class BuscarActualizacionesTask extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... arg0) {
			String mensaje = LoadFromWeb.getUltimaVersion();
			return mensaje;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result != null && result.length() > 0) {
				int version = Integer.parseInt(result);
				if(Principal.this != null) {
					int currentVersion;
					try {
						currentVersion = getPackageManager().getPackageInfo(Principal.this.getPackageName(), 0).versionCode;
						if(version > currentVersion) {
							AlertDialog.Builder builder = new Builder(Principal.this);
							builder.setTitle("Hay una actualizaci贸n de la aplicaci贸n");
							builder.setMessage("Hay una nueva versi贸n de la aplicaci贸n, es aconsejable que la instale para asegurar un buen funcionamiento");
							builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									Intent i = new Intent(android.content.Intent.ACTION_VIEW);
									i.setData(Uri.parse("market://details?id="+Principal.this.getPackageName()));
									startActivity(i);
								}
							});
							builder.setNegativeButton("Ahora no", null);
							builder.show();
						}
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			super.onPostExecute(result);
		}
	}

   
	public void toggleSlidingMenu() {
		getSlidingMenu().toggle();
	}
	
	public void abrirListadoParadas() {
		MostrarListadosParadasFragment listaParadasF = new MostrarListadosParadasFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listaParadasF).commit();
		getSlidingMenu().toggle();
		onSlidingMenuItemSelected();
	}
	
	public void abrirMapaParadas() {
		MapaParadasFragment mapaParadas = MapaParadasFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapaParadas).commit();
		getSlidingMenu().toggle();
		onSlidingMenuItemSelected();
	}
	
	public void abrirMapaParadas(LatLng coord, boolean necesario) {
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if(f != null && f instanceof MapaParadasFragment) {
			((MapaParadasFragment) f).centrarMapa(coord);
		} else {
			if(necesario) {
				MapaParadasFragment mapaParadas = MapaParadasFragment.newInstance(coord);
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapaParadas).commit();
			}
		}
		onSlidingMenuItemSelected();
	}
	
	public void abrirTarifas() {
		TarifaFragment tarifas = new TarifaFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tarifas).commit();
		getSlidingMenu().toggle();
		onSlidingMenuItemSelected();
	}
	
	public void abrirInfo() {
		InfoFragment info = new InfoFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
		getSlidingMenu().toggle();
		onSlidingMenuItemSelected();
	}
	
	public void abrirAvisos() {
		AvisoInfoFragment aviso = new AvisoInfoFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, aviso).commit();
		getSlidingMenu().toggle();
		onSlidingMenuItemSelected();
	}
	
	public void onSlidingMenuItemSelected(){
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}
	
	@Override
	public void onBackPressed() {
		int stackCount = getSupportFragmentManager().getBackStackEntryCount();
		if(stackCount == 0 && !getSlidingMenu().isMenuShowing()) {
			if(loaded || active) {
				getSlidingMenu().toggle();
			} else {
				finish();
			}
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onStart() {
		receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				loaded = true;
			}
		};
		registerReceiver(receiver, new IntentFilter("org.arasthel.UPDATE_FINISHED"));
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		if(receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onStop();
	}
	
}
