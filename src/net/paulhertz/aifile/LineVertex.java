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
import processing.core.*;

import java.io.PrintWriter;

import net.paulhertz.aifile.AIFileWriter;
import net.paulhertz.aifile.BezShape;
import net.paulhertz.aifile.LineVertex;
import net.paulhertz.aifile.Vertex2DINF;


/**
 * Stores a line vertex consisting of a single point.
 */
public class LineVertex implements Vertex2DINF {
	/** x-coordinate of anchor point */
	protected float x;
	/** y-coordinate of anchor point */
	protected float y;
	/** path segment type */
	public final static int segmentType = BezShape.LINE_SEGMENT;

	public LineVertex(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public LineVertex() {
		this(0, 0);
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
		return LineVertex.segmentType;
	}

	@Override
	public float[] coords() {
		float[] knots = new float[2];
		knots[0] = x;
		knots[1] = y;
		return knots;
	}

	@Override
	public LineVertex clone() {
		return new LineVertex(this.x, this.y);
	}

	@Override
	public void draw(PApplet parent) {
		parent.vertex(x, y);
	}

	 @Override
	 public void draw(PGraphics pg) {
		 pg.vertex(x, y);
	}

	 @Override
	public void write(PrintWriter output) {
		AIFileWriter.psLineTo(x, y, output);
	}

}

