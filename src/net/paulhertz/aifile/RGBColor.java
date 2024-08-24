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
 */
/**
 * Provides storage and conversion utilities for an RGBColor in floating point format 
 * and range used by AIFileWriter. Not to be confused with Processing RGB. 
 */
public class RGBColor {
	protected double r;
	protected double g;
	protected double b;
	
	/**
	 * Instantiates an RGB color from r, g and b components represented as doubles in the range 0..1
	 * @param r   red component
	 * @param g   green component
	 * @param b   blue component
	 */
	public RGBColor(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/**
	 * Instantiates an RGB color from r, g and b components represented as ints in the range 0..255,
	 * the format used in Processing and most bitmap graphics applications.
	 * @param r   red component
	 * @param g   green component
	 * @param b   blue component
	 */
	public RGBColor(int r, int g, int b) {
		this(r/255.0, g/255.0, b/255.0);
	}
	
	/**
	 * Instantiates an RGB color.
	 * @param rgb   an array with three entries for r, g, and b components represented as doubles in the range 0..1
	 */
	public RGBColor(double[] rgb) {
		this(rgb[0], rgb[1], rgb[2]);
	}
		
	/**
	 * Instantiates an RGB color with all components set to 0 (i.e., black)
	 */
	public RGBColor() {
		this(0, 0, 0);
	}


	/**
	 * @return the red component
	 */
	public double r() {
		return r;
	}
	/**
	 * @param r the red component (0..1) to set
	 */
	public void setR(double r) {
		this.r = r;
	}

	/**
	 * @return the green component
	 */
	public double g() {
		return g;
	}

	/**
	 * @param g the green component (0..1) to set
	 */
	public void setG(double g) {
		this.g = g;
	}

	/**
	 * @return the blue component
	 */
	public double b() {
		return b;
	}

	/**
	 * @param b the blue component (0..1) to set
	 */
	public void setB(double b) {
		this.b = b;
	}
	
	
	/**
	 * Uses the standard quick and dirty direct transform of CMYK to RGB. You can use it, but don't believe it. 
	 * @param cyan      the cyan component, in the range 0..1
	 * @param magenta   the magenta component, in the range 0..1
	 * @param yellow    the yellow component, in the range 0..1
	 * @param black     the black component, in the range 0..1
	 * @return   the "equivalent" RGB color
	 */
	public static RGBColor cmyk2rgb(double cyan, double magenta, double yellow, double black) {
		cyan = Math.min(1, cyan * (1 - black) + black);
		magenta = Math.min(1, magenta * (1 - black) + black);
		yellow = Math.min(1, yellow * (1 - black) + black);
		return new RGBColor(1 - cyan, 1 - magenta, 1 - yellow);
	}
	/**
	 * Uses the standard quick and dirty direct transform of CMYK to RGB. You can use it, but don't believe it. 
	 * @param cmyk   an array of four doubles in the range 0..1, representing c, m, y and k components
	 * @return   the "equivalent" RGB color
	 */
	public static RGBColor cmyk2rgb(double[] cmyk) {
		return cmyk2rgb(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
	}
	/**
	 * Uses the standard quick and dirty direct transform of CMYK to RGB. You can use it, but don't believe it. 
	 * @param cmyk   a CMYKColor
	 * @return   the "equivalent" RGB color
	 */
	public static RGBColor cmyk2rgb(CMYKColor cmyk) {
		return cmyk2rgb(cmyk.c(), cmyk.m(), cmyk.y(), cmyk.k());
	}


}
