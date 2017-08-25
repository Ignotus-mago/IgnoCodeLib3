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

import processing.core.PApplet;

import net.paulhertz.aifile.BezCircle;
import net.paulhertz.aifile.BezShape;

/**
 * Provides factory methods to create a closed circular shape.
 * In methods that do not include a reference to a PApplet in the signature, 
 * the PApplet used for calls to the Processing environment is obtained from 
 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException.
 * 
 * @example EllipsesAndCircles 
 */
public class BezCircle extends BezShape {

	
	/**
	 * Creates a closed circular BezShape with sectors number of Bezier curves.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
     * @param parent    PApplet used for calls to the Processing environment, notably for drawing
	 * @param xctr      x-coordinate of center of circle
	 * @param yctr      y-coordinate of center of circle
	 * @param radius    radius of the circle
	 * @param sectors   integer for number of equal divisions of circle
	 * @return          a circular BezShape of type BezShape.BEZ_CIRCLE
	 */
	protected BezCircle(PApplet parent, float xctr, float yctr, float radius, int sectors) {
		super(parent, xctr, radius + yctr, true);
		float k = 4 * (float) KAPPA / sectors;
		float d = k * radius;
		float cx1, cy1, cx2, cy2, ax2, ay2;
		Point2D.Float cp1 = new Point2D.Float();
		Point2D.Float cp2 = new Point2D.Float();
		Point2D.Float ap2 = new Point2D.Float();
		cx1 = d;
		cy1 = radius;
		cp2 = GeomUtils.rotateCoor(-d, radius, (float) -(GeomUtils.TWO_PI/sectors));
		ap2 = GeomUtils.rotateCoor(0, radius, (float) -(GeomUtils.TWO_PI/sectors));
		cx2 = cp2.x;
		cy2 = cp2.y;
		ax2 = ap2.x;
		ay2 = ap2.y;
		this.setBezType(BezType.BEZ_CIRCLE);
		this.append(cx1 + xctr, cy1 + yctr, cx2 + xctr, cy2 + yctr, ax2 + xctr, ay2 + yctr);
		for (int i = 1; i < sectors; i++) {
			cp1 = GeomUtils.rotateCoor(cx1, cy1, i * (float) -(GeomUtils.TWO_PI/sectors));
			cp2 = GeomUtils.rotateCoor(cx2, cy2, i * (float) -(GeomUtils.TWO_PI/sectors));
			ap2 = GeomUtils.rotateCoor(ax2, ay2, i * (float) -(GeomUtils.TWO_PI/sectors));
			this.append(cp1.x + xctr, cp1.y + yctr, cp2.x + xctr, cp2.y + yctr, ap2.x + xctr, ap2.y + yctr);
		}
		this.setCenter(xctr, yctr);
	}
	
	public static BezCircle makeCenterRadius(PApplet parent, float xctr, float yctr, float radius) {
		return new BezCircle(parent, xctr, yctr, radius, 4);
	}
	public static BezCircle makeCenterRadius(float xctr, float yctr, float radius) {
		return new BezCircle(IgnoCodeLib.getMyParent(), xctr, yctr, radius, 4);
	}


	public static BezCircle makeCenterRadiusSectors(PApplet parent, float xctr, float yctr, float radius, int sectors) {
		return new BezCircle(parent, xctr, yctr, radius, sectors);
	}
	public static BezCircle makeCenterRadiusSectors(float xctr, float yctr, float radius, int sectors) {
		return new BezCircle(IgnoCodeLib.getMyParent(), xctr, yctr, radius, sectors);
	}


	public static BezCircle makeLeftTopRadius(PApplet parent, float left, float top, float radius) {
		return new BezCircle(parent, left + radius, top + radius, radius, 4);
	}
	public static BezCircle makeLeftTopRadius(float left, float top, float radius) {
		return new BezCircle(IgnoCodeLib.getMyParent(), left + radius, top + radius, radius, 4);
	}


	public static BezCircle makeLeftTopRadiusSectors(PApplet parent, float left, float top, float radius, int sectors) {
		return new BezCircle(parent, left + radius, top + radius, radius, sectors);
	}
	public static BezCircle makeLeftTopRadiusSectors(float left, float top, float radius, int sectors) {
		return new BezCircle(IgnoCodeLib.getMyParent(), left + radius, top + radius, radius, sectors);
	}

}
