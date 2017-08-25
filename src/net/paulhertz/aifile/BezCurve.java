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

import net.paulhertz.aifile.BezCurve;
import net.paulhertz.aifile.BezShape;

import processing.core.PApplet;

/**
 * Provides factory methods to construct a single Bezier curve.
 */
public class BezCurve extends BezShape {

	/**
	 * Constructs a single BŽzier curve.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The curve is initially open ({@code isClosed == false}).
	 * @param parent   reference to the host PApplet, used for calls to Processing environment
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 */
	protected BezCurve(PApplet parent, float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2) {
		super(parent, ax1, ay1, cx1, cy1, cx2, cy2, ax2, ay2, false);
		this.setBezType(BezType.BEZ_CURVE);
	}


	/**
	 * Constructs a single BŽzier curve.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The curve is initially open ({@code isClosed == false}).
	 * @param parent   reference to the host PApplet, used for calls to Processing environment
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 * @return      a curved line BezShape of type BezShape.BEZ_CURVE
	 */
	public static BezCurve makeCurve(PApplet parent, float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2) {
		return new BezCurve(parent, ax1, ay1, cx1, cy1, cx2, cy2, ax2, ay2);
	}
	/**
	 * Constructs a single BŽzier curve.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The curve is initially open ({@code isClosed == false}).
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 * @return      a curved line BezShape of type BezShape.BEZ_CURVE
	 */
	public static BezCurve makeCurve(float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2) {
		return new BezCurve(IgnoCodeLib.getMyParent(), ax1, ay1, cx1, cy1, cx2, cy2, ax2, ay2);
	}
	
	

}
