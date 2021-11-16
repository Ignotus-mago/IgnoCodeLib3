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

import processing.core.PApplet;

/**
 * Implements the ComponentVisitor interface to calculate the bounding rectangle of all geometry visited.
 *
 */
public class BoundsCalculationVisitor extends ComponentVisitor {
	private PApplet parent;
	private float xMax;
	private float yMax;
	private float xMin;
	private float yMin;
	private int index = 0;
	
	
	private BoundsCalculationVisitor() {
		
	}	
	
	public static BoundsCalculationVisitor makeBoundsCalculationVisitor() {
		return new BoundsCalculationVisitor();
	}
	
	
	public static BezRectangle componentBounds(DisplayComponent comp) {
		BoundsCalculationVisitor visitor = BoundsCalculationVisitor.makeBoundsCalculationVisitor();
		comp.accept(visitor);
		return visitor.bounds();
	}

	
	/**
	 * visits a BezShape node
	 * @param comp   a BezShape instance
	 */
	public void visitBezShape(BezShape comp) {
		BezRectangle compBounds = comp.boundsRect();
		float left = compBounds.getLeft();
		float right = compBounds.getRight();
		float bottom = compBounds.getBottom();
		float top = compBounds.getTop();
		// we snag our parent field from the first shape
		// if it's null, we're on the first shape
		if (null == parent) {
			this.parent = comp.parent;
			this.xMax = right;
			this.xMin = left;
			this.yMax = bottom;
			this.yMin = top;
		}
		if (this.xMax < right) this.xMax = right;
		if (this.xMin > left) this.xMin = left;
		if (this.yMax < bottom) this.yMax = bottom;
		if (this.yMin > top) this.yMin = top;
		index++;
	}

	/**
	 * @return the xMax
	 */
	public float xMax() {
		return xMax;
	}

	/**
	 * @return the yMax
	 */
	public float yMax() {
		return yMax;
	}

	/**
	 * @return the xMin
	 */
	public float xMin() {
		return xMin;
	}

	/**
	 * @return the yMin
	 */
	public float yMin() {
		return yMin;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	public BezRectangle bounds() {
		BezRectangle bounds = BezRectangle.makeLeftTopRightBottom(parent, xMin, yMin, xMax, yMax);
		return bounds;
	}
	
	
}
