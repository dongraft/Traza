package cl.uchile.dcc.redes.traza.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import cl.uchile.dcc.redes.traza.utils.Localizer;
import cl.uchile.dcc.redes.traza.utils.Ping;
import cl.uchile.dcc.redes.traza.utils.PingResult;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class TrazaService extends Service {
	
	private final static Logger LOGGER = Logger.getLogger(PingResult.class.getName());
	
	private static boolean running;
	
	private String androidID;
	
	ConnectivityManager connectivityManager;
	TelephonyManager telephonyManager;
	
	ScheduledThreadPoolExecutor threadPool;
	TestRunnable testRunnable;
	
	private Localizer localizer;
	private Ping ping;
	private int company;
	
	private class TestRunnable implements Runnable {
		
		@Override
		public void run() {
			try {
				System.out.println("TRAZA RUN");
				NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
				if(activeNetwork.isConnectedOrConnecting() && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
					PingResult pingResult = ping.execute();
					int networkType = telephonyManager.getNetworkType();
					String data = String.format("%s %s %s %d %d\n", androidID, pingResult.toStringMin(), localizer.toStringMin(), networkType, company);
					boolean sent = postData(data);
					System.out.println("TRAZA sent ["+sent+"] :: "+data);
				}
			} catch(Exception e) {}
			System.out.println("TRAZA END RUN");
		}
	}
	
	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("TrazaThread", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		androidID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		
		threadPool = new ScheduledThreadPoolExecutor(1);
		
		connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
		testRunnable = new TestRunnable();
		localizer = new Localizer(this);
		ping = new Ping("nic.cl", 5);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		company = settings.getInt("company", 100);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		running = true;
		threadPool.scheduleWithFixedDelay(testRunnable, 3, 420, TimeUnit.SECONDS);
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		threadPool.shutdown();
		running = false;
	}
	
	public boolean postData(String data) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://traza.cadcc.cl/");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("data", data));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            @SuppressWarnings("unused")
			HttpResponse response = httpclient.execute(httppost);
            
        } catch (ClientProtocolException e) {
            LOGGER.warning("Couldn't POST data\n\t" + e.getMessage());
            return false;
        } catch (IOException e) {
        	LOGGER.warning("Couldn't POST data\n\t" + e.getMessage());
        	return false;
        }
        return true;
    }
	
	public static boolean isRunning() {return running;}
}
