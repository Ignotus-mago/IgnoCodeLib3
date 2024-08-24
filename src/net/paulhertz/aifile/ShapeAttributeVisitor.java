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


public class ShapeAttributeVisitor extends ComponentVisitor implements ColorableINF {
	private boolean hasFill;
	private boolean hasStroke;
	private int fillColor;
	private int strokeColor;
	private float weight;
	
	
	private ShapeAttributeVisitor() {
		
	}
	
	private ShapeAttributeVisitor(PApplet parent) {
		this.setColors(parent);
	}
	
	
	public static ShapeAttributeVisitor makeCurrentStateVisitor(PApplet parent) {
		return new ShapeAttributeVisitor(parent);
	}
	
	public static ShapeAttributeVisitor makeFilledShapeVisitor(int fillColor) {
		ShapeAttributeVisitor visitor = new ShapeAttributeVisitor();
		visitor.setFillColor(fillColor);
		visitor.setNoStroke();
		return visitor;
	}
	
	public static ShapeAttributeVisitor makeStrokedShapeVisitor(int strokeColor, int weight) {
		ShapeAttributeVisitor visitor = new ShapeAttributeVisitor();
		visitor.setStrokeColor(strokeColor);
		visitor.setWeight(weight);
		visitor.setNoFill();
		return visitor;
	}


	public static ShapeAttributeVisitor makeFilledStrokedShapeVisitor(int fillColor, int strokeColor, int weight) {
		ShapeAttributeVisitor visitor = new ShapeAttributeVisitor();
		visitor.setFillColor(fillColor);
		visitor.setStrokeColor(strokeColor);
		visitor.setWeight(weight);
		return visitor;
	}

	
	/**
	 * visits a BezShape node
	 * @param comp   a BezShape instance
	 */
	public void visitBezShape(BezShape comp) {
		if (this.hasFill) {
			comp.setFillColor(this.fillColor);
		}
		if (this.hasStroke) {
			comp.setStrokeColor(this.strokeColor);
			comp.setWeight(this.weight);
		}
	}

	/**
	 * Sets fill and stroke using the current graphics state.
	 */
	public void setColors(PApplet host) {
		if (host.g.fill) {
			this.setFillColor(host.g.fillColor);
		} 
		else {
			setNoFill();
		}
		if (host.g.stroke) {
			this.setStrokeColor(host.g.strokeColor);
			this.setWeight(host.g.strokeWeight);
		}
		else {
			setNoStroke();
		}
	}

	
	/**
	 * @return   true if this shape is filled, false otherwise
	 */
	public boolean hasFill() {
		return this.hasFill;
	}
	/**
	 * @param newHasFill   pass true if this shape has a fill, false otherwise. Note that
	 * the current fillColor will not be discarded by setting hasFill to false: the shape
	 * simply won't display or save to file with a fill. 
	 */
	public void setHasFill(boolean newHasFill) {
		this.hasFill = newHasFill;
	}
	/**
	 * Equivalent to setHasFill(false).
	 */
	public void setNoFill() {
		this.setHasFill(false);
	}
	

	/**
	 * @return   true if this shape is stroked, false otherwise
	 */
	public boolean hasStroke() {
		return this.hasStroke;
	}
	/**
	 * @param newHasStroke   pass true if this shape has a stroke, false otherwise. Note that
	 * the current strokeColor will not be discarded by setting hasStroke to false: the shape
	 * simply won't display or save to file with a stroke.
	 */
	public void setHasStroke(boolean newHasStroke) {
		this.hasStroke = newHasStroke;
	}
	/**
	 * Equivalent to setHasStroke(false).
	 */
	public void setNoStroke() {
		this.setHasStroke(false);
	}
	
	/**
	 * @return the current fill color
	 */
	public int fillColor() {
		return this.fillColor;
	}
	/**
	 * @param newFillColor   a Processing color (32-bit int with ARGB bytes).
	 */
	public void setFillColor(int newFillColor) {
		this.fillColor = newFillColor;
		setHasFill(true);
	}


	/**
	 * @return the current stroke color
	 */
	public int strokeColor() {
		return this.strokeColor;
	}
	/**
	 * @param newStrokeColor   a Processing color (32-bit int with ARGB bytes).
	 */
	public void setStrokeColor(int newStrokeColor) {
		this.strokeColor = newStrokeColor;
		this.setHasStroke(true);
	}


	/**
	 * Sets opacity of current fill color.
	 * @param opacity   a number in the range 0..255. Value is not checked!
	 */
	public void setFillOpacity(int opacity) {
		int[] argb = Palette.argbComponents(this.fillColor);
		this.setFillColor(Palette.composeColor(argb[1], argb[2], argb[3], opacity));
	}
	/**
	 * @return   the opacity value of the current fill color
	 */
	public int fillOpacity() {
		int[] argb = Palette.argbComponents(this.fillColor);
		return argb[0];
	}
	
	/**
	 * Sets opacity of current stroke color.
	 * @param opacity   a number in the range 0..255. Value is not checked!
	 */
	public void setStrokeOpacity(int opacity) {
		int[] argb = Palette.argbComponents(this.strokeColor);
		this.setStrokeColor(Palette.composeColor(argb[1], argb[2], argb[3], opacity));
	}
	/**
	 * @return   the opacity value of the current stroke color
	 */
	public int strokeOpacity() {
		int[] argb = Palette.argbComponents(this.strokeColor);
		return argb[0];
	}

	
	/**
	 * Returns the current weight (in points) of stroked lines.
	 * @return the current weight (in points) of stroked lines. One point = one pixel on screen.
	 */
	public float weight() {
		return this.weight;
	}
	/**
	 * @param newWeight the new weight of stroked lines.
	 */
	public void setWeight(float newWeight) {
		this.weight = newWeight;
	}


}
