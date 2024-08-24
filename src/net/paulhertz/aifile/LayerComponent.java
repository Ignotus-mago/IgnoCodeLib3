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
import java.util.*;

import net.paulhertz.geom.Matrix3;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @author Paul Hertz
 * 
 */
/** 
 * Instantiates a Layer component, immediately below the document (root) level. 
 * Layers can only be created as children of a DocumentComponent. 
 * Attempting to insert them elsewhere will lead to an <code>UnsupportedOperationException</code>. 
 * At the moment, we set the layer to be visible, previewed, enabled, and printing
 * and not dimmed or containing multi-layer masks. Only visibility can be changed, 
 * and only for drawing, at the moment. 
 * The colorIndex can be a number from 0 to 26, at the moment there is no provision for custom colors.
 * The layer name can be anything you want. 
 * 
 * @example DocumentExport
 */
public class LayerComponent extends DisplayComponent {
	/** name of the layer, assigned automatically if not set explicitly */
	protected String name;
	/** color index of layer, assigned automatically if not set explicitly */
	protected int colorIndex;
	/** counter for layers, used to automate assignment of {@code name} and {@code colorIndex} */
	private static int layerCounter = 1;
	
    /**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
     * @param name     Name of this layer, shown in the AI Layers palette
     * @param colorIndex   number from 0 to 26, color to use for layer selections and Layers palette in AI
     */
    public LayerComponent(String name, int colorIndex) {
    	this(IgnoCodeLib.getMyParent(), name, colorIndex);
    }
    /**
     * @param parent   PApplet used for calls to the Processing environment
     * @param name     Name of this layer, shown in the AI Layers palette
     * @param colorIndex   number from 0 to 26, color to use for layer selections and Layers palette in AI
     */
    public LayerComponent(PApplet parent,String name, int colorIndex) {
    	this.parent = parent;
    	this.name = name;
    	this.colorIndex = colorIndex;
    	this.children = new ArrayList<DisplayComponent>();
    	this.id = DisplayComponent.counter++;
     }
    /**
     * @param parent   PApplet used for calls to the Processing environment
     */
    public LayerComponent(PApplet parent) {
    	this(parent, "", 1);
   		this.setName("Layer " + LayerComponent.layerCounter++);
    }
    /**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
     */
    public LayerComponent() {
    	this(IgnoCodeLib.getMyParent(), "", 1);
   		this.setName("Layer " + LayerComponent.layerCounter++);
    }
    /**
     * @param parent   PApplet used for calls to the Processing environment
     * @param name     Name of this layer, shown in the AI Layers palette
     */
    public LayerComponent(PApplet parent, String name) {
    	this(parent, name, 1);
    }
    /**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
     * @param name     Name of this layer, shown in the AI Layers palette
     */
    public LayerComponent(String name) {
    	this(IgnoCodeLib.getMyParent(), name, 1);
    }
   

	/**
	 * Returns the namd of this layer.
	 * @return   the layer name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of this layer.
	 * @param name   the name of the layer to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Returns the color index. 
	 * @return the colorIndex
	 */
	public int getColorIndex() {
		return colorIndex;
	}
	/**
	 * Sets the color index, used in Illustrator's Layers palette.
	 * @param colorIndex    the colorIndex to set, should be a number from 0 to 26.
	 * Light Blue = 0 Yellow = 4 Black = 8 Tan = 12
	 * Dark Blue = 16 Olive Green = 20 Ochre = 24
	 * Red = 1, Magenta = 5, Orange = 9, Brown = 13, Pink = 17, Peach = 21, Purple = 25,
	 * Green = 2, Cyan = 6, Dark Green = 10, Violet = 14, Lavender = 18, Burgundy = 22, Light Gray = 26,
	 * Blue = 3, Gray = 7, Teal = 11, Gold = 15, Brick Red = 19, Grass Green = 23
	 */
	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}
	
	
	/**
	 * Adds a component to children() of this component. Overrides DisplayComponent. 
	 * Throws an UnsupportedOperationException if an attempt is made to add a DocumentComponent 
	 * or a LayerComponent. 
	 * @param component   DisplayComponent to add to this component's children
	 */
	public void add(DisplayComponent component) {
		if (component instanceof DocumentComponent) {
			throw new UnsupportedOperationException("Attempt to add a Document component to a Layer.");
		}
		if (component instanceof LayerComponent) {
			throw new UnsupportedOperationException("Attempt to add a Layer component to a Layer: nested layers are currently unsupported.");
		}
		if (DocumentComponent.verbose) System.out.println("adding "+ component.getClass().getSimpleName() +
				" id "+ component.id +" to Layer id "+ this.id +" \""+ this.getName() + "\"");
		this.children().add(component);
		component.setParentComponent(this);
	}

	/**
	 * Adds all components in a list to this document
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
		AIFileWriter.beginLayer(this.name, this.colorIndex, this.isVisible, this.isLocked, pw);
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.write(pw);
		}
		AIFileWriter.endLayer(pw);
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
		visitor.visitLayerComponent(this);
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
			visitor.visitLayerComponent(this);
		}
		else {
			accept(visitor);
		}
	}

}
