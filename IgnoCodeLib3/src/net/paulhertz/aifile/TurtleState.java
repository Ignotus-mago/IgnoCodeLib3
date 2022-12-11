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

// import net.paulhertz.aifile.TurtleState;

import processing.core.PVector;

/**
 * @author paulhz
 */
/**
 * Stores and manages state of a Turtle: location, angle, pen state, visibility.
 */
public class TurtleState {
	double turtleAngle;
	boolean penDown;
	boolean turtleVisible;
	double turtleX;
	double turtleY;
	
	public TurtleState() {
		initTurtleState();
	}
	
	public TurtleState(TurtleState state) {
		this.copyState(state);
	}
	
	public void initTurtleState() {
		this.turtleAngle = 0;
		this.penDown = true;
		this.turtleVisible = false;
		this.turtleX = 0;
		this.turtleY = 0;
	}

	/**
	 * @return the turtleAngle
	 */
	public double getTurtleAngle() {
		return turtleAngle;
	}

	/**
	 * Returns turtle angle as a normalized PVector.
	 * @return  turtle angle as a normalized PVector
	 */
	public PVector turtleVector() {
		double vx = Math.cos(this.turtleAngle);
		double vy = Math.sin(this.turtleAngle);
		PVector vec = new PVector((float) vx, (float) vy);
		return vec;
	}

	/**
	 * @param turtleAngle the turtleAngle to set
	 */
	public void setTurtleAngle(double turtleAngle) {
		this.turtleAngle = turtleAngle;
	}
	
	public void setTurtleAngle(PVector vec) {
		this.turtleAngle = Math.atan2(vec.y, vec.x);
	}

	/**
	 * @return the penDown
	 */
	public boolean isPenDown() {
		return penDown;
	}

	/**
	 * @param penDown the penDown to set
	 */
	public void setPenDown(boolean penDown) {
		this.penDown = penDown;
	}

	/**
	 * @return the turtleVisible
	 */
	public boolean isTurtleVisible() {
		return turtleVisible;
	}

	/**
	 * @param turtleVisible the turtleVisible to set
	 */
	public void setTurtleVisible(boolean turtleVisible) {
		this.turtleVisible = turtleVisible;
	}

	/**
	 * @return the turtleX
	 */
	public double getTurtleX() {
		return turtleX;
	}

	/**
	 * @param turtleX the turtleX to set
	 */
	public void setTurtleX(double turtleX) {
		this.turtleX = turtleX;
	}

	/**
	 * @return the turtleY
	 */
	public double getTurtleY() {
		return turtleY;
	}

	/**
	 * @param turtleY the turtleY to set
	 */
	public void setTurtleY(double turtleY) {
		this.turtleY = turtleY;
	}
	
	public void copyState(TurtleState state) {
		this.turtleAngle = state.turtleAngle;
		this.turtleX = state.turtleX;
		this.turtleY = state.turtleY;
		this.penDown = state.penDown;
		this.turtleVisible = state.turtleVisible;
	}
		
}
