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
package net.paulhertz.aifile;

import java.io.PrintWriter;
import java.util.*;

import net.paulhertz.util.*;


// TODO utility functions for sorting, etc.
// TODO support CMYK
// TODO use float values in colors models
// TODO constants for black, white, middle gray



/**
 * Maintains a set of Processing-style colors: alpha, red, green and blue stored as 4 bytes in an 32-bit int.
 * Does not permit duplicate color entries.
 * Provides methods for adding colors in other formats, and some utility functions.
 * 
 */
public class Palette {
	protected LinkedHashSet<Integer> colorSet;
	protected static RandUtil rand;
	
	/**
	 * Creates a Palette with no colors
	 */
	public Palette() {
		this.colorSet = new LinkedHashSet<Integer>();
	}
	
	/**
	 * Creates a Palette instance from another Palette's colorSet.
	 * @param palette   a Palette, source for the colors used in this Palette
	 */
	public Palette(Palette palette) {
		this.colorSet = new LinkedHashSet<Integer>();
		this.colorSet.addAll(palette.getColorSet());
	}
	
	
	/**
	 * Writes this Palette in Adobe Illustrator file format. If you call this yourself,
	 * call it right after outputting the header (perhaps with a call to 
	 * {@link net.paulhertz.aifile.DocumentComponent#writeHeader(PrintWriter) writeHeader()}).
	 * @param pw   PrintWriter for output to file
	 */
	public void write(PrintWriter pw) {
		AIFileWriter.beginPalette(pw);
		Iterator<Integer> iter = this.colorSet.iterator();
		while(iter.hasNext()) {
			int[] farb = argbComponents(iter.next());
			AIFileWriter.paletteRGBCell(farb[1]/255.0, farb[2]/255.0, farb[3]/255.0, pw);
		}
		AIFileWriter.endPalette(pw);
	}
	
	
	/**
	 * Adds black, white and gray to the palette.
	 */
	public void addBlackWhiteGray() {
		colorSet.add(Integer.valueOf(composeColor(0, 0, 0, 255)));
		colorSet.add(Integer.valueOf(composeColor(255, 255, 255, 255)));
		colorSet.add(Integer.valueOf(composeColor(127, 127, 127, 255)));
	}
	
	/**
	 * Transforms an {@link net.paulhertz.aifile.RGBColor RGBColor} to a 32-bit </code>int</code>
	 * and adds it to the colorSet of this Palette.
	 * @param rgb   an {@link RGBColor RGBColor}
	 */
	public void addColor(RGBColor rgb) {
		this.colorSet.add(Integer.valueOf(composeColor( 
				(int) Math.round(rgb.r * 255), 
				(int) Math.round(rgb.g * 255), 
				(int) Math.round(rgb.b * 255), 
				255)));
	}
	
	/**
	 * Transforms a {@link net.paulhertz.aifile.CMYKColor CMYKColor} to a 32-bit </code>int</code>
	 * and adds it to the colorSet of this Palette.
	 * @param cmyk   a {@link CMYKColor CMYKColor}
	 */
	public void addColor(CMYKColor cmyk) {
		RGBColor rgb = RGBColor.cmyk2rgb(cmyk);
		this.addColor(rgb);
	}
	
	/**
	 * Adds a Processing color to the colorSet of this Palette.
	 * @param argb   a Processing-format color, alpha, red, green and blue stored as 4 bytes in an 32-bit int
	 */
	public void addColor(int argb) {
		this.colorSet.add(Integer.valueOf(argb));
	}
	
	/**
	 * Adds colors in argbColors to the colorSet of this Palette. Skips colors that are already in the palette.
	 * @param argbColors   an ArrayList of Processing-format colors (32 bit integers)
	 */
	public void addColors(ArrayList<Integer> argbColors) {
		for (Integer argb : argbColors) {
			this.addColor(argb.intValue());
		}
	}
	
	/**
	 * Adds colors in argbColors to the colorSet of this Palette. Skips colors that are already in the palette.
	 * @param argbColors   an array of Processing-format colors (32 bit integers)
	 */
	public void addColors(int[] argbColors) {
		for (int farb : argbColors) {
			this.colorSet.add(farb);
		}
	}
	
	/**
	 * Returns the internal storage for colors.
	 * @return   the internal storage for colors, a LinkedHashSet
	 */
	public LinkedHashSet<Integer> getColorSet() {
		return this.colorSet;
	}
	
	/**
	 * Returns the colors in the colorSet of this Palette as an array of integers. These
	 * can be treated as Processing-format colors, with ARGB channels.
	 * @return   colors in palette as an array of <code>int</code>s
	 */
	public int[] getColors() {
		Integer[] arr = new Integer[this.colorSet.size()];
		arr = this.colorSet.toArray(arr);
		int i = 0;
		int[] result = new int[arr.length];
		for (Integer num : arr) {
			result[i++] = num.intValue();
		}
		return result;
	}
	
	public int getColor(int index) {
		Integer[] arr = new Integer[this.colorSet.size()];
		arr = this.colorSet.toArray(arr);
		return arr[index];
	}
	
	/**
	 * Breaks a Processing color into A, R, G and B values in an array.
	 * @param argb   a Processing color as a 32-bit integer 
	 * @return       an array of integers in the range 0..255 for each color component: {A, R, G, B}
	 */
	public static int[] argbComponents(int argb) {
		int[] comp = new int[4];
		comp[0] = (argb >> 24) & 0xFF;	// alpha
		comp[1] = (argb >> 16) & 0xFF;  // Faster way of getting red(argb)
		comp[2] = (argb >> 8) & 0xFF;   // Faster way of getting green(argb)
		comp[3] = argb & 0xFF;          // Faster way of getting blue(argb)
		return comp;
	}
	/**
	 * Breaks a Processing color into R, G and B values in an array.
	 * @param argb   a Processing color as a 32-bit integer 
	 * @return       an array of integers in the range 0..255 for 3 primary color components: {R, G, B}
	 */
	public static int[] rgbComponents(int argb) {
		int[] comp = new int[3];
		comp[0] = (argb >> 16) & 0xFF;  // Faster way of getting red(argb)
		comp[1] = (argb >> 8) & 0xFF;   // Faster way of getting green(argb)
		comp[2] = argb & 0xFF;          // Faster way of getting blue(argb)
		return comp;
	}

	/**
	 * Creates a Processing ARGB color from r, g, b, and alpha channel values. Note the order
	 * of arguments, the same as the Processing color(value1, value2, value3, alpha) method. 
	 * @param r   red component 0..255
	 * @param g   green component 0..255
	 * @param b   blue component 0..255
	 * @param a   alpha component 0..255
	 * @return    a 32-bit integer with bytes in Processing format ARGB.
	 */
	public static int composeColor(int r, int g, int b, int a) {
		return a << 24 | r << 16 | g << 8 | b;
	}
	/**
	 * Creates a Processing ARGB color from a grayscale value. Alpha will be set to 255.
	 * @param gray   a grayscale value 0..255
	 * @return       an int compatible with a Processing color
	 */
	public static int composeColor(int gray) {
		return 255 << 24 | gray << 16 | gray << 8 | gray;
	}
	
	
	/**
	 * Provides lazy initialization of rand, an instance of {@link RandUtil RandUtil}
	 * @return   instance of RandUtil used by this class
	 */
	protected static RandUtil rand() {
		if (null == rand) {
			rand = new RandUtil();
		}
		return rand;
	}
	
	/**
	 * Creates a random color from randomly selected values in an array. 
	 * @param c   an array of integer values
	 * @return    a color with a = 255 and r, g and b randomly selected from the array values
	 */
	public static int randColor(int[] c) {
		return composeColor(rand().randomElement(c), rand().randomElement(c), rand().randomElement(c), 255);
	}
	/**
	 * Returns a random Processing color.
	 * @return   a color with a = 255 and r, g and b set to random values in 0..255
	 */
	public static int randColor() {
		return composeColor(rand().randomInRange(0, 255), rand().randomInRange(0, 255), rand().randomInRange(0, 255), 255);
	}


	/**
	 * Returns all unique permutations of the color channels of a supplied color.
	 * @param argb   a Processing-format color, a, r, g and b as bytes in a 32-bit integer
	 * @return       an array of Processing-format colors with all unique permutations of r, g and b, where a = 255
	 */
	public static int[] colorPermutation(int argb) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int[] chip = rgbComponents(argb);
		int[] perm = {0,1,2};
		for (int i = 0; i < 6; i++) {
			Integer farb = Integer.valueOf(composeColor(chip[perm[0]], chip[perm[1]], chip[perm[2]], 255));
			if (!temp.contains(farb)) temp.add(farb);
			Permutator.nextPerm(perm);     
		}
		int[] result = new int[temp.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = temp.get(i).intValue();
		}
		return result;
	}
	
	/**
	 * Returns the complement of a color.
	 * @param argb   a Processing-format color, 32-bit integer a, r, g and b channels.
	 * @return		 the rgb complement of the supplied color, same alpha
	 */
	public static int complement(int argb) {
		int[] temp = argbComponents(argb);
		return Palette.composeColor(255 - temp[1], 255 - temp[2], 255 - temp[3], temp[0]);
	}
	
	
	/**
	 * Converts a Processing color to an {@link net.paulhertz.aifile.RGBColor RGBColor}.
	 * @param argb   a Processing color
	 * @return       an RGBColor
	 */
	public static RGBColor int2rgb(int argb) {
		int[] elems = Palette.rgbComponents(argb);
		return new RGBColor(elems[0], elems[1], elems[2]);
	}
	/**
	 * Converts an {@link net.paulhertz.aifile.RGBColor RGBColor} to a Processing color.
	 * @param rgb
	 * @return   a Processing color
	 */
	public static int rgb2int(RGBColor rgb) {
		return Palette.composeColor((int)Math.round(rgb.r), (int)Math.round(rgb.g), (int)Math.round(rgb.b), 255);
	}
	/**
	 * Converts a {@link net.paulhertz.aifile.CMYKColor CMYKColor} to a Processing color.
	 * @param cmyk
	 * @return   a Processing color
	 */
	public static int cmyk2int(CMYKColor cmyk) {
		RGBColor rgb = RGBColor.cmyk2rgb(cmyk);
		return Palette.composeColor((int)Math.round(rgb.r), (int)Math.round(rgb.g), (int)Math.round(rgb.b), 255);
	}

	/* (non-Javadoc)
	 * returns a list of rgb colors in the palette
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Palette with rgb color values \n");
		int i = 0;
		int[] colors = this.getColors();
		for(int color : colors) {
			int[] comps = Palette.rgbComponents(color);
			sb.append(i++ + ": ("+ comps[0] +", "+ comps[1] +", "+ comps[2] +")\n");
		}
		return sb.toString();
	}
	
	

}
