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


/**
 * Abstract class for concrete Visitors to classes that implement the Visitable interface.
 * The Visitor design pattern enables commands to be carried out over the members 
 * of a heterogeneous hierarchy, such as a document or display list. 
 * All methods are empty shells. In your subclass override only those 
 * methods that interest you. 
 */
public abstract class ComponentVisitor {
	
	/**
	 * visits a DocumentComponent node
	 * @param comp   a DocumentComponent instance
	 */
	public void visitDocumentComponent(DocumentComponent comp) {
		// EMPTY METHOD
	}

	/**
	 * visits a LayerComponent node
	 * @param comp   a LayerComponent instance
	 */
	public void visitLayerComponent(LayerComponent comp) {
		// EMPTY METHOD
	}

	/**
	 * visits a GroupComponent node
	 * @param comp   a GroupComponent instance
	 */
	public void visitGroupComponent(GroupComponent comp) {
		// EMPTY METHOD
	}

	/**
	 * visits a CustomComponent node
	 * @param comp   a CustomComponent instance
	 */
	public void visitCustomComponent(CustomComponent comp) {
		// EMPTY METHOD
	}

	/**
	 * visits a BezShape node
	 * @param comp   a BezShape instance
	 */
	public void visitBezShape(BezShape comp) {
		// EMPTY METHOD
	}

	/**
	 * visits a PointText node
	 * @param comp   a PointText instance
	 */
	public void visitPointText(PointText comp) {
		// EMPTY METHOD
	}

}
