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

import net.paulhertz.aifile.Vertex2DINF;

import processing.core.*;

/**
 * @author paulhz
 */
/**
 * Interface for line and curve vertices. 
 * TODO provide drawing commands for display of anchor points and direction points
 *
 */
public interface Vertex2DINF {
	/**
	 * @return x-coordinate as a float
	 */
	public float x();
	/**
	 * @return y-coordinate as a float
	 */
	public float y();
	/**
	 * @return type of segment, either BezShape.LINE_SEGMENT or BezShape.CURVE_SEGMENT
	 */
	public int segmentType();
	/**
	 * @return coordinates as an array of float
	 */
	public float[] coords();
	/**
	 * @return a deep copy of a Vertex2DINF
	 */
	public Vertex2DINF clone();
	/** 
	 * Draws a path to the display. It is only valid to call this within a 
	 * Processing beginShape/endShape pair where  an initial 
	 * vertex has been set with a call to vertex(). 
	 * @param parent   the PApplet that handles drawing
	 */
	public void draw(PApplet parent);
	/**
	 * Draws a path to an offscreen buffer. It is only valid to call this within a 
	 * Processing beginShape/endShape pair where  an initial 
	 * vertex has been set with a call to vertex(). 
	 * @param pg   a PGraphics instance
	 */
	public void draw(PGraphics pg);
  /**
   * Draws marks at vertices and control points to the display.
   *
   */
  public void mark(PGraphics pg);
  /**
   * Draws marks at vertices and control points to an offscreen buffer.
   *
   */
  public void mark(PApplet parent);
	/**
	 * Writes a path segment to file. Multiple calls to this method should be bracketed 
	 * by AIFileWriter.psMoveTo and AIFileWriter.paintPath commands 
	 * @param output   a PrintWriter for output to file
	 */
	public void write(PrintWriter output);
}
