package cl.uchile.dcc.redes.traza.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Localizer {
	
	private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    
    private final class TrazaLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location changedLocation) {
			location = changedLocation;
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
    	
    }
    
    public Localizer(Context context) {
    	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    	locationListener = new TrazaLocationListener();
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    	location = null;
    }
    
    public String toString() {
    	if(location != null) {
    		String format = "Latitude: %.6f\nLogitude: %.6f\nAccuracy: %.2f";
    		return String.format(format, location.getLatitude(), location.getLongitude(), location.getAccuracy());
    	}
    	else
    		return "Acquiring location...";
    }
    
    public String toStringMin() {
    	String format = "%.6f %.6f %.2f";
    	if(location != null) {
    		return String.format(format, location.getLatitude(), location.getLongitude(), location.getAccuracy());
    	}
    	else {
    		return "null null null";
    	}
    }
    
    public double getLattitude(){return location.getLatitude();}
    public double getLongitude(){return location.getLongitude();}
    public float getAccuracy(){return location.getAccuracy();}
    
}