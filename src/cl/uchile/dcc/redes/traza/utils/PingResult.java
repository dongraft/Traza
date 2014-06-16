package cl.uchile.dcc.redes.traza.utils;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.format.Time;

public class PingResult {
	
	private final static Logger LOGGER = Logger.getLogger(PingResult.class.getName());
	
	private Time timestamp;
	private String hostname, ipAddress;
	private int packetsTransmitted, packetsReceived, packetsLost, time;
	private float min, avg, max, stdev;
	
	public PingResult(){
	}
	
	public PingResult(String out){
		timestamp = new Time();
		timestamp.setToNow();
		parse(out);
	}
	
	public PingResult(String out, Time timestamp){
		this.timestamp = timestamp;
		parse(out);
	}
	
	
	public void parse(String out){
		
		//LOGGER.info("Will parse:\n"+out);
		
		String hostRegex = "PING\\s(\\S+)\\s\\((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\)\\s"; // PING\s(\S+)\s\((\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})\)\s
		String infoRegex = "(\\d+)\\spackets\\stransmitted.*?(\\d+)\\sreceived.*?(\\d+)%\\spacket\\sloss.*?(\\d+)ms"; // (\d+)\spackets\stransmitted.*?(\d+)\sreceived.*?(\d+)%\spacket\sloss.*?(\d+)ms
		String timesRegex = "(\\d+(?:\\.\\d+)){0,1}/(\\d+(?:\\.\\d+)){0,1}/(\\d+(?:\\.\\d+)){0,1}/(\\d+(?:\\.\\d+)){0,1}"; // (\d+(?:\.\d+)){0,1}/(\d+(?:\.\d+)){0,1}/(\d+(?:\.\d+)){0,1}/(\d+(?:\.\d+)){0,1}
		
		//LOGGER.info("Regex are:\nhostRegex: "+hostRegex+"\nresultRegex: "+timesRegex);
		
		Pattern hostPattern = Pattern.compile(hostRegex, Pattern.MULTILINE);
		Pattern infoPattern = Pattern.compile(infoRegex, Pattern.MULTILINE);
		Pattern resultPattern = Pattern.compile(timesRegex, Pattern.MULTILINE);
		
		// Find matches
		Matcher hostMatcher = hostPattern.matcher(out);
		Matcher infoMatcher = infoPattern.matcher(out);
		Matcher resultMatcher = resultPattern.matcher(out);
		
		if(hostMatcher.find() && infoMatcher.find() && resultMatcher.find()){
			hostname = hostMatcher.group(1);
			ipAddress = hostMatcher.group(2);
			
			packetsTransmitted = Integer.valueOf(infoMatcher.group(1));
			packetsReceived = Integer.valueOf(infoMatcher.group(2));
			packetsLost = Integer.valueOf(infoMatcher.group(3));
			time = Integer.valueOf(infoMatcher.group(4));
		
			min = Float.valueOf(resultMatcher.group(1));
			avg = Float.valueOf(resultMatcher.group(2));
			max = Float.valueOf(resultMatcher.group(3));
			stdev = Float.valueOf(resultMatcher.group(4));
		} else{
			LOGGER.severe("Matchers returned no matches");
		}
	}
	
	public String toString(){
		String format = "Timestamp: %s\nHostname: %s\nIP Address: %s\nPackets transmitted: %d\nPackets received: %d\nPackets lost: %d%%\nTime: %dms\nmin: %.3f\navg: %.3f\nmax: %.3f\nstdev: %.3f";
		return String.format(format, timestamp.format2445(), hostname, ipAddress, packetsTransmitted, packetsReceived, packetsLost, time, min, avg, max, stdev);
	}
	
	public String toStringMin(){
		String format = "%s %s %s %d %d %d %d %.3f %.3f %.3f %.3f";
		return String.format(format, timestamp.format2445(), hostname, ipAddress, packetsTransmitted, packetsReceived, packetsLost, time, min, avg, max, stdev);
	}

	public String getHostname() {
		return hostname;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public float getMin() {
		return min;
	}


	public float getAvg() {
		return avg;
	}


	public float getMax() {
		return max;
	}


	public float getStdev() {
		return stdev;
	}
	
	
}
