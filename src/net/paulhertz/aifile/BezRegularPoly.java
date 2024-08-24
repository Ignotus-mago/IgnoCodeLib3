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

import processing.core.PApplet;

import net.paulhertz.geom.GeomUtils;
import net.paulhertz.geom.Matrix3;

/**
 * Provides factory methods to construct a regular polygonal shape consisting of straight lines.
 * 
 * @example Polygons
 */
public class BezRegularPoly extends BezShape {
	
	/**
	 * Constructs a regular pentagonal BezShape with 5 edges and radius 1 at 0,0.
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 */
	protected BezRegularPoly(PApplet parent) {
		this(parent, 0, 0, 1, 5);
	}

	/**
	 * Constructs a regular polygon BezShape with <code>sides</code> edges and radius <code>radius</code>.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The shape will be closed and consist only of straight lines. 
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 * @param xctr     x-coordinate of center of polygon
	 * @param yctr     y-coordinate of center of polygon
	 * @param radius   radius of the polygon
	 * @param sides    number of sides of the polygon
	 */
	protected BezRegularPoly(PApplet parent, float xctr, float yctr, float radius, int sides) {
		super(parent, xctr, yctr - radius);
		this.setCenter(xctr,yctr);
		this.setBezType(BezShape.BezType.BEZ_REGULAR_POLY);
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
	 * Constructs a regular polygon BezShape with <code>sides</code> edges and radius <code>radius</code>.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The shape will be closed and consist only of straight lines. 
 	 * @param parent   a PApplet hosting the geometry
	 * @param xctr     x-coordinate of center of polygon
	 * @param yctr     y-coordinate of center of polygon
	 * @param radius   the radius of a circumscribed circle
	 * @param sides    the number of sides
	 * @return         a closed regular polygon of type BezShape.BEZ_REGULAR_POLY
	 */
	public static BezRegularPoly makeCenterRadiusSides(PApplet parent, float xctr, float yctr, float radius, int sides) {
		return new BezRegularPoly(parent, xctr, yctr, radius, sides);
	}
	/**
	 * Constructs a regular polygon BezShape with <code>sides</code> edges and radius <code>radius</code>.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The shape will be closed and consist only of straight lines. 
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param xctr     x-coordinate of center of polygon
	 * @param yctr     y-coordinate of center of polygon
	 * @param radius   the radius of a circumscribed circle
	 * @param sides    the number of sides
	 * @return         a closed regular polygon of type BezShape.BEZ_REGULAR_POLY
	 */
	public static BezRegularPoly makeCenterRadiusSides(float xctr, float yctr, float radius, int sides) {
		return new BezRegularPoly(IgnoCodeLib.getMyParent(), xctr, yctr, radius, sides);
	}
	
	
	/**
	 * Constructs a regular polygon BezShape with <code>sides</code> edges and diameter <code>diameter</code>
	 * that fits within a square with the given left and top and a width equal to diameter.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The shape will be closed and consist only of straight lines. 
 	 * @param parent     a PApplet hosting the geometry
	 * @param left       x-coordinate of top left corner of square bounds
	 * @param top        y-coordinate of top left corner of square bounds
	 * @param diameter   the width/height of square bounds
	 * @param sides      the number of sides
	 * @return           a closed regular polygon of type BezShape.BEZ_REGULAR_POLY
	 */
	public static BezRegularPoly makeLeftTopDiameterSides(PApplet parent, float left, float top, float diameter, int sides) {
		float radius = diameter/2.0f;
		return new BezRegularPoly(parent, left + radius, top + radius, radius, sides);
	}
	/**
	 * Constructs a regular polygon BezShape with <code>sides</code> edges and diameter <code>diameter</code>
	 * that fits within a square with the given left and top and a width equal to diameter.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The shape will be closed and consist only of straight lines. 
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param left       x-coordinate of top left corner of square bounds
	 * @param top        y-coordinate of top left corner of square bounds
	 * @param diameter   the width/height of square bounds
	 * @param sides      the number of sides
	 * @return           a closed regular polygon of type BezShape.BEZ_REGULAR_POLY
	 */
	public static BezRegularPoly makeLeftTopDiameterSides(float left, float top, float diameter, int sides) {
		float radius = diameter/2.0f;
		return new BezRegularPoly(IgnoCodeLib.getMyParent(), left + radius, top + radius, radius, sides);
	}
	
}
