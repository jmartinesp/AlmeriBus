package org.arasthel.almeribus.utils;

import java.util.TreeMap;

import org.arasthel.almeribus.interfaces.FiltroBuscarParadas;
import org.arasthel.almeribus.interfaces.Linea;
import org.arasthel.almeribus.interfaces.Parada;

public class DataStorage {

	public static TreeMap<Integer, Parada> paradas = new TreeMap<Integer, Parada>();
	public static TreeMap<Integer, Linea> lineas = new TreeMap<Integer, Linea>();
	
	public static DatabaseHelper DBHelper;
	
	public static int[] numLineas = new int[] {1,2,3,5,6,7,11,12,15,18,20,21,22,30};
	//public static int[] numLineas = new int[] {1,2,3,5,6,7,11,12,15,18,20,21,22,30,91};
	
	public static FiltroBuscarParadas filtro = new FiltroBuscarParadas();
	
}
