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

import java.io.Serializable;
import java.util.ArrayList;

public class Linea implements Serializable{
	
	private ArrayList<Parada> paradas;
	private int numero;
	
	public Linea (int numero){
		this.numero = numero;
		paradas = new ArrayList<Parada>();
	}

	public ArrayList<Parada> getParadas() {
		return paradas;
	}

	public void setParadas(ArrayList<Parada> paradas) {
		this.paradas = paradas;
	}

	public void addParada(Parada p){
		if(!paradas.contains(p)){
			paradas.add(p);
		}
	}
	
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}
	
}
