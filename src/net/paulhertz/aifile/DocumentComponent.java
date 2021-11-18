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

import java.awt.Rectangle;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import net.paulhertz.geom.Matrix3;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Handles the root level of the document hierarchy. All the children of a document component are layer components. 
 * Any other components you add will be placed in the default layer.  A document hierarchy can have only one {@code DocumentComponent},
 * at the top of the hierarchy. The document can act as a display list, by cascading {@link #draw()} commands
 * down the document tree, or as a file output list, similarly cascading the {@link #write(PrintWriter)} command. 
 * Processing and Adobe Illustrator use different coordinate systems. To 
 * 
 * See code example DocumentExport
 */
public class DocumentComponent extends DisplayComponent {
	protected String title; 
	protected String creator = ""; 
	protected String org = ""; 
	protected int width = 612; 
	protected int height = 792; 
	protected Rectangle bbox;
	protected Palette palette;
	protected LayerComponent defaultLayer;
	protected static boolean verbose = false;
	private Matrix3 aiTransform;
	
    // TODO consider whether we automatically run aiTransform, bracketing any write statement that outputs geometry
    /**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
     */
    public DocumentComponent() {
    	this(IgnoCodeLib.getMyParent(), "Untitled");
    }
    /**
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
     */
    public DocumentComponent(PApplet parent) {
    	this(parent, "Untitled");
    }
    /**
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
     * @param title	   Title stored in the document header
     */
    public DocumentComponent(PApplet parent, String title) {
    	this.title = title;
    	this.parentComponent = null;
    	this.children = new ArrayList<DisplayComponent>();
    	this.id = DisplayComponent.counter++;
    }
    /**
	 * PApplet reference is obtained from initialized {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}.
     * @param title	   Title stored in the document header
     */
    public DocumentComponent(String title) {
     	this(IgnoCodeLib.getMyParent(), title);
    }
      

	/**
	 * Returns the document title, used in the document header.
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Set the document title, used in the document header.
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	/**
	 * Returns the document creator, used in the document header. 
	 * Typically this is set to the name of the person who created the document. 
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * Sets the document creator, used in the document header.  
	 * Typically this is the name of the person who created the document. 
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
	/**
	 * Returns the org (organization), used in the document header.
	 * @return the org
	 */
	public String getOrg() {
		return org;
	}
	/**
	 * Sets the org (organization), used in the document header.
	 * @param org the org to set
	 */
	public void setOrg(String org) {
		this.org = org;
	}
	
	
	/**
	 * Returns the width (in points) of this document will have when exported. 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Sets the width of this document when it is exported. Often this 
	 * is the pixel width of the display window in Processing, but it could be, for 
	 * example, the width of an A4 page (595.28 x 841.89 points).
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	
	/**
	 * Returns the height (in points) this document will have when exported.
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Sets the height of this document when it is exported. Often this 
	 * is the pixel height of the display window in Processing, but it could be, for 
	 * example, the width of a U.S. Letter (612 x 792 points).
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	
	/**
	 * Returns the bounding box of this document.
	 * @return the bbox
	 */
	public Rectangle getBbox() {
		if (null == this.bbox) {
			this.bbox = new Rectangle(width, height);
		}
		return bbox;
	}
	/**
	 * Sets the bounding box of this document.
	 * @param bbox the bbox to set
	 */
	public void setBbox(Rectangle bbox) {
		this.bbox = bbox;
	}
	
	
	/**
	 * Returns the palette associated with this document.
	 * @return the palette
	 */
	public Palette getPalette() {
		if (null == this.palette) {
			this.palette = new Palette();
		}
		return palette;
	}
	/**
	 * Sets the palette associated with this document.
	 * @param palette the palette to set
	 */
	public void setPalette(Palette palette) {
		this.palette = palette;
	}
	

	/**
	 * Returns <code>true</code> if debugging information is requested.
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return DocumentComponent.verbose;
	}
	/**
	 * Set verbose to true to get some debugging information, default is false
	 * @param verbose the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		DocumentComponent.verbose = verbose;
	}
	
	
	/**
	 * Returns the default layer. This is the top layer, where all content not assigned to 
	 * another layer will go. 
	 * @return the defaultLayer
	 */
	public LayerComponent getDefaultLayer() {
		if (null == defaultLayer) {
	    	this.defaultLayer = new LayerComponent(parent);
	    	this.add(defaultLayer);
		}
		return defaultLayer;
	}
	/**
	 * Sets the default layer to a user-specified layer. Generally not much need to do this. 
	 * @param defaultLayer the defaultLayer to set
	 */
	public void setDefaultLayer(LayerComponent defaultLayer) {
		this.defaultLayer = defaultLayer;
	}

	
	/**
	 * Adds a component to children() of this component. Overrides DisplayComponent. 
	 * Throws an UnsupportedOperationException if an attempt is made to add a DocumentComponent. 
	 * If component is not a LayerComponent, adds it to the default layer.
	 * @param component   DisplayComponent to add to this component's children
	 * @throws UnsupportedOperationException
	 */
	public void add(DisplayComponent component) {
		// System.out.println("component name = " + component.getClass().getSimpleName());
		if (component instanceof DocumentComponent) {
			throw new UnsupportedOperationException("Attempt to add a Document component to a Document.");
		}
		if (component instanceof LayerComponent) {
			if (DocumentComponent.verbose) System.out.println("adding Layer id "+ 
					((LayerComponent)component).id +" \""+ ((LayerComponent)component).getName() +"\" to Document");
			this.children().add(component);
		}
		else {
			this.getDefaultLayer().add(component);
		}
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
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.draw();
		}
	}
	
	@Override
	public void draw(PGraphics pg) {
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.draw(pg);
		}
	}


	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.DisplayComponent#isLocked()
	 * @return false, document component is never locked
	 */
	@Override
	public boolean isLocked() {
		return false;
	}
	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.DisplayComponent#isVisible()
	 * @return true, document component is always visible
	 */
	@Override
	public boolean isVisible() {
		return true;
	}
	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.DisplayComponent#setLocked(boolean)
	 * ignored, document component is never locked
	 */
	@Override
	public void setLocked(boolean isLocked) {
		;
	}
	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.DisplayComponent#setVisible(boolean)
	 * ignored, document component is always visible
	 */
	@Override
	public void setVisible(boolean isVisible) {
		;
	}
	@Override
	public boolean isTerminal() {
		return false;
	}

	/** 
	 * @see net.paulhertz.aifile.DisplayComponent#write(java.io.PrintWriter)
	 * Writes the document hierarchy to the supplied file. Performs a transform
	 * from Processing coordinate system to Illustrator coordinate system on all
	 * geometry to make the file have the same appearance as the display.
	 */
	@Override
	public void write(PrintWriter pw) {
		writeHeader(pw);
		runAITransform();
		writeDisplayList(pw);
		runAITransform();
		writeTrailer(pw);
	}
	/** 
	 * Writes the document hierarchy to the supplied file. Performs a transform
	 * from Processing coordinate system to Illustrator coordinate system on all
	 * geometry to make the file have the same appearance as the display.
	 * Here for historical reasons.
	 */
	public void writeWithAITransform(PrintWriter pw) {
		writeHeader(pw);
		runAITransform();
		writeDisplayList(pw);
		runAITransform();
		writeTrailer(pw);
	}
	/** 
	 * @see net.paulhertz.aifile.DisplayComponent#write(java.io.PrintWriter)
	 * Writes the document hierarchy to the supplied file without a transform.
	 * Image will be upside-down and reflected with respect to the display. 
	 */
	public void writeNoTransform(PrintWriter pw) {
		writeHeader(pw);
		writeDisplayList(pw);
		writeTrailer(pw);
	}

	
	/**
	 * Writes header portion of an Adobe Illustrator file, including palette.
	 * @param pw PrintWriter for output
	 */
	public void writeHeader(PrintWriter pw) {
	    AIFileWriter.writeHeader(pw, title, creator, org, width, height, getBbox());
	    AIFileWriter.writeState(pw);
	    if (null != this.palette) {
	    	this.palette.write(pw);
	    }
	    else {
	    	this.palette = new Palette();
	    	this.palette.addBlackWhiteGray();
	    	this.palette.write(pw);
	    }
	}
	
	/**
	 * Writes all the children of this document. Called internally by the {@code write()} method.
	 * Unlike the {@code write()} method, this method does not perform a transform to make the 
	 * geometry in the file have the same orientation as the geometry on the computer display. 
	 * @param pw PrintWriter for output
	 */
	public void writeDisplayList(PrintWriter pw) {
		Iterator<DisplayComponent> iter = this.children().iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.write(pw);
		}
	}
	/**
	 * Writes display list (middle) portion of an Adobe Illustrator file. You can pass
	 * this method an ArrayList of {@link net.paulhertz.aifile.BezShape BezShape} and 
	 * other components and it will write them out for you. Does not transform the
	 * geometry of the ArrayList. 
	 * @param comps   an {@code ArrayList<DisplayComponent>} 
	 * @param pw PrintWriter for output
	 */
	public void writeDisplayList(ArrayList<DisplayComponent> comps, PrintWriter pw) {
		Iterator<DisplayComponent> iter = comps.iterator();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.write(pw);
		}
	}
	/**
	 * Writes display list (middle) portion of an Adobe Illustrator file. You can pass
	 * this method an ArrayList of {@link net.paulhertz.aifile.BezShape BezShape} and 
	 * other components and it will write them out for you. Uses the matrix generated by 
	 * the document's {@code getAITransform()} method to transform the geometry in the ArrayList
	 * before writing it to file, then transforms it back to its original position. This ensures 
	 * that the geometry in the file and the geometry on the display have the same orientation.
	 * @param comps   an {@code ArrayList<DisplayComponent>} 
	 * @param pw PrintWriter for output
	 */
	public void writeDisplayListWithTransform(ArrayList<DisplayComponent> comps, PrintWriter pw) {
		Iterator<DisplayComponent> iter = comps.iterator();
		Matrix3 matx = this.getAITransform();
		while (iter.hasNext()) {
			DisplayComponent component = iter.next();
			component.transform(matx);
			component.write(pw);
			component.transform(matx);
		}
	}

	/**
	 * Writes trailer portion of an Adobe Illustrator file, flushes and closes output.
	 * @param pw PrintWriter for output
	 */
	public void writeTrailer(PrintWriter pw) {
		AIFileWriter.writeTrailer(pw);
		pw.flush();
		pw.close();
	}

	/**
	 * Creates a transform to go from Processing coordinate system to Illustrator coordinate system.
	 * Call after you have set document height. 
	 * The transform is symmetrical, calling it twice sets things back the way they were.
	 * The {@code write()} method handles the transform between coordinates systems for you.
	 * 
	 */
	public Matrix3 getAITransform() {
		// start with the identity matrix
		aiTransform = new Matrix3();
		// add a horizontal reflection around x = 0
		aiTransform.scaleCTM(1.0, -1.0);
		// and translate by "height" distance on the y-axis
		aiTransform.translateCTM(0, height);
		return aiTransform;
	}

	/**
	 * Initializes and runs the transform used to go from Processing coordinate system to Illustrator 
	 * coordinate system. Called internally by the {@code write()} method.
	 */
	private void runAITransform() {
		this.transform(this.getAITransform());
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
		visitor.visitDocumentComponent(this);
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
			visitor.visitDocumentComponent(this);
		}
		else {
			accept(visitor);
		}
	}

}
