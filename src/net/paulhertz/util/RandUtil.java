/*
 * Copyright (c) 2011, Paul Hertz This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 3.0 of the License, or (at your option) any later version.
 * http://www.gnu.org/licenses/lgpl.html This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 * 
 */

package net.paulhertz.util;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author paulhz
 */
/**
 * Implements a few useful random number methods, maintains an internal random number generator. 
 * You will only need to instantiate this class if you want to maintain different random number 
 * generators (for example, initialized with different seeds). 
 */
public class RandUtil {
	// random number generator
	private Random randGenerator;
	private Integer Nrand;
	private double Arand;
	private double gaussAdd;
	private double gaussFac;

	/**
	 * Returns the internal random number generator.
	 * @return an instance of Random
	 */
	public Random randGenerator() {
		if (null == randGenerator) {
			randGenerator = new Random();
		}
		return randGenerator;
	}

	/**
	 * Sets the internal random number generator to the supplied instance of Random.
	 * @param newRandGenerator   an instance of Random
	 */
	public void setRandGenerator(Random newRandGenerator) {
		randGenerator = newRandGenerator;
	}

	/**
	 * Sets the seed value for the internal random number generator.
	 * @param seed
	 */
	public void setRandSeed(long seed) {
		randGenerator().setSeed(seed);
	}

	/******** shuffle ********/

	/**
	 * Shuffles an array of integers into random order.
	 * Implements Richard Durstenfeld's version of the Fisher-Yates algorithm, popularized by Donald Knuth.
	 * see http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
	 * @param intArray an array of <code>int</code>s, changed on exit
	 */
	public void shuffle(int[] intArray) {
		for (int lastPlace = intArray.length - 1; lastPlace > 0; lastPlace--) {
			// Choose a random location from 0..lastPlace
			int randLoc = (randGenerator().nextInt(lastPlace + 1));
			// Swap items in locations randLoc and lastPlace
			int temp = intArray[randLoc];
			intArray[randLoc] = intArray[lastPlace];
			intArray[lastPlace] = temp;
		}
	}

	/**
	 * Shuffles an array of floats into random order.
	 * @param floatArray an array of <code>float</code>s, changed on exit
	 */
	public void shuffle(float[] floatArray) {
		for (int lastPlace = floatArray.length - 1; lastPlace > 0; lastPlace--) {
			// Choose a random location from 0..lastPlace
			int randLoc = (randGenerator().nextInt(lastPlace + 1));
			// Swap items in locations randLoc and lastPlace
			float temp = floatArray[randLoc];
			floatArray[randLoc] = floatArray[lastPlace];
			floatArray[lastPlace] = temp;
		}
	}

	/**
	 * Shuffles an array of doubles into random order.
	 * @param doubleArray an array of <code>double</code>s, changed on exit
	 */
	public void shuffle(double[] doubleArray) {
		for (int lastPlace = doubleArray.length - 1; lastPlace > 0; lastPlace--) {
			// Choose a random location from 0..lastPlace
			int randLoc = (randGenerator().nextInt(lastPlace + 1));
			// Swap items in locations randLoc and lastPlace
			double temp = doubleArray[randLoc];
			doubleArray[randLoc] = doubleArray[lastPlace];
			doubleArray[lastPlace] = temp;
		}
	}

	/**
	 * Shuffles an <code>ArrayList</code> into random order.
	 * @param arr an <code>ArrayList</code>, changed on exit
	 */
	public void shuffle(ArrayList<Object> arr) {
		for (int lastPlace = arr.size() - 1; lastPlace > 0; lastPlace--) {
			// Choose a random location from 0..lastPlace
			int randLoc = (randGenerator().nextInt(lastPlace + 1));
			// Swap items in locations randLoc and lastPlace
			Object temp = arr.get(randLoc);
			arr.set(randLoc, arr.get(lastPlace));
			arr.set(lastPlace, temp);
		}
	}


	/******** random in range ********/

	/**
	 * Returns a random <code>int</code> in a range from low to high (inclusive).
	 * @param low     lower bound of range
	 * @param high    upper bound of range
	 * @return a random integer from low to high, inclusive.
	 */
	public int randomInRange(int low, int high) {
		if (high == low) {
			return high;
		}
		if (high < low) {
			int temp = high;
			high = low;
			low = temp;
		}
		int range = high - low;
		return low + randGenerator().nextInt(range + 1);
	}
	/**
	 * Returns a random <code>float</code> in a range from low to high (exclusive).
	 * @param low     lower bound of range
	 * @param high    upper bound of range
	 * @return a random integer from low to high (exclusive).
	 */
	public float randomInRange(float low, float high) {
		if (high == low) {
			return high;
		}
		if (high < low) {
			float temp = high;
			high = low;
			low = temp;
		}
		float range = high - low;
		return (low + range * randGenerator().nextFloat());
	}
	/**
	 * Returns a random <code>double</code> in a range from low to high (exclusive).
	 * @param low    lower bound of range
	 * @param high    upper bound of range
	 * @return a random integer from low to high, inclusive.
	 */
	public double randomInRange(double low, double high) {
		if (high == low) {
			return high;
		}
		if (high < low) {
			double temp = high;
			high = low;
			low = temp;
		}
		double range = high - low;
		return (low + range * randGenerator().nextFloat());
	}


	/******** random element from list ********/

	/**
	 * Returns a random <code>int</code> from an array.
	 * @param arr   an array of <code>int</code>
	 * @return a randomly selected value from the supplied array
	 */
	public int randomElement(int[] arr) {
		int index = randomInRange(0, arr.length - 1);
		return arr[index];
	}


	/**
	 * Returns a random <code>float</code> from an array.
	 * @param arr   an array of <code>float</code>
	 * @return a randomly selected value from the supplied array
	 */
	public float randomElement(float[] arr) {
		int index = randomInRange(0, arr.length - 1);
		return arr[index];
	}


	/**
	 * Returns a random <code>double</code> from an array.
	 * @param arr   an array of <code>double</code>
	 * @return a randomly selected value from the supplied array
	 */
	public double randomElement(double[] arr) {
		int index = randomInRange(0, arr.length - 1);
		return arr[index];
	}


	/**
	 * Returns a random element from a raw <code>ArrayList</code>.
	 * @param arr   and <code>ArrayList</code> of untyped objects.
	 * @return a randomly selected value from the supplied array
	 */
	public Object randomElement(@SuppressWarnings("rawtypes") ArrayList arr) {
		int index = randomInRange(0, arr.size() - 1);
		return arr.get(index);
	}


	/******** Gauss ********/


	/**
	 * Returns a Gaussian variable using Java library call to <code>Random.nextGaussian</code>.
	 * @return a Gaussian-distributed random number with mean 0.0 and variance 1.0
	 */
	public double gauss() {
		return randGenerator().nextGaussian();
	}

	/**
	 * Returns a Gaussian variable using a Java library call to <code>Random.nextGaussian</code>.
	 * @param mean
	 * @param variance
	 * @return a Gaussian-distributed random number with mean <code>mean</code> and variance <code>variance</code>
	 */
	public double gauss(double mean, double variance) {
		return randGenerator().nextGaussian() * Math.sqrt(variance) + mean;
	}


	/**
	 * Returns a Gaussian variable using standard algorithm from Crandall,
	 * <em>Pascal Applications for the Sciences</em>
	 * 
	 * @param mean
	 * @param variance
	 * @return a Gaussian-distributed random number
	 */
	public double calcGauss(double mean, double variance) {
		double u, v, x;
		double gauss;
		do {
			// assign u and v the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
			u = randGenerator().nextDouble();
			v = randGenerator().nextDouble();
			if (0.0 == u)
				u = 0.000000001;
			x = 2.0 * (v - 0.5) / u;
		} while (x * x > (-4 * Math.log(u)));
		gauss = x * Math.sqrt(variance) + mean;
		return gauss;
	}


	/**
	 * returns a Gaussian variable using Peitgen and Saupe's algorithm from 
	 * <em>The Science of Fractal Images</em>
	 * 
	 * @return a random number that approximates a Gaussian distribution
	 */
	public double quickGauss() {
		if (null == Nrand) {
			// number of samples to use in generating number
			Nrand = new Integer(4); 
			// upper bound from random generator
			Arand = 1;
			// parameter for linear transformation
			gaussAdd = Math.sqrt(3 * Nrand);
			// parameter for linear transformation
			gaussFac = 2 * gaussAdd / (Nrand * Arand);
		}
		double sum = 0;
		for (int i = 0; i < Nrand; i++) {
			sum += randGenerator().nextDouble();
		}
		return gaussFac * sum - gaussAdd;
	}

	/**
	 * returns a quick approximation to a Gaussian variable using Peitgen and Saupe's 
	 * algorithm from <em>The Science of Fractal Images</em>
	 * 
	 * @param mean       mean value of the distribution
	 * @param variance   variance of the distribution
	 * @return a random number that over repeated calls approximates a Gaussian distribution
	 */
	public double quickGauss(double mean, double variance) {
		if (null == Nrand) {
			// number of samples to use in generating number
			Nrand = new Integer(4); 
			// upper bound from random generator
			Arand = 1.0;
			// parameter for linear transformation
			gaussAdd = Math.sqrt(3 * Nrand);
			// parameter for linear transformation
			gaussFac = 2 * gaussAdd / (Nrand * Arand);
		}
		double sum = 0;
		for (int i = 0; i < Nrand; i++) {
			sum += randGenerator().nextDouble();
		}
		return (gaussFac * sum - gaussAdd) * Math.sqrt(variance) + mean;
	}
}
