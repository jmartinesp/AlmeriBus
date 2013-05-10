package org.arasthel.almeribus.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import org.arasthel.almeribus.utils.ParadaNotFoundException;

public class Parada implements Serializable{

	private double[] coord;
	private int id;
	private String nombre;
	private TreeMap<Integer,Integer> anterior;
	private TreeMap<Integer,Integer> siguiente;
	private ArrayList<Integer> lineas;
	private Parada repetida;
	
	
	public Parada(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
		anterior = new TreeMap<Integer, Integer>();
		siguiente = new TreeMap<Integer, Integer>();
		this.lineas = new ArrayList<Integer>();
	}
	
	public Parada(int id, String nombre, String coord){
		this.id = id;
		this.nombre = nombre;
		anterior = new TreeMap<Integer, Integer>();
		siguiente = new TreeMap<Integer, Integer>();
		String[] coordenadas = coord.split("[,]");
		this.coord = new double[] {Double.parseDouble(coordenadas[0]), Double.parseDouble(coordenadas[1])};
		this.lineas = new ArrayList<Integer>();
	}

	public double[] getCoord() {
		return coord;
	}

	public void setCoord(double[] coord) {
		this.coord = coord;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getAnterior(int linea) throws ParadaNotFoundException {
		if(!anterior.containsKey(linea)) {
			throw new ParadaNotFoundException();
		}
		return anterior.get(linea);
	}

	public void setAnterior(int anterior, int linea) {
		this.anterior.put(linea, anterior);
	}

	public int getSiguiente(int linea) throws ParadaNotFoundException {
		if(!siguiente.containsKey(linea)) {
			throw new ParadaNotFoundException();
		}
		return siguiente.get(linea);
	}

	public void setSiguiente(int siguiente, int linea) {
		this.siguiente.put(linea, siguiente);
	}

	public int getLinea(int i) {
		return lineas.get(i);
	}
	
	public ArrayList<Integer> getLineas() {
		return lineas;
	}
	
	public void addLinea(int linea) {
		if(!lineas.contains(linea)) {
			this.lineas.add(linea);
		}
	}
	
	public boolean removeLinea(int i) {
		if(lineas.size() > i) {
			lineas.remove(i);
			return true;
		}
		return false;
	}
	
	public void clearLineas() {
		this.lineas.clear();
	}
	
	public Parada getRepetida() {
		return repetida;
	}

	public void setRepetida(Parada repetida) {
		this.repetida = repetida;
	}

	@Override
	public String toString() {
		if(coord == null) {
			return id + ", "+nombre;
		} else {
			return id + ", "+nombre+" | "+coord[0]+","+coord[1];
		}
		
	}
}
