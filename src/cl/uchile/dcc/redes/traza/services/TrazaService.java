package cl.uchile.dcc.redes.traza.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cl.uchile.dcc.redes.traza.utils.Localizer;
import cl.uchile.dcc.redes.traza.utils.Ping;
import cl.uchile.dcc.redes.traza.utils.PingResult;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

public class TrazaService extends Service {
	
	String FILENAME = "data";
	FileOutputStream fileOutputStream;
	
	ScheduledThreadPoolExecutor threadPool;
	TestRunnable testRunnable;
	private Looper serviceLooper;
	private ServiceHandler serviceHandler;
	
	private Localizer localizer;
	private Ping ping;
	
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			
		}
	}
	
	private class TestRunnable implements Runnable {
		
		int counter;
		
		public TestRunnable() {
			counter = 0;
		}
		
		@Override
		public void run() {
			
			PingResult pingResult = ping.execute();
			String data = String.format("TRAZA %d :: %s %s\n", counter, pingResult.toStringMin(), localizer.toStringMin());
			try{
				fileOutputStream.write(data.getBytes());
				System.out.println(String.format("Traza %d", counter));
			} catch(Exception e) {
				System.out.println(String.format("FAILSAFE Couldn't write to file: %s", e.getMessage()));
				System.out.println(String.format("Traza %d", counter));
				System.out.println(pingResult.toString());
				System.out.println(localizer.toString());
			}
			
			counter++;
		}
	}
	
	@Override
	public void onCreate() {
		
		HandlerThread thread = new HandlerThread("TrazaThread", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		// Get the HandlerThread's Looper and use it for our Handler
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
		
		testRunnable = new TestRunnable();
		threadPool = new ScheduledThreadPoolExecutor(1);
		
		try {
			fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		localizer = new Localizer(this);
		ping = new Ping("anakena.dcc.uchile.cl");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		threadPool.scheduleAtFixedRate(testRunnable, 0, 3, TimeUnit.SECONDS);
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
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}
