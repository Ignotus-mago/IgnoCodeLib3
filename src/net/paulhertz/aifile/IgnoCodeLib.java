/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package net.paulhertz.aifile;


import processing.core.PApplet;

/**
 * IgnoCodeLib library initializer, library information source.
 * IgnoCodeLib is initialized with a reference to the host PApplet, 
 * which it makes available to other classes through {@link #getMyParent getMyParent}.
 * 
 * @example Hello 
 *
 *
 */

public class IgnoCodeLib {
	// myParent is a reference to the parent sketch
	private static PApplet myParent;

	public final static String VERSION = "##library.prettyVersion##";
	

	/**
	 * Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library. However, in its current form, IgnoCodeLib 
	 * does not require initialization. It does require you to pass a reference to 
	 * a PApplet to some of its classes. 
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public IgnoCodeLib(PApplet theParent) {
		myParent = theParent;
		welcome();
	}
	
	
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	
	
	public String sayHello() {
		return "Hello, IgnoCodeLib.";
	}
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}


	/**
	 * @return the myParent
	 */
	protected static PApplet getMyParent() {
		if (null == myParent) {
			throw new NullPointerException("IgnoCodeLib must be initialized with a reference to a host PApplet.");
		}
		return myParent;
	}

}

