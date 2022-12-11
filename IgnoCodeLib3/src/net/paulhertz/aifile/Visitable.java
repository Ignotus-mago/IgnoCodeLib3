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

import net.paulhertz.aifile.ComponentVisitor;

/**
 * Interface for display components that implement the Visitor design pattern.
 *
 * <p>The Visitor design pattern represents operations to be performed on the elements of 
 * an object structure. The pattern encapsulates operations into methods separate from
 * the structure, which only needs to accept the visitor and allow it to access its state,
 * generally by passing back a reference to itself to an overloaded method tailored to the 
 * class of the visited instance.</p>
 *
 *  abstract class for Visitors.
 *
 */
public interface Visitable {
	/** 
	 * Accepts a ComponentVisitor that traverses a document structure tree.
	 *
	 * <p>
	 * When called from an object that implements the <code>Visitable</code> interface:</p>
	 * <ol>
	 * <li>each visited object passes a reference to itself back to the visitor </li>
	 * <li>each visited object calls <code>accept( visitor )</code> on all its children </li>
	 * </ol>
	 * <p>
	 * The reference is passed back to the visitor through a method of the form 
	 * visitor.visit&lt;ComponentClassName&gt;( this );
	 * See {@link #net.paulhertz.aifile.ComponentVisitor ComponentVisitor}
	 * </p>
	 * <p>
	 * The order of steps 1 and 2 determines if traversal of the composite 
	 * structure is preorder or postorder. As shown above, it's preorder.
	 * Depending on what you want to do, one traversal may be better suited than
	 * the other. Preorder visits parents first, postorder visits children first.
	 * </p>
	 * @param visitor    a {@code ComponentVisitor}
	 */
	public void accept( ComponentVisitor visitor );
	/** 
	 * Accepts a ComponentVisitor that traverses a document structure tree in preorder or postorder.
	 * @param visitor    a {@link net.paulhertz.aifile.ComponentVisitor ComponentVisitor}
	 * @param order      boolean to determine if traversal is preorder or postorder
	 *                   <code>accept( visitor )</code> should implement the default order of traversal, 
	 *                   <code>accept( visitor, &lt;false&gt;</code> ) should implement the other order.
	 */
	public void accept( ComponentVisitor visitor, boolean order );
	
}
