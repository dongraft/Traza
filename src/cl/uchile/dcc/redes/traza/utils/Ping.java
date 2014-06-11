package cl.uchile.dcc.redes.traza.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Ping {
	
	private Runtime runtime;
	
	private String host;
	private int times;
	
	public Ping(String host, int times) {
		this.host = host;
		this.times = times;
		
		runtime = Runtime.getRuntime();
	}
	
	public Ping(String host){
		this(host, 3);
	}
	
	public PingResult execute(){
		
		String[] progArg = new String[4];
		progArg[0] = "/system/bin/ping";
		progArg[1] = "-q";
		progArg[2] = "-c"+this.times;
		progArg[3] = host;
		
		PingResult result = new PingResult();
		
		try{
			// Execute process
			Process pingProcess = runtime.exec(progArg);
			int exitCode = pingProcess.waitFor();
			// Read output
			BufferedReader stdoutBr = new BufferedReader(new InputStreamReader(pingProcess.getInputStream()));
	        BufferedReader stderrBr = new BufferedReader(new InputStreamReader(pingProcess.getErrorStream()));
	        
	        
	        // Get output
	        String temp;
	        String stdout = "";
	        String stderr = ""; // Could be useful someday
	        
	        if(exitCode == 0){
	        
		        while ((temp = stdoutBr.readLine()) != null) {
		            stdout += temp + "\n";
		        }
		        while ((temp = stderrBr.readLine()) != null) {
		            stderr += temp + "\n";
		        }	
		        
		        result.parse(stdout);
	        }
	        
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("ERROR");
		}
		
		return result;
	}
}
