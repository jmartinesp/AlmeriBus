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
package org.arasthel.almeribus.interfaces;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.arasthel.almeribus.utils.DataStorage;

public class FiltroBuscarParadas extends Observable{

	private Set<Integer> lineas;
	
	private int proximidad;
	
	public FiltroBuscarParadas() {
		lineas = new HashSet<Integer>();
		for(int linea : DataStorage.numLineas) {
			lineas.add(linea);
		}
		proximidad = Integer.MAX_VALUE;
	}

	public Set<Integer> getLineas() {
		return lineas;
	}

	public void setLineas(Set<Integer> lineas) {
		this.lineas = lineas;
		setChanged();
		notifyObservers();
	}

	public void setProximidad(int proximidad) {
		this.proximidad = proximidad;
		setChanged();
		notifyObservers();
	}
	
	public void addLinea(int linea) {
		lineas.add(linea);
		setChanged();
		notifyObservers();
	}
	
	public boolean removeLinea(int linea) {
		boolean removed = false;
		if(lineas.contains(linea)) {
			removed = lineas.remove(linea);
			setChanged();
			notifyObservers();
		}
		return removed;
	}
	
	public void addLineas(Collection<Integer> lineas) {
		this.lineas.addAll(lineas);
		setChanged();
		notifyObservers();
	}
	
	public void setDistacia(int metros) {
		proximidad = metros;
		setChanged();
		notifyObservers();
	}
	
	public int getDistancia() {
		return proximidad;
	}
	
	@Override
	public String toString() {
		String result = "Proximidad: "+proximidad+" ;; ";
		for(int l : lineas) {
			result += "| "+l+" ";
		}
		return result;
	}
	
}
