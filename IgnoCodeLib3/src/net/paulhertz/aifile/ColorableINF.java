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

public interface ColorableINF {
	/**
	 * @return   true if this component is filled, false otherwise
	 */
	public boolean hasFill();
	/**
	 * Marks this component as having no fill.
	 * Equivalent to setHasFill(false), if the implementor provides a setHasFill method
	 */
	public void setNoFill();
	

	/**
	 * @return   true if this component is stroked, false otherwise
	 */
	public boolean hasStroke();
	/**
	 * Marks this component as having no stroke
	 * Equivalent to setHasStroke(false), if the implementor provides a setHasStroke method
	 */
	public void setNoStroke();
	
	/**
	 * @return the current fill color
	 */
	public int fillColor();
	/**
	 * Sets the current fill color.
	 * @param newFillColor   a Processing color (32-bit int with ARGB bytes).
	 */
	public void setFillColor(int newFillColor);


	/**
	 * @return the current stroke color
	 */
	public int strokeColor();
	/**
	 * Sets the current stroke color.
	 * @param newStrokeColor   a Processing color (32-bit int with ARGB bytes).
	 */
	public void setStrokeColor(int newStrokeColor);


	/**
	 * Sets opacity of current fill color.
	 * @param opacity   a number in the range 0..255. Value is not checked!
	 */
	public void setFillOpacity(int opacity);
	/**
	 * @return   the opacity value of the current fill color
	 */
	public int fillOpacity();
	
	/**
	 * Sets opacity of current stroke color.
	 * @param opacity   a number in the range 0..255. Value is not checked!
	 */
	public void setStrokeOpacity(int opacity);
	/**
	 * @return   the opacity value of the current stroke color
	 */
	public int strokeOpacity();

	
	/**
	 * Returns the current weight (in points) of stroked lines.
	 * @return the current weight (in points) of stroked lines. One point = one pixel on screen.
	 */
	public float weight();
	/**
	 * @param newWeight the new weight of stroked lines.
	 */
	public void setWeight(float newWeight);

}
