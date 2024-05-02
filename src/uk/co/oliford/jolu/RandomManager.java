package uk.co.oliford.jolu;

import java.util.Random;

/**
 * A class to make it possible to control the random number generation globally
 * in the COLT package. 
 * 
 * <p></p>
 * <em><b>THE ABOVE IS NO LONGER TRUE, INTERNALLY JAVA.UTIL.RANDOM IS USED TO GET RID OF COLT</b></em>
 */
public class RandomManager {
	
	Random rnd;
	private static RandomManager instance = null;
	public static final double LOGZERO = Double.NEGATIVE_INFINITY;
	
	public RandomManager() {
		rnd = new Random(System.currentTimeMillis());
	}
	
	public void setSeed(int seed) {
		rnd = new Random(seed);
	}
	
	public static RandomManager instance() {
		if (instance == null) {
			instance = new RandomManager();
		}
		return instance;
	}
	
	public double nextNormal(double mean, double standardDeviation) {
		return rnd.nextGaussian() * standardDeviation + mean;
	}
	
	public double nextUniform(double from, double to) {
		return rnd.nextDouble() * (to - from) + from;
	}
}
