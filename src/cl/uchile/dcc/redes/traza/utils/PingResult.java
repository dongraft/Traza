package cl.uchile.dcc.redes.traza.utils;

import java.util.regex.Pattern;

public class PingResult {
	
	private float min, avg, max, stdev;
	
	public PingResult(float min, float avg, float max, float stdev){
		this.min = min;
		this.avg = avg;
		this.max = max;
		this.stdev = stdev;
	}
	
	public PingResult(String out, String err){
		parse(out, err);
	}
	
	private void parse(String out, String err){
		Pattern patternHostName = Pattern.compile("^PING");
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
