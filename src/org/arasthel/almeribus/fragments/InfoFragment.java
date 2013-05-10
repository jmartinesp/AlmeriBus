package org.arasthel.almeribus.fragments;

import org.arasthel.almeribus.R;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class InfoFragment extends SherlockFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.about_layout, null);
		TextView version = (TextView) v.findViewById(R.id.version);
		try {
			version.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			version.setText("No hay información de la versión");
		}
		return v;
	}
	
	@Override
	public void onResume() {
		getActivity().setTitle("Sobre la aplicación");
		((SlidingFragmentActivity) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		View sendMail = getView().findViewById(R.id.email);
		sendMail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"angel.arasthel@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, "Sobre Almeribus");
				try{
					startActivity(Intent.createChooser(i, "Enviar correo con..."));
				} catch (ActivityNotFoundException e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					
					builder.setMessage("No tiene clientes de correo electrónico instalados. El correo del desarrollador es:\nangel.arasthel@gmail.com");
					builder.setPositiveButton("Aceptar", null);
					builder.show();
				}
			}
		});
		
		View twitter = getView().findViewById(R.id.twitter);
		twitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://twitter.com/arasthel92"));
				startActivity(i);
			}
		});
		super.onResume();
	}

}
