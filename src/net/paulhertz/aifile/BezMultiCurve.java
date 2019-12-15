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

import net.paulhertz.aifile.BezMultiCurve;
import net.paulhertz.aifile.BezShape;

import processing.core.PApplet;

/**
 * Provides factory methods to create an open path consisting of multiple Bezier curves.
 * 
 * <p>
 * Example shows how to draw BezMultiLine and BezMultiCurve objects, also how to use a DocumentComponent
 * as a display list, how to turn layers on and off, and how to set and export opacity. The signature of 
 * the fade in and fade out methods gets altered during export to JavaDocs, which apparently ignores
 * code in angle brackets. The sample file is fine. Here's the right signature:<br />
 * {@code public boolean fadeOut(ArrayList<? extends BezShape> shapes, int step)}
 * </p>
 * @example DrawMulti 
 */
public class BezMultiCurve extends BezShape {

	/**
	 * Constructs a multi-segment Bezier curve from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The first coordinate pair is the initial anchor point, the following coordinate pairs correspond
	 * to the first control point, second control point, and final anchor point of each additional curve.
	 * The shape will be open and consist only of Bezier curves with no straight lines. 
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment curved line of type BezShape.BEZ_MULTICURVE
	 */
	protected BezMultiCurve(PApplet parent, float[] coords) {
		super(parent);
		if (coords.length < 2) {
			throw new IllegalArgumentException("The array argument must contain at least two values");
		}
		if (0 != (coords.length - 2) % 6 ) {
			throw new IllegalArgumentException("The array argument must be of length 2 + 6n, where n is the number of Bezier vertices.");
		}
		this.setIsClosed(false);
		this.setNoFill();
		int i = 0;
		this.setStartPoint(coords[i++], coords[i++]);
		while (i < coords.length - 1) {
			this.append(coords[i++], coords[i++], coords[i++], coords[i++], coords[i++], coords[i++]);
		}
		this.setBezType(BezType.BEZ_MULTICURVE);
	}
	
	/**
	 * Returns a multi-segment Bezier curve from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The first coordinate pair is the initial anchor point, the following coordinate pairs correspond
	 * to the first control point, second control point, and final anchor point of each additional curve.
	 * The shape will be open and consist only of Bezier curves with no straight lines.
	 * @param parent   reference to host PApplet used for calls to Processing environment 
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment curved line of type BezShape.BEZ_MULTICURVE
	 */
	public static BezMultiCurve makeMultiCurve(PApplet parent, float[] coords) {
		return new BezMultiCurve(parent, coords);
	}
	/**
	 * Returns a multi-segment Bezier curve from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The first coordinate pair is the initial anchor point, the following coordinate pairs correspond
	 * to the first control point, second control point, and final anchor point of each additional curve.
	 * The shape will be open and consist only of Bezier curves with no straight lines. 
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment curved line of type BezShape.BEZ_MULTICURVE
	 */
	public static BezMultiCurve makeMultiCurve(float[] coords) {
		return new BezMultiCurve(IgnoCodeLib.getMyParent(), coords);
	}

}
