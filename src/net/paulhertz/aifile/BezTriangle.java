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

import java.awt.geom.Point2D;

import net.paulhertz.geom.GeomUtils;
import net.paulhertz.geom.Matrix3;
import processing.core.PApplet;

/**
 * Provides factory methods to create and operate on a triangular shape.
 */
public class BezTriangle extends BezShape {

	protected BezTriangle(PApplet parent) {
		this(parent, 0, 0, 1);
	}

	/**
	 * Constructs an equilateral triangle with center {@code xctr, yctr} and distance 
	 * from center to each vertex equal to {@code radius}.
	 * The base is aligned with the x-axis, apex points up. 
	 * @param xctr     x-coordinate of center of triangle
	 * @param yctr     y-coordinate of center of triangle
	 * @param radius   radius of the triangle
	 */
	protected BezTriangle(PApplet parent, float xctr, float yctr, float radius) {
		super(parent, xctr, yctr - radius);
		float sides = 3;
		this.setCenter(xctr,yctr);
		this.setBezType(BezShape.BezType.BEZ_TRIANGLE);
		double ang = GeomUtils.TWO_PI/sides;
		Matrix3 matx = new Matrix3();
		matx.translateCTM(-xctr, -yctr);
		matx.rotateCTM(ang);
		matx.translateCTM(xctr, yctr);
		Point2D.Double pt = new Point2D.Double(xctr, yctr - radius);
		for (int i = 0; i < sides; i++) {
			pt = matx.multiplyPointByNormalCTM(pt.x, pt.y, pt);
			this.append((float) pt.getX(), (float) pt.getY());
		}
	}

	/**
	 * Constructs a triangle from three points.
	 * @param parent   PApplet used for calls to the Processing environment
	 * @param x1   x-coordinate of first point
	 * @param y1   y-coordinate of first point
	 * @param x2   x-coordinate of second point
	 * @param y2   y-coordinate of second point
	 * @param x3   x-coordinate of third point
	 * @param y3   y-coordinate of third point
	 */
	protected BezTriangle(PApplet parent, float x1, float y1, float x2, float y2, float x3, float y3) {
		super(parent, x1, y1);
		this.append(x2, y2);
		this.append(x3, y3);
		this.append(x1, y1);
		this.setBezType(BezShape.BezType.BEZ_TRIANGLE);
	}
	
	/**
	 * Returns an equilateral triangle with center {@code xctr, yctr} and distance 
	 * from center to each vertex equal to {@code radius}.
	 * The base is aligned with the x-axis, apex points down. 
	 * @param parent   PApplet used for calls to the Processing environment
	 * @param xctr     x-coordinate of center of triangle
	 * @param yctr     y-coordinate of center of triangle
	 * @param radius   radius of the triangle
	 */
	public static BezTriangle makeCenterRadius(PApplet parent, float xctr, float yctr, float radius) {
		return new BezTriangle(parent, xctr, yctr, radius);
	}
	/**
	 * Returns an equilateral triangle with center {@code xctr, yctr} and distance 
	 * from center to each vertex equal to {@code radius}.
	 * The base is aligned with the x-axis, apex points down. 
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param xctr     x-coordinate of center of triangle
	 * @param yctr     y-coordinate of center of triangle
	 * @param radius   radius of the triangle
	 */
	public static BezTriangle makeCenterRadius(float xctr, float yctr, float radius) {
		return new BezTriangle(IgnoCodeLib.getMyParent(), xctr, yctr, radius);
	}
	
	/**
	 * Returns a triangle constructed from three points.
	 * @param parent   PApplet used for calls to the Processing environment
	 * @param x1   x-coordinate of first point
	 * @param y1   y-coordinate of first point
	 * @param x2   x-coordinate of second point
	 * @param y2   y-coordinate of second point
	 * @param x3   x-coordinate of third point
	 * @param y3   y-coordinate of third point
	 */
	public static BezTriangle makeThreePoints(PApplet parent, float x1, float y1, float x2, float y2, float x3, float y3) {
		return new BezTriangle(parent, x1, y1, x2, y2, x3, y3);
	}
	/**
	 * Returns a triangle constructed from three points.
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param x1   x-coordinate of first point
	 * @param y1   y-coordinate of first point
	 * @param x2   x-coordinate of second point
	 * @param y2   y-coordinate of second point
	 * @param x3   x-coordinate of third point
	 * @param y3   y-coordinate of third point
	 */
	public static BezTriangle makeThreePoints(float x1, float y1, float x2, float y2, float x3, float y3) {
		return new BezTriangle(IgnoCodeLib.getMyParent(), x1, y1, x2, y2, x3, y3);
	}
	


}
