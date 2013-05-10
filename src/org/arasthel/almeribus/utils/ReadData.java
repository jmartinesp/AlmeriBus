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
import java.io.InputStream;
import java.util.Scanner;

import org.arasthel.almeribus.interfaces.Parada;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ReadData {

	public static void leerParadas(Context context) throws IOException, JSONException{
		InputStream is = context.getAssets().open("paradas.json");
		StringBuilder builder = new StringBuilder();
		Scanner scan = new Scanner(is);
		while(scan.hasNext()){
			builder.append(scan.nextLine());
		}
		scan.close();
		is.close();
		JSONObject json = new JSONObject(builder.toString());
		JSONArray paradas = json.getJSONArray("paradas");
		Parada p;
		JSONObject paradaJson;
		DataStorage.paradas.clear();
		for(int i = 0; i < paradas.length(); i++){
			paradaJson = paradas.getJSONObject(i);
			String coord = paradaJson.optString("coord");
			if(coord.length() == 0) {
				p = new Parada(paradaJson.getInt("id"), paradaJson.getString("nombre"));
			} else {
				p = new Parada(paradaJson.getInt("id"), paradaJson.getString("nombre"), paradaJson.getString("coord"));
			}			
			Log.d("PARADA",p.toString());
			DataStorage.paradas.put(p.getId(), p);
		}
	}
	
}
