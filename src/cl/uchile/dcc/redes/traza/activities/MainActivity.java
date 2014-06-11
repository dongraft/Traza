package cl.uchile.dcc.redes.traza.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cl.uchile.dcc.redes.traza.R;
import cl.uchile.dcc.redes.traza.utils.Ping;
import cl.uchile.dcc.redes.traza.utils.PingResult;


public class MainActivity extends Activity {
	private LocationManager locationManager;
    private LocationListener locationListener;
    private String latitude;
    private String longitude;
    private String accuracy;
    public final static String EXTRA_MESSAGE = "cl.uchile.dcc.redes.traza.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                accuracy = String.valueOf(location.getAccuracy());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        
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
        
        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText("Trazando");
        setContentView(textView);        
    }
    
    /** button_ping onClick listener */
    public void doPing(View view){
        
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        
        // The ping
        Ping ping = new Ping("anakena.dcc.uchile.cl");
        PingResult result = ping.execute();
        // Muestra la salida en el TextView
        TextView tvDisplay = (TextView) findViewById(R.id.tv_display);
        String ret = result.toString();
        ret = ret + "\n\n" + latitude +  "," + longitude + "\nAccuracy: "+accuracy+"%";
        tvDisplay.setText(ret);
        locationManager.removeUpdates(locationListener);
        //Se cae con post data buuu
//        postData(latitude, longitude, accuracy);
    }
    
    public void postData(String lat, String lng, String accu) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://traza.cadcc.cl/");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("lat", lat));
            nameValuePairs.add(new BasicNameValuePair("lng", lng));
            nameValuePairs.add(new BasicNameValuePair("accu", accu));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            @SuppressWarnings("unused")
			HttpResponse response = httpclient.execute(httppost);
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
    
 

}