package cl.uchile.dcc.redes.traza.activities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cl.uchile.dcc.redes.traza.R;
import cl.uchile.dcc.redes.traza.R.id;
import cl.uchile.dcc.redes.traza.R.layout;
import cl.uchile.dcc.redes.traza.R.menu;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "cl.uchile.dcc.redes.traza.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        
        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText("Trazando");
        turnGPSOn();
        String currentLocation = getMyCurrentLocation();
        turnGPSOff();
        textView.setText(currentLocation);
        // Set the text view as the activity layout
        setContentView(textView);
        
        //startActivity(intent);
    }
    
    /** button_ping onClick listener */
    public void doPing(View view){
    	System.out.println(" executeCammand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
        	// Ejecuta el ping
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            // Procesa la salida
            String temp;
            String stdout = "";
            String stderr = "";
            BufferedReader stdoutBr = new BufferedReader(new InputStreamReader(mIpAddrProcess.getInputStream()));
            BufferedReader stderrBr = new BufferedReader(new InputStreamReader(mIpAddrProcess.getErrorStream()));
            while ((temp = stdoutBr.readLine()) != null) {
                stdout += temp + "\n";
            }
            while ((temp = stderrBr.readLine()) != null) {
                stderr += temp + "\n";
            }
            // Muestra la salida en el TextView
            TextView tvDisplay = (TextView) findViewById(R.id.tv_display);
            tvDisplay.setText(stdout);
            // Muestra la salida en la consola
            if(mExitValue==0){
                System.out.println("MOCO SUCCESS ping returned:\nstd: "+stdout+"\nerr: "+stderr);
            }else{
            	System.out.println("MOCO FAIL ping returned:\nstd: "+stdout+"\nerr: "+stderr);
            }
            // Destruye el proces
            mIpAddrProcess.destroy();
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
    }
    
    public void turnGPSOn(){
        try
        {
       
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

       
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
        }
        catch (Exception e) {
           
        }
    }
    
    public void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
    
    String getMyCurrentLocation() {
        
        
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();
       
       
         try{gps_enabled=locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
           try{network_enabled=locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

            //don't start listeners if no provider is enabled
            //if(!gps_enabled && !network_enabled)
                //return false;

            if(gps_enabled){
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
               
            }
           
           
            if(gps_enabled){
                location=locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               
               
            }
           
 
            if(network_enabled && location==null){
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
               
            }
       
       
            if(network_enabled && location==null)    {
                location=locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
           
            }
       
        if (location != null) {
           
            MyLat = location.getLatitude();
            MyLong = location.getLongitude();

       
        } else {
            Location loc= getLastKnownLocation(this);
            if (loc != null) {
               
                MyLat = loc.getLatitude();
                MyLong = loc.getLongitude();
               

            }
        }
        locManager.removeUpdates(locListener); // removes the periodic updates from location listener to //avoid battery drainage. If you want to get location at the periodic intervals call this method using //pending intent.
       
        try
        {
// Getting address from found locations.
        Geocoder geocoder;
       
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
         addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

        StateName= addresses.get(0).getAdminArea();
        CityName = addresses.get(0).getLocality();
        CountryName = addresses.get(0).getCountryName();
        // you can get more details other than this . like country code, state code, etc.
       
       
        System.out.println(" StateName " + StateName);
        System.out.println(" CityName " + CityName);
        System.out.println(" CountryName " + CountryName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        turnGPSOn();
        return ""+MyLat+","+MyLong+"\n\n"+StateName+"\n"+CityName+"\n"+CountryName;
    }
   
    // Location listener class. to get location.
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            if (location != null) {
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
   
    private boolean gps_enabled=false;
    private boolean network_enabled=false;
    Location location;
   
    Double MyLat, MyLong;
    String CityName="";
    String StateName="";
    String CountryName="";
   
// below method to get the last remembered location. because we don't get locations all the times .At some instances we are unable to get the location from GPS. so at that moment it will show us the last stored location. 

    public static Location getLastKnownLocation(Context context){
        Location location = null;
        LocationManager locationmanager = (LocationManager)context.getSystemService("location");
        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do
        {
            //System.out.println("---------------------------------------------------------------------");
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            //if(i != 0 && !locationmanager.isProviderEnabled(s))
            if(i != false && !locationmanager.isProviderEnabled(s))
                continue;
           // System.out.println("provider ===> "+s);
            Location location1 = locationmanager.getLastKnownLocation(s);
            if(location1 == null)
                continue;
            if(location != null)
            {
                //System.out.println("location ===> "+location);
                //System.out.println("location1 ===> "+location);
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if(f >= f1)
                {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if(l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
           // System.out.println("location  out ===> "+location);
            //System.out.println("location1 out===> "+location);
            i = locationmanager.isProviderEnabled(s);
           // System.out.println("---------------------------------------------------------------------");
        } while(true);
        return location;
    }

}