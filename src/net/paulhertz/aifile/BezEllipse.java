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

/**
 * Provides factory methods to construct a closed elliptical BezShape.
 * In methods that do not include a reference to a PApplet in the signature, 
 * the PApplet used for calls to the Processing environment is obtained from 
 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
 * 
 * @example EllipsesAndCircles 
 */
public class BezEllipse extends BezShape {

	/**
	 * Creates a closed elliptical BezShape with sectors number of Bezier curves.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 * @param xctr      x-coordinate of center of circle
	 * @param yctr      y-coordinate of center of circle
	 * @param w         width of the ellipse
	 * @param h         height of the ellipse
	 * @param sectors   integer for number of equal divisions of circle
	 * @return          an elliptical BezShape of type BezShape.BEZ_ELLIPSE
	 */
	protected BezEllipse(PApplet parent, float xctr, float yctr, float w, float h, int sectors) {
		super(parent, xctr, w/2 + yctr, true);
		float k = 4 * (float) KAPPA / sectors;
		float radius = w/2;
		float d = k * radius;
		float cx1, cy1, cx2, cy2, ax2, ay2;
		Point2D.Float cp1 = new Point2D.Float();
		Point2D.Float cp2 = new Point2D.Float();
		Point2D.Float ap2 = new Point2D.Float();
		cx1 = d;
		cy1 = radius;
		cp2 = GeomUtils.rotateCoor(-d, radius, (float) -GeomUtils.TWO_PI/sectors);
		ap2 = GeomUtils.rotateCoor(0, radius, (float) -GeomUtils.TWO_PI/sectors);
		cx2 = cp2.x;
		cy2 = cp2.y;
		ax2 = ap2.x;
		ay2 = ap2.y;
		this.setBezType(BezType.BEZ_ELLIPSE);
		this.append(cx1 + xctr, cy1 + yctr, cx2 + xctr, cy2 + yctr, ax2 + xctr, ay2 + yctr);
		for (int i = 1; i < sectors; i++) {
			cp1 = GeomUtils.rotateCoor(cx1, cy1, i * (float) -GeomUtils.TWO_PI/sectors);
			cp2 = GeomUtils.rotateCoor(cx2, cy2, i * (float) -GeomUtils.TWO_PI/sectors);
			ap2 = GeomUtils.rotateCoor(ax2, ay2, i * (float) -GeomUtils.TWO_PI/sectors);
			this.append(cp1.x + xctr, cp1.y + yctr, cp2.x + xctr, cp2.y + yctr, ap2.x + xctr, ap2.y + yctr);
		}
		this.setCenter(xctr, yctr);
		this.scaleShape(1.0f, h/w);
	}
	
	public static BezEllipse makeLeftTopWidthHeight(PApplet parent, float left, float top, float width, float height) {
		return new BezEllipse(parent, left + width/2.0f, top + height/2.0f, width, height, 4);
	}
	public static BezEllipse makeLeftTopWidthHeight(float left, float top, float width, float height) {
		return new BezEllipse(IgnoCodeLib.getMyParent(), left + width/2.0f, top + height/2.0f, width, height, 4);
	}
	
	
	public static BezEllipse makeCenterWidthHeight(PApplet parent, float xctr, float yctr, float width, float height) {
		return new BezEllipse(parent, xctr, yctr, width, height, 4);
	}
	public static BezEllipse makeCenterWidthHeight(float xctr, float yctr, float width, float height) {
		return new BezEllipse(IgnoCodeLib.getMyParent(), xctr, yctr, width, height, 4);
	}
	
	
	public static BezEllipse makeLeftTopRightBottom(PApplet parent, float left, float top, float right, float bottom) {
		return new BezEllipse(parent, (left + right)/2.0f, (top + bottom)/2.0f, right - left, bottom - top, 4);
	}
	public static BezEllipse makeLeftTopRightBottom(float left, float top, float right, float bottom) {
		return new BezEllipse(IgnoCodeLib.getMyParent(), (left + right)/2.0f, (top + bottom)/2.0f, right - left, bottom - top, 4);
	}
	
	
	public static BezEllipse makeLeftTopWidthHeightSectors(PApplet parent, float left, float top, float width, float height, int sectors) {
		return new BezEllipse(parent, left + width/2.0f, top + height/2.0f, width, height, sectors);
	}
	public static BezEllipse makeLeftTopWidthHeightSectors(float left, float top, float width, float height, int sectors) {
		return new BezEllipse(IgnoCodeLib.getMyParent(), left + width/2.0f, top + height/2.0f, width, height, sectors);
	}
	
	
	public static BezEllipse makeCenterWidthHeightSectors(PApplet parent, float xctr, float yctr, float width, float height, int sectors) {
		return new BezEllipse(parent, xctr, yctr, width, height, sectors);
	}
	public static BezEllipse makeCenterWidthHeightSectors(float xctr, float yctr, float width, float height, int sectors) {
		return new BezEllipse(IgnoCodeLib.getMyParent(), xctr, yctr, width, height, sectors);
	}
	
	
	public static BezEllipse makeLeftTopRightBottomSectors(PApplet parent, float left, float top, float right, float bottom, int sectors) {
		return new BezEllipse(parent, (left + right)/2.0f, (top + bottom)/2.0f, right - left, bottom - top, sectors);
	}
	public static BezEllipse makeLeftTopRightBottomSectors(float left, float top, float right, float bottom, int sectors) {
		return new BezEllipse(IgnoCodeLib.getMyParent(), (left + right)/2.0f, (top + bottom)/2.0f, right - left, bottom - top, sectors);
	}
	
}
