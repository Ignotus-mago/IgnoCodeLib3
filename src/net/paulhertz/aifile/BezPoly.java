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

import java.util.ArrayList;

import processing.core.PApplet;

/** 
 * Provides factory methods to construct a closed polygonal path consisting of straight line segments.
 * 
 * @example Polygons
 */
public class BezPoly extends BezShape {

	/**
	 * Constructs a closed polygonal shape from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last coordinate pair
	 * and the first should be identical for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * @param parent   a PApplet
	 * @param coords   an array of coordinate pairs
	 */
	protected BezPoly(PApplet parent, float[] coords) {
		super(parent);
		if (coords.length < 2) {
			throw new IllegalArgumentException("The array argument must contain at least two values");
		}
		if (0 != coords.length % 2) {
			throw new IllegalArgumentException("The array argument must contain an even number of values");
		}
		this.setIsClosed(true);
		int i = 0;
		this.setStartPoint(coords[i++], coords[i++]);
		while (i < coords.length - 1) {
			this.append(coords[i++], coords[i++]);
		}
		this.setBezType(BezType.BEZ_POLY);
	}

	/**
	 * Constructs a closed polygonal shape from a list of {@code LineVertex}.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last coordinate pair
	 * and the first should be identical for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * @param parent   a PApplet
	 * @param points   an ArrayList of LineVertex
	 */
	protected BezPoly(PApplet parent, ArrayList<LineVertex> points) {
		super(parent);
		if (points.size() < 1) {
			throw new IllegalArgumentException("The array argument must contain at least one point");
		}
		this.setIsClosed(true);
		this.setStartPoint(points.get(0));
		for (int i = 1; i < points.size(); i++) {
			this.append(points.get(i));
		}
		this.setBezType(BezType.BEZ_POLY);
	}


	/**
	 * Constructs a closed polygonal shape from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last coordinate pair
	 * and the first should be the same for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * @param parent   a PApplet
	 * @param coords   an array of coordinate pairs
	 * @return         a closed polygon of type BezShape.BEZ_POLY
	 */
	public static BezPoly makePoly(PApplet parent, float[] coords) {
		return new BezPoly(parent, coords);
	}
	/**
	 * Constructs a closed polygonal shape from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last coordinate pair
	 * and the first should be the same for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param coords   an array of coordinate pairs
	 * @return         a closed polygon of type BezShape.BEZ_POLY
	 */
	public static BezPoly makePoly(float[] coords) {
		return new BezPoly(IgnoCodeLib.getMyParent(), coords);
	}

	/**
	 * Constructs a closed polygonal shape from a list of {@code Vertex2DINF}.
	 * Only the anchor points of a BezVertex will be used, control points are ignored. 
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last vertex
	 * and the first should be the same for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * @param parent   a PApplet
	 * @param points   an ArrayList of Vertex2DINF. 
	 * @return         a closed polygon of type BezShape.BEZ_POLY
	 */
	public static BezPoly makePolyVertex2D(PApplet parent, ArrayList<Vertex2DINF> points) {
		float[] coords = new float[points.size() * 2];
		int i = 0;
		for (Vertex2DINF pt : points) {
			coords[i++] = pt.x();
			coords[i++] = pt.y();
		}
		return new BezPoly(parent, coords);
	}
	/**
	 * Constructs a closed polygonal shape from a list of {@code Vertex2DINF}.
	 * Only the anchor points of a BezVertex will be used, control points are ignored. 
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last vertex
	 * and the first should be the same for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param points   an ArrayList of Vertex2DINF. 
	 * @return         a closed polygon of type BezShape.BEZ_POLY
	 */
	public static BezPoly makePolyVertex2D(ArrayList<Vertex2DINF> points) {
		float[] coords = new float[points.size() * 2];
		int i = 0;
		for (Vertex2DINF pt : points) {
			coords[i++] = pt.x();
			coords[i++] = pt.y();
		}
		return new BezPoly(IgnoCodeLib.getMyParent(), coords);
	}

	/**
	 * Constructs a closed polygonal shape from a list of {@code LineVertex}.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last vertex
	 * and the first should be the same for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * @param parent   a PApplet
	 * @param points   an ArrayList of LineVertex. 
	 * @return         a closed polygon of type BezShape.BEZ_POLY
	 */
	public static BezPoly makePolyLineVertex(PApplet parent, ArrayList<LineVertex> points) {
		return new BezPoly(parent, points);
	}
	/**
	 * Constructs a closed polygonal shape from a list of {@code LineVertex}.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * The shape will be closed and consist only of straight lines. The last vertex
	 * and the first should be the same for a properly closed polygon (it will be marked
	 * as closed when exported to Adobe Illustrator, regardless).
	 * PApplet used for calls to the Processing environment is obtained from 
	 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
	 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param points   an ArrayList of LineVertex. 
	 * @return         a closed polygon of type BezShape.BEZ_POLY
	 */
	public static BezPoly makePolyLineVertex(ArrayList<LineVertex> points) {
		return new BezPoly(IgnoCodeLib.getMyParent(), points);
	}
	
	
	/**
	 * Returns geometry of a star as an array of float.
	 * @param x0           X center of star.
	 * @param y0           Y center of star.
	 * @param innerRadius  Inner radis of arms.
	 * @param outerRadius  Outer radius of arms.
	 * @param nArms        Number of arms.
	 * @return             Geometry of star [x,y,x,y,...].
	 */
	public static float[] getStarCoords (float x0, float y0,
			float innerRadius, float outerRadius, int nArms) {
		int nPoints = nArms * 2 + 1;
		float[] xy = new float[nPoints * 2];
		float angleStep = (float) (Math.PI / nArms);
		float turn = (float) Math.toRadians(90);
		for (int i = 0; i < nArms * 2; i++) {
			float angle = i * angleStep + turn;
			float radius = (i % 2) == 0 ? innerRadius : outerRadius;

			xy[i*2 + 0] = (float) (x0 + radius * Math.cos (angle));
			xy[i*2 + 1] = (float) (y0 + radius * Math.sin (angle));
		}
		// Close polygon
		xy[nPoints*2 - 2] = xy[0];
		xy[nPoints*2 - 1] = xy[1];
		return xy;
	}

	
}
