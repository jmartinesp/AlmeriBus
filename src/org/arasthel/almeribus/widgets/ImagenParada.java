package org.arasthel.almeribus.widgets;

import org.arasthel.almeribus.utils.DataStorage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ImagenParada extends ImageView implements OnClickListener{
	
	private String archivoBase;
	private boolean activado = true;
	private int linea;

	public ImagenParada(Context context) {
		super(context);
		setOnClickListener(this);
	}
	
	public ImagenParada(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public String getArchivoBase() {
		return archivoBase;
	}

	public void setArchivoBase(String archivoBase) {
		this.archivoBase = archivoBase;
		linea = this.getId();
		activado = DataStorage.filtro.getLineas().contains(linea);
		mostrarDrawable(activado);
	}

	@Override
	public void onClick(View arg0) {
		if(activado) {
			if(DataStorage.filtro.getLineas().contains(linea)) {
				Log.d("LINEAS", linea+": "+"OFF");
				DataStorage.filtro.removeLinea(linea);
			}
		} else {
			DataStorage.filtro.addLinea(linea);
			Log.d("LINEAS", linea+": "+"ON");
		}
		activado = !activado;
		mostrarDrawable(activado);
	}
	
	private void mostrarDrawable(boolean activado) {
		int resId = 0;
		if(activado) {
			resId = getResources().getIdentifier(archivoBase, "drawable", getContext().getPackageName());
		} else {
			resId = getResources().getIdentifier(archivoBase+"_disabled", "drawable", getContext().getPackageName());
		}
		Drawable d = getResources().getDrawable(resId);
		setImageDrawable(d);
	}

	public boolean isActivado() {
		return activado;
	}

	public void setActivado(boolean activado) {
		this.activado = activado;
		mostrarDrawable(activado);
	}
	
	
}
