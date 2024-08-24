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
import java.util.List;
import java.util.Iterator;

import net.paulhertz.geom.Matrix3;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @author paulhz
 *
 */
/**
 * Permits the addition of arbitrary, non-printing, custom information to file output. 
 *
 */
public class CustomComponent extends DisplayComponent {
	protected String tagIdentifier;
	protected String tagValue;
	
	/**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 */
	public CustomComponent() {
		this(IgnoCodeLib.getMyParent(), "", "");
	}
	/**
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 */
	public CustomComponent(PApplet parent) {
		this(parent, "", "");
	}
	/**
	 * @param tagIdentifier   String to identify custom data
	 * @param tagValue		  Custom data to save to file (non-printing).
	 */
	public CustomComponent(PApplet parent, String tagIdentifier, String tagValue) {
		this.parent = parent;
		this.tagIdentifier = tagIdentifier;
		this.tagValue = tagValue;
    	this.id = DisplayComponent.counter++;
	}

	
	/**
	 * @return the tagIdentifier
	 */
	public String tagIdentifier() {
		return tagIdentifier;
	}
	/**
	 * Sets key used to identify custom data.
	 * @param tagIdentifier the tagIdentifier to set
	 */
	public void setTagIdentifier(String tagIdentifier) {
		this.tagIdentifier = tagIdentifier;
	}
	/**
	 * Sets the value of custom data.
	 * @return the tagValue
	 */
	public String tagValue() {
		return tagValue;
	}
	/**
	 * @param tagValue the tagValue to set
	 */
	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}
	/** 
	 * @throws UnsupportedOperationException, CustomComponent is a terminal (leaf) node
	 */
	public void add(DisplayComponent component) {
		throw new UnsupportedOperationException("Attempt to add child to a terminal node."); 
	}
	/** 
	 * @throws UnsupportedOperationException, CustomComponent is a terminal (leaf) node
	 */
	public void add(ArrayList<? extends DisplayComponent> comps) {
		throw new UnsupportedOperationException("Attempt to add child to a terminal node."); 
	}
	/** 
	 * @throws UnsupportedOperationException, CustomComponent is a terminal (leaf) node
	 */
	public boolean remove(DisplayComponent component) {
		throw new UnsupportedOperationException("Attempt to remove child from a terminal node."); 
	}
	/** 
	 * @throws UnsupportedOperationException, CustomComponent is a terminal (leaf) node
	 */
	public DisplayComponent get(int index) {
		throw new UnsupportedOperationException("Attempt to access child of a terminal node.");
	}
	/** 
	 * @throws UnsupportedOperationException, CustomComponent is a terminal (leaf) node
	 */
	public Iterator<DisplayComponent> iterator() {
		throw new UnsupportedOperationException("Attempt to access children array of a terminal node.");
	}		
	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.DisplayComponent#children()
	 */
	@Override
	public List<DisplayComponent> children() {
		return Collections.emptyList();
	}
	/** 
	 * @return   <code>true</code>, this is a terminal component.
	 * @see net.paulhertz.aifile.DisplayComponent#isTerminal()
	 */
	public boolean isTerminal() {
		return true;
	}

	@Override
	public void draw() {
		return;
	}

	@Override
	public void draw(PGraphics pg) {
		return;
	}
	
	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.DisplayComponent#write(java.io.PrintWriter)
	 */
	@Override
	public void write(PrintWriter pw) {
		AIFileWriter.customObject(this.tagIdentifier, this.tagValue, pw);
	}

	@Override
	public void transform(Matrix3 matx) {
		// do nothing
	}

	/* (non-Javadoc)
	 * This is a terminal node, no children to visit
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor) {
		visitor.visitCustomComponent(this);
	}

	/* (non-Javadoc)
	 * This is a terminal node, no children to visit
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor, boolean order) {
		visitor.visitCustomComponent(this);
		
	}

}
