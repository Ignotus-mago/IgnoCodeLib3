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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.paulhertz.geom.Matrix3;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @author Paul Hertz
 * 
 * TODO graphics state component
 */
/**
 * The base class for all components. Do not invoke this class directly. Components form a tree structure with the 
 * {@link net.paulhertz.aifile.DocumentComponent DocumentComponent} at the top. A document may have several layers 
 * ({@link net.paulhertz.aifile.LayerComponent LayerComponent}). Each layer can contain
 * shapes ({@link net.paulhertz.aifile.BezShape BezShape}), text ({@link net.paulhertz.aifile.PointText PointText}) or 
 * groups ({@link net.paulhertz.aifile.GroupComponent GroupComponent}). The {@code GroupComponent} can contain
 * shapes or text or nested groups. 
 * <p>The draw, write, and transform commands all cascade down the tree. Thus a draw command given to 
 * a document, layer, or group will eventually reach all the children of that component, and cause
 * them to draw their geometry or text to the screen. Similarly, the write command from the document 
 * level will write out a complete Adobe Illustrator file. Transform executed at any level, or on terminal
 * components such as BezShape and PointText, will execute a supplied geometric transform.
 * </p>
 */
public abstract class DisplayComponent implements Visitable {
	/** components immediately down tree from this component */
	protected ArrayList<DisplayComponent> children;
	/** parent of this component, if there is one */
	protected DisplayComponent parentComponent;
	/** unique id for each instantiated object */
	protected int id;
	/** PApplet for callbacks to Processing drawing environment, etc. Used by constructors */
	protected PApplet parent;
	/** locked/enabled status of component, default is false */
	protected boolean isLocked = false;
	/** visibility of component, default is true */
	protected boolean isVisible = true;
	/** counter static var for assigning component IDs */
	protected static int counter = 0;
	
	
	/**
	 * Adds a component to children of this component, 
	 * throws an {@code UnsupportedOperationException} if component is terminal. 
	 * @param component   DisplayComponent to add to this component's children
	 */
	public abstract void add(DisplayComponent component);
	/**
	 * Adds all components in a list to this component, 
	 * throws an {@code UnsupportedOperationException} if component is terminal.
	 * @param comps   an ArrayList of DisplayComponents
	 */
	public abstract void add(ArrayList<? extends DisplayComponent> comps);

	/**
	 * Removes a component from children of this component, 
	 * throws an {@code UnsupportedOperationException} if component is terminal.  
	 * @param component   DisplayComponent to remove from this component's children
	 * @return   true if component was found and removed, false otherwise.
	 */
	public boolean remove(DisplayComponent component) {
		return this.children().remove(component);
	}

	/**
	 * Returns the component at index from the children of this component, 
	 * throws an {@code UnsupportedOperationException} if component is terminal.
	 * Throws an IndexOutOfBoundsException 
	 * if index is out of range (index &lt; 0 || index &gt;= size). 
	 * @param index   index to component
	 * @return   the DisplayComponent at the supplied index
	 */
	public DisplayComponent get(int index) {
		return this.children().get(index);
	}

	/**
	 * Returns an iterator over children, 
	 * throws an {@code UnsupportedOperationException} if component is terminal.
	 * @return   an Iterator over the children array of this component
	 */
	public Iterator<DisplayComponent> iterator() {
		return this.children().iterator(); 
	}

	/**
	 * @return the parent component of this component, or null if this is the root component
	 */
	public DisplayComponent parentComponent() {
		return this.parentComponent;
	}
	/**
	 * Sets the parent component of this component to the supplied component.
	 * There is generally no need to do this, constructors take care of it for you.
	 * @param newParentComponent   component to set as parent of this component.
	 */
	public void setParentComponent(DisplayComponent newParentComponent) {
		this.parentComponent = newParentComponent;
	}

	/**
	 * @return   the children of this component (may be an empty list)
	 * @since Nov. 3, 2011 -- return an empty List instead of null. Return value changed to List interface.
	 */
	public List<DisplayComponent> children() {
		if (null == children) {
			return Collections.emptyList();
		}
		return this.children;
	}

	public int id() {
		return this.id;
	}

	/**
	 * @return   true if this is a terminal (leaf) component, false if it is a composite component (i.e.
	 *           a component that can add sub-components to a children array)
	 */

	public abstract boolean isTerminal();

	/**
	 * @return the isLocked
	 */
	public boolean isLocked() {
		return isLocked;
	}
	/**
	 * @param isLocked the isLocked to set
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}
	/**
	 * @param isVisible the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Sets the {@code isVisible} attribute of this component to true. {@code draw()} command will execute.
	 */
	public void show() {
		this.setVisible(true);
	}
	/**
	 * Sets the {@code isVisible} attribute of this component to true. {@code draw()} command will not execute.
	 * {@code write()} command will bracket component as not visible, with subsequent components visible.
	 */
	public void hide() {
		this.setVisible(false);
	}

	/**
	 * Draws geometry or text to the Processing window. Component must be flagged as visible (the default).
	 */
	public abstract void draw();
	
	/**
	 * Draws a component to a supplied PGraphics. Except for the document component, a component 
	 * must be visible (the default) to draw. Structural components simply iterate over their children.
	 * Graphic components should call beginShape and endShape on their own. 
	 * It's up to the user to call beginDraw() and endDraw() on the PGraphics instance.
	 * @param pg   a PGraphics instance	 
	 */
	public abstract void  draw(PGraphics pg);

	/**
	 * Writes an Adobe Illustrator 7.0 file format encoding structure, geometry and text.
	 * @param pw   a PrintWriter for file output.
	 */
	public abstract void write(PrintWriter pw);

	/**
	 * Transforms geometry of shapes and location of text using the supplied matrix.
	 * @param matx   a Matrix3 that encapsulates an affine geometric transform.
	 */
	public abstract void transform(Matrix3 matx);	
	
}
