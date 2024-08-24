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


/**
 * @author paulhz
 * TODO file output method.
 */
/**
 * Provides storage and conversion utilities for CMYK colors.
 */
public class CMYKColor {
	protected double c;
	protected double m;
	protected double y;
	protected double k;
	
	/**
	 * Instantiates a CMYK color from c, m, y and k components represented as doubles in the range 0..1
	 * @param c
	 * @param m
	 * @param y
	 * @param k
	 */
	public CMYKColor(double c, double m, double y, double k) {
		this.c = c;
		this.m = m;
		this.y = y;
		this.k = k;
	}
	
	/**
	 * Instantiates a CMYK color
	 * @param cmyk   an array of four doubles in the range 0..1 representing c, m, y and k components
	 */
	public CMYKColor(double[] cmyk) {
		this(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
	}
	
	
	/**
	 * Instantiates a CMYK color with all components set to 0 (i.e., white)
	 */
	public CMYKColor() {
		this(0, 0, 0, 0);
	}


	/**
	 * @return the cyan component
	 */
	public double c() {
		return c;
	}
	/**
	 * @param c the cyan component (0..1) to set
	 */
	public void setC(double c) {
		this.c = c;
	}

	/**
	 * @return the magenta component
	 */
	public double m() {
		return m;
	}

	/**
	 * @param m the magenta componet (0..1) to set
	 */
	public void setM(double m) {
		this.m = m;
	}

	/**
	 * @return the yellow component
	 */
	public double y() {
		return y;
	}

	/**
	 * @param y the yellow component (0..1) to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the black component
	 */
	public double k() {
		return k;
	}

	/**
	 * @param k the black component (0..1) to set
	 */
	public void setK(double k) {
		this.k = k;
	}
	
	
	/**
	 * Uses the standard quick and dirty direct transform of RGB to CMYK. You can use it, but don't believe it. 
	 * @param red     the red component, in the range 0..1
	 * @param green   the green component, in the range 0..1
	 * @param blue    the blue component, in the range 0..1
	 * @return  the "equivalent" CMYK color
	 */
	public static CMYKColor rgb2cmyk(double red, double green, double blue) {
		double cyan = 1 - red;
		double magenta = 1 - green;
		double yellow = 1 - blue;
		double black = Math.min(Math.min(cyan, magenta), yellow);
		cyan = (cyan - black)/(1 - black);
		magenta = (magenta - black)/(1 - black);
		yellow = (yellow - black)/(1 - black);
		return new CMYKColor(cyan, magenta, yellow, black);
	}
	/**
	 * Uses the standard quick and dirty direct transform of RGB to CMYK. You can use it, but don't believe it. 
	 * @param rgb   an RGB color represented as an array of ints in the range 0..255
	 * @return  the "equivalent" CMYK color
	 */
	public static CMYKColor rgb2cmyk(int[] rgb) {
		return rgb2cmyk(rgb[0]/255.0, rgb[1]/255.0, rgb[2]/255.0);
	}
	/**
	 * Uses the standard quick and dirty direct transform of RGB to CMYK. You can use it, but don't believe it. 
	 * @param rgb   an RGB color represented as an array of doubles in the range 0..1
	 * @return  the "equivalent" CMYK color
	 */
	public static CMYKColor rgb2cmyk(double[] rgb) {
		return rgb2cmyk(rgb[0], rgb[1], rgb[2]);
	}
	/**
	 * Uses the standard quick and dirty direct transform of RGB to CMYK. You can use it, but don't believe it. 
	 * @param rgb   an RGB color
	 * @return  the "equivalent" CMYK color
	 */
	public static CMYKColor rgb2cmyk(RGBColor rgb) {
		return rgb2cmyk(rgb.r(), rgb.g(), rgb.b());
	}


}
