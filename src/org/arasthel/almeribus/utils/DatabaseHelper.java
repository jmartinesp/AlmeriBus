package org.arasthel.almeribus.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.arasthel.almeribus.interfaces.Linea;
import org.arasthel.almeribus.interfaces.Parada;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper {
	
	private Context context;
	private ParadasSQLiteHelper paradasHelper;
	private SQLiteDatabase db;
	private ProgressDialog copiandoDB;
	
	public DatabaseHelper(Context context) {
		this.context = context;
	}
	
	public void copyDB(String dbName) {
		ParadasSQLiteHelper paradasHelper = new ParadasSQLiteHelper(context, dbName, null, 1);
		
		SQLiteDatabase db = paradasHelper.getWritableDatabase();
		db.close();
		try {
			InputStream is = context.getAssets().open(dbName);
			String destino = context.getDatabasePath(dbName).getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(destino);
			byte[] buf = new byte[1024];
			int len;
			while((len = is.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getDB(){
		try {
			paradasHelper = new ParadasSQLiteHelper(context, "DBParadas.sqlite", null, 1);
			
			db = paradasHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public void addFavorito(int parada) {
		try {
			db.execSQL(" INSERT INTO ParadasFavoritas VALUES ("+parada+")");
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public void removeFavorito(int parada) {
		try {
			db.execSQL(" DELETE FROM ParadasFavoritas WHERE idParada = "+parada);
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public boolean isFavorito(int parada) {
		try {
			Cursor c = db.rawQuery(" SELECT idParada FROM ParadasFavoritas WHERE idParada = "+parada, null);
			
			if(c.moveToFirst() && c.getInt(c.getColumnIndex("idParada")) > 0) {
				c.close();
				return true;
			} else {
				c.close();
				return false;
			}
		} catch (SQLiteException e) {
			errorBD(e);
			return false;
		}
	}
	
	public void addParadaLinea(int parada, int linea, int siguiente) {
		try {
			db.execSQL(" INSERT INTO Linea_has_Parada VALUES ("+linea+", "+parada+", "+siguiente+")");
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public void eliminarParadasLineas() {
		try {
			db.execSQL(" DELETE FROM Linea_has_Parada WHERE 1");
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public void eliminarParadasLinea(int linea) {
		try {
			db.execSQL(" DELETE FROM Linea_has_Parada WHERE linea="+linea);
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public ArrayList<Integer> buscarParadasFavoritas() {
		ArrayList<Integer> paradasFavoritas = new ArrayList<Integer>();
		try {
			Cursor c = db.rawQuery(" SELECT idParada FROM ParadasFavoritas", null);
			
			if(c.moveToFirst()) {
				do {
					paradasFavoritas.add(c.getInt(c.getColumnIndex("idParada")));	
				} while (c.moveToNext());
			}
			c.close();
		} catch (SQLiteException e) {
			errorBD(e);
		}
		return paradasFavoritas;
	}
	
	public void leerParadasDB() {
		Parada parada;
		
		Cursor paradasCursor = db.rawQuery(" SELECT id, longitud, latitud, nombre FROM Paradas", null);
		try{
			if(paradasCursor.moveToFirst()) {
				do {
					int id = paradasCursor.getInt(paradasCursor.getColumnIndex("id"));
					String nombre = paradasCursor.getString(paradasCursor.getColumnIndex("nombre"));
					Cursor lineaParadaCursor = db.rawQuery(" SELECT * FROM Linea_has_Parada WHERE idParada = "+id, null);
					ArrayList<Integer> lineas = new ArrayList<Integer>();
					parada = new Parada(id, nombre);
					parada.setCoord(new double[] {paradasCursor.getDouble(paradasCursor.getColumnIndex("longitud")), paradasCursor.getDouble(paradasCursor.getColumnIndex("latitud"))});
					if(lineaParadaCursor.moveToFirst()) {
						do{
							int linea = lineaParadaCursor.getInt(lineaParadaCursor.getColumnIndex("linea"));
							lineas.add(linea);
						} while(lineaParadaCursor.moveToNext());
					}
					lineaParadaCursor.close();
					for(int numLinea: lineas) {
						Linea linea = null;
						if(DataStorage.lineas.containsKey(numLinea)) {
							linea = DataStorage.lineas.get(numLinea);
							linea.addParada(parada);
						} else {
							linea = new Linea(numLinea);
							linea.addParada(parada);
						}
						parada.addLinea(linea.getNumero());
						DataStorage.lineas.put(numLinea, linea);
					}
					synchronized (DataStorage.paradas) {
						DataStorage.paradas.put(id, parada);
					}
				} while(paradasCursor.moveToNext());
			}
			paradasCursor.close();
			for(Parada p : DataStorage.paradas.values()) {
				Cursor lineaParadaCursor = db.rawQuery(" SELECT * FROM Linea_has_Parada WHERE idParada = "+p.getId(), null);
				if(lineaParadaCursor.moveToFirst()) {
					do{
						int linea = lineaParadaCursor.getInt(lineaParadaCursor.getColumnIndex("linea"));
						int siguiente = lineaParadaCursor.getInt(lineaParadaCursor.getColumnIndex("idSiguiente"));
						p.setSiguiente(siguiente, linea);
					} while(lineaParadaCursor.moveToNext());
				}
				lineaParadaCursor.close();
				
			}
			Cursor paradasRepetidas = db.rawQuery(" SELECT * FROM ParadasRepetidas", null);
			if(paradasRepetidas.moveToFirst()) {
				do {
					int idA = paradasRepetidas.getInt(paradasRepetidas.getColumnIndex("ID_Parada_A"));
					int idB = paradasRepetidas.getInt(paradasRepetidas.getColumnIndex("ID_Parada_B"));
					synchronized (DataStorage.paradas) {
						DataStorage.paradas.get(idA).setRepetida(DataStorage.paradas.get(idB));
						DataStorage.paradas.get(idB).setRepetida(DataStorage.paradas.get(idA));
					}
				} while(paradasRepetidas.moveToNext());
			}
			paradasRepetidas.close();
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}
	
	public void addInfoParada(int parada, String nombre) {
		try {
			db.execSQL(" UPDATE Paradas SET nombre='"+nombre+"' WHERE id = "+parada);
		} catch (SQLiteException e) {
			errorBD(e);
		}
	}

	public void createDatabase() {
		if(db != null) {
			try {
				Scanner scan = new Scanner(context.getAssets().open("paradas.txt"));
				scan.useDelimiter(" ");
				while(scan.hasNextLine()) {
					String line = scan.nextLine();
					String[] lineaSplit = line.split(" ");
					int id = Integer.parseInt(lineaSplit[0]);
					double latitud = Double.parseDouble(lineaSplit[1]);
					double longitud = Double.parseDouble(lineaSplit[2]);
					db.execSQL("INSERT INTO Paradas (id, latitud, longitud) VALUES ("+id+", "+latitud+", "+longitud+")");
				}
			} catch (IOException e) {
				errorBD(e);
			}
			db.close();
			
		}
	}
	
	private class ParadasSQLiteHelper extends SQLiteOpenHelper {

		private String createTable = "CREATE TABLE Paradas (id INTEGER, nombre TEXT, longitud DOUBLE, latitud DOUBLE)";
		
		public ParadasSQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int lastVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS Paradas");
			db.execSQL(createTable);
		}
		
	}
	
	public void errorBD(final Exception e) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Error al cargar la BD");
		builder.setMessage("La base de datos está corrupta. Se copiará de nuevo la base de datos para intentar arreglar el error.");
		builder.setPositiveButton("Aceptar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				copiandoDB = new ProgressDialog(context);
				copiandoDB.setMessage("Copiando base de datos...");
				copiandoDB.setIndeterminate(true);
				copiandoDB.setCancelable(false);
				copiandoDB.show();
				Runnable r = new Runnable() {
					
					@Override
					public void run() {
						context.deleteDatabase("DBParadas.sqlite");
						copyDB("DBParadas.sqlite");
						getDB();
						leerParadasDB();
						copiandoDB.dismiss();
					}
				};
				new Thread(r).start();
				
			}
		});
		builder.setNegativeButton("Cerrar aplicación", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				((Activity) context).finish();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				((Activity) context).finish();
			}
		});
		builder.show();
	}
	
}
