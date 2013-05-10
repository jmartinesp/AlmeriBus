package org.arasthel.almeribus.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class GPS {
	
	private LocationManager locManager;
	private LocationListener locListener;
	
	private int ESPERA = 1000;
	
	public double calcularDistancia(double[] pt1, double[] pt2, char unit) {
		double latA = pt1[1];
		double longA = pt1[0];
		double latB = pt2[1];
		double longB = pt2[0];
		double theta = longA - longB;
		double dist = Math.sin(deg2rad(latA)) * Math.sin(deg2rad(latB)) + Math.cos(deg2rad(latA)) * Math.cos(deg2rad(latB)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		} else if (unit == 'M'){
			dist = dist * 1609.344;
		}
		return (dist);
	}
	
	private double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		  return (rad * 180 / Math.PI);
	}

	public void stopGPS(){
		locManager.removeUpdates(locListener);
	}
	
	public void startGPS(Context context){
		locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled, net_enabled;
		net_enabled = false;
		gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = con.getAllNetworkInfo();
        for(int i = 0; i < redes.length; i++){
        	if(redes[i].isConnected()){
        		net_enabled = true;
        	}
        }
		if(gps_enabled||net_enabled){
			locListener = new LocationChangeListener(context);
			if(net_enabled)
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, ESPERA, 10, locListener);
			if(gps_enabled)
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ESPERA, 10, locListener);
		}
		else{
			//Sin senial GPS. TO-DO algo por la patria
		}
	}
	
	private class LocationChangeListener implements LocationListener{
		
		private Context context;
		
		public LocationChangeListener(Context context) {
			this.context = context;
		}
		
		@Override
		public void onLocationChanged(final Location location) {
			if(location != null){
				if(location.getLongitude() != 0 || location.getLatitude() != 0){
					Intent i = new Intent();
					i.setAction("org.arasthel.almeribus.COORDENADAS_RECIBIDAS");
					i.putExtra("longitud", location.getLongitude());
					i.putExtra("latitud", location.getLatitude());
					context.sendBroadcast(i);
				}
			}
		
            
			/*Thread geocoderThread = new Thread(){
				@Override public void run() {
		            Geocoder geocoder = new Geocoder(Principal.myContext(), Locale.getDefault());   
		            Geocoder geo = new Geocoder(Principal.myContext(),Locale.getDefault());
		            List<Address> direcciones = geo.getFromLocation(location.getAltitude(), location.getLongitude(), 10);
				}
				
			};*/
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("GPS","Provider disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("GPS","Provider enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("GPS","Provider: "+provider+", is "+ status);
		}	
	}
	
}
