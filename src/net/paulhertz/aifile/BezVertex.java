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

import net.paulhertz.aifile.AIFileWriter;
import net.paulhertz.aifile.BezShape;
import net.paulhertz.aifile.BezVertex;
import net.paulhertz.aifile.Vertex2DINF;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @author paulhz
 */ 
/** 
 * Provides storage for a cubic Bezier curves's control points and terminal anchor point. 
 */
public class BezVertex implements Vertex2DINF {
	/** x-coordinate of first control point */
	protected float cx1;
	/** y-coordinate of first control point */
	protected float cy1;
	/**  x-coordinate of second control point  */
	protected float cx2;
	/**  y-coordinate of second control point  */
	protected float cy2;
	/** x-coordinate of terminal anchor point */
	protected float x;
	/** y-coordinate of terminal anchor point */
	protected float y;
	/** path segemnt type */
	public final static int segmentType = BezShape.CURVE_SEGMENT;

	/**
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param x     x-coordinate of terminal anchor point
	 * @param y     y-coordinate of terminal anchor point
	 */
	public BezVertex(float cx1, float cy1, float cx2, float cy2, float x, float y) {
		this.cx1 = cx1;
		this.cy1 = cy1;
		this.cx2 = cx2;
		this.cy2 = cy2;
		this.x = x;
		this.y = y;
	}

	/**
	 * initializes all points to 0,0
	 */
	public BezVertex() {
		this(0, 0, 0, 0, 0, 0);
	}

	public float cx1() {
		return cx1;
	}
	public void setCx1(float newCx1) {
		cx1 = newCx1;
	}

	public float cy1() {
		return cy1;
	}
	public void setCy1(float newCy1) {
		cy1 = newCy1;
	}

	public float cx2() {
		return cx2;
	}
	public void setCx2(float newCx2) {
		cx2 = newCx2;
	}

	public float cy2() {
		return cy2;
	}
	public void setCy2(float newCy2) {
		cy2 = newCy2;
	}

	@Override
	public float x() {
		return x;
	}
	public void setX(float newX) {
		x = newX;
	}

	@Override
	public float y() {
		return y;
	}
	public void setY(float newY) {
		y = newY;
	}

	@Override
	public int segmentType() {
		return BezVertex.segmentType;
	}

	@Override
	public float[] coords() {
		float[] knots = new float[6];
		knots[0] = cx1;
		knots[1] = cy1;
		knots[2] = cx2;
		knots[3] = cy2;
		knots[4] = x;
		knots[5] = y;
		return knots;
	}

	@Override
	public BezVertex clone() {
		return new BezVertex(this.cx1, this.cy1, this.cx2, this.cy2, this.x, this.y); 
	}

	 @Override
	 public void draw(PApplet parent) {
		 parent.bezierVertex(cx1, cy1, cx2, cy2, x, y);
	 }

	 @Override
	 public void draw(PGraphics pg) {
		 pg.bezierVertex(cx1, cy1, cx2, cy2, x, y);
	}

	@Override
	public void mark(PApplet parent) {
    int w = 6;
    int d = w - 1;
    parent.pushStyle();
    parent.noStroke();
    parent.fill(192);
    parent.ellipse(cx1,cy1, d, d);
    parent.ellipse(cx2,cy2, d, d);
    parent.fill(160);
    // square(x - w/2, y - w/2, w);
    parent.rect(x - w/2, y - w/2, w, w);
    parent.popStyle();
	}

	@Override
	public void mark(PGraphics pg) {
    int w = 6;
    int d = w - 1;
    pg.pushStyle();
    pg.noStroke();
    pg.fill(192);
    pg.ellipse(cx1,cy1, d, d);
    pg.ellipse(cx2,cy2, d, d);
    pg.fill(160);
    // square(x - w/2, y - w/2, w);
    pg.rect(x - w/2, y - w/2, w, w);
    pg.popStyle();
	}

	@Override
	public void write(PrintWriter output) {
		 AIFileWriter.psCurveTo(cx1, cy1, cx2, cy2, x, y, output);
	}
	
}
