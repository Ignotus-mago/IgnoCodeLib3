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
import java.util.Iterator;

import net.paulhertz.geom.Matrix3;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @author paulhz
 * TODO bounding box and geometric center point of group
 */
/**
 * Groups together geometry ({@link net.paulhertz.aifile.BezShape BezShape}), 
 * text ({@link net.paulhertz.aifile.PointText PointText}) and nested groups within a layer or 
 * another group. Components in a group are displayed and written to file with the most recently 
 * added component first (last in first out). 
 * 
 * @example DocumentExport *
 */
public class GroupComponent extends DisplayComponent {
	
	/**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 */
	public GroupComponent() {
		this(IgnoCodeLib.getMyParent());
	}
	/**
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 */
	public GroupComponent(PApplet parent) {
		this.parent = parent;
    	this.children = new ArrayList<DisplayComponent>();
    	this.id = DisplayComponent.counter++;
	}
	
	
	/**
	 * Adds a component to children of this component. Overrides DisplayComponent. 
	 * Throws an UnsupportedOperationException if an attempt is made to add a DocumentComponent 
	 * or a LayerComponent. Permits nested groups.
	 * @param component   DisplayComponent to add to this component's children
	 */
	public void add(DisplayComponent component) {
		if (component instanceof DocumentComponent) {
			throw new UnsupportedOperationException("Attempt to add a Document component to a Group.");
		}
		if (component instanceof LayerComponent) {
			throw new UnsupportedOperationException("Attempt to add a Layer component to a Group.");
		}
		if (DocumentComponent.verbose) System.out.println("Adding "+ component.getClass().getSimpleName() 
				+" id "+ component.id +" to group id " + this.id);
		this.children().add(component);
	}

	/**
	 * Adds all components in a list to this component
	 * @param comps   an ArrayList of DisplayComponents
	 */
	public void add(ArrayList<? extends DisplayComponent> comps) {
		for (DisplayComponent component : comps) {
			this.add(component);
		}
	}

	@Override
	public void draw() {
		if (!this.isVisible) return;
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.draw();
		}
	}

	@Override
	public void draw(PGraphics pg) {
		if (!this.isVisible) return;
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.draw(pg);
		}
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public void write(PrintWriter pw) {
		boolean bracketVisible = false;
		boolean bracketLocked = false;
		if (!this.isVisible) {
			AIFileWriter.setVisible(false, pw);
			bracketVisible = true;
		}
		if (this.isLocked) {
			AIFileWriter.setLocked(true, pw);
			bracketLocked = true;
		}
		AIFileWriter.openGroup(pw);
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.write(pw);
		}
		AIFileWriter.closeGroup(pw);
		if (bracketVisible) AIFileWriter.setVisible(true, pw);
		if (bracketLocked) AIFileWriter.setLocked(false, pw);
	}

	@Override
	public void transform(Matrix3 matx) {
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.transform(matx);
		}
	}

	/* (non-Javadoc)
	 * This is a composite node, it visits the children of this component in preorder traversal, 
	 * executing a command on the parent first. 
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor) {
		visitor.visitGroupComponent(this);
		Iterator<DisplayComponent> iter = this.iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.accept(visitor);
		}
	}

	/* (non-Javadoc)
	 * This is a composite node, it visits children first (postorder) if order is true, 
	 * and in preorder if order is false.
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor, boolean order) {
		if (order) {
			Iterator<DisplayComponent> iter = this.iterator();
			while (iter.hasNext()) {
				DisplayComponent component = iter.next();
				component.accept(visitor);
			}
			visitor.visitGroupComponent(this);
		}
		else {
			accept(visitor);
		}
	}


}
