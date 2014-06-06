package cl.uchile.dcc.redes.traza.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import android.widget.TextView;
import cl.uchile.dcc.redes.traza.R;

public class Ping {
	
	private final Runtime runtime = Runtime.getRuntime();
	
	private URL url;
	private int times;
	
	public Ping(URL url, int times) {
		this.url = url;
		this.times = times;
		
		runtime = Runtime.getRuntime();
	}
	
	public PingResult execute(){
		
		String[] progArg = new String[4];
		progArg[0] = "/system/bin/ping";
		progArg[1] = "-q";
		progArg[2] = "-c"+this.times;
		progArg[3] = this.url.getHost();
		
		try{
			// Execute process
			Process pingProcess = runtime.exec(progArg);
			int exitCode = pingProcess.waitFor();
			// Read output
			BufferedReader stdoutBr = new BufferedReader(new InputStreamReader(pingProcess.getInputStream()));
	        BufferedReader stderrBr = new BufferedReader(new InputStreamReader(pingProcess.getErrorStream()));
	        // Parse output
	        String temp;
	        String stdout = "";
	        String stderr = "";
	        
	        while ((temp = stdoutBr.readLine()) != null) {
	            stdout += temp + "\n";
	        }
	        while ((temp = stderrBr.readLine()) != null) {
	            stderr += temp + "\n";
	        }
	        
	        pingProcess.destroy();
	        
	        
			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("ERROR");
		}
	}
}
