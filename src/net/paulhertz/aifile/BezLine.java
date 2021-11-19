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

import net.paulhertz.geom.GeomUtils;
import processing.core.PApplet;

/**
 * Provides factory methods to construct and operate on a straight line.
 */
public class BezLine extends BezShape {
	// TODO various parametric and implicit forms, interpolation, perpendicular, parallel, outline
	
	/**
	 * Constructs a straight line between (x1, y1) and (x2, y2), with black stroke and no fill.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * Sets center of transformation (rotation, etc) to first point.
	 * Called by static methods that return an instance of BezLine.
	 * @param x1   x-coordinate of first point
	 * @param y1   y-coordinate of first point
	 * @param x2   x-coordinate of second point
	 * @param y2   y-coordinate of second point
	 */
	protected BezLine(PApplet parent, float x1, float y1, float x2, float y2) {
		super(parent, x1, y1, false);
		this.append(x2, y2);
		this.setCenter(x1, y1);
		this.setBezType(BezType.BEZ_LINE);
	}
	
	public static BezLine makeCoordinates(PApplet parent, float x1, float y1, float x2, float y2) {
		return new BezLine(parent, x1, y1, x2, y2);
	}
	public static BezLine makeCoordinates(float x1, float y1, float x2, float y2) {
		return new BezLine(IgnoCodeLib.getMyParent(), x1, y1, x2, y2);
	}
	
	public static BezLine makePointAngleDistance(PApplet parent, float x1, float y1, float angle, float distance) {
		double dx = Math.cos(Math.toRadians(angle)) * distance;
		double dy = Math.sin(Math.toRadians(angle)) * distance;
		float x2 = (float) dx + x1;
		float y2 = (float) dy + y1;
		return new BezLine(parent, x1, y1, x2, y2);
	}
	public static BezLine makePointAngleDistance(float x1, float y1, float angle, float distance) {
		double dx = Math.cos(Math.toRadians(angle)) * distance;
		double dy = Math.sin(Math.toRadians(angle)) * distance;
		float x2 = (float) dx + x1;
		float y2 = (float) dy + y1;
		return new BezLine(IgnoCodeLib.getMyParent(), x1, y1, x2, y2);
	}
	
	public float[] getCoords() {
		float[] coords = new float[4];
		coords[0] = this.x;
		coords[1] = this.y;
		coords[2] = this.curves().get(0).x();
		coords[3] = this.curves().get(0).y();
		return coords;
	}
	
	public float angle() {
		float x1 = this.x;
		float y1 = this.y;
		float x2 = this.curves().get(0).x();
		float y2 = this.curves().get(0).y();
		float dx = x2 - x1;
		float dy = y2 - y1;
		return GeomUtils.getAngle(dx, dy);
	}
	
	public float distance() {
		float x1 = this.x;
		float y1 = this.y;
		float x2 = this.curves().get(0).x();
		float y2 = this.curves().get(0).y();
		return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

}
