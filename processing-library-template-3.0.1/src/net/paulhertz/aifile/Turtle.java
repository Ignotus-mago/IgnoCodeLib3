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

import net.paulhertz.aifile.BezShape;
import net.paulhertz.aifile.BezTriangle;
import net.paulhertz.aifile.ColorableINF;
import net.paulhertz.aifile.DisplayComponent;
import net.paulhertz.aifile.GroupComponent;
import net.paulhertz.aifile.Palette;
import net.paulhertz.aifile.TurtleState;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

// TODO visible turtle, circular buffer member class for turtle states

/**
 * Implements a basic 2D TurtleGraphics command set that can be used to draw shapes 
 * in Processing and export them to Adobe Illustrator.
 * <p>
 * A turtle is an imaginary denizen of computer graphics that had its origins in the LOGO language, 
 * which was a subset of LISP used for teaching programming. LOGO was designed for children, but 
 * was a powerful language. Its model of a "local geometry" implemented through the orientation 
 * and location of the turtle offered many intuitive advantages over the "global geometry" used
 * in most computer graphics packages.
 * </p>
 * <p>
 * A turtle is created facing right (east) at the center of the screen or at user-supplied coordinates.
 * It moves across the display upon receiving {@link #forward(double)}, {@link #backward(double)}, 
 * {@link #move(double)} or {@link #moveTo(double, double)} commands, and changes its orientation
 * in response to {@link #left(double)}, {@link #right(double)} and {@link #turn(double)} commands. 
 * It carries with it a "pen" that may be up or down. When the pen is down, the turtle leaves a 
 * mark that can be displayed by calling the turtle's {@link #draw()} command from Processing's draw() method. 
 * A shape (also referred to here as a "trail" or a "turtle trail") is created by moving the turtle
 * while the turtle pen is down ({@link #penDown()} command). A shape is finished by a {@link #penUp()} command.
 * Each shape is stored into an array, the turtleTrails array, which is also drawn by the {@link #draw()} 
 * method. Shapes in the turtleTrails array and the current trail can be deleted with the {@link #clear()} command.
 * </p>
 * <p>
 * The graphics state of trails/shapes is controlled through the commands in the ColorableINF interface,
 * which allow you to get and set fill color, stroke color, and stroke weight. The {@link #setNoFill() setNoFill()}
 * and {@link #setNoStroke()} commands turn off filling and stroking of shapes. Commands apply
 * to the current trail or to new trails, but not to previously stored trails.
 * </p>
 * <p>
 * The location, orientation, pen state and visibility of the turtle are stored in a TurtleState object. The 
 * turtle x-coordinate, turtle y-coordinate, orientation, pen state and visibility can be set directly, by
 * "divine intervention," bypassing the move, moveTo and turn commands. Turtle states can be pushed onto the 
 * turtleStack ({@link #push(TurtleState)}, examined via {@link #peek()} and retrieved and deleted 
 * with {@link #pop()}. The {@link #clearStack()} command clears the stored states. 
 * </p>
 * <p>
 * Shapes can be written to an Adobe Illustrator 7.0 file with the {@link #write(PrintWriter)} command; however, 
 * a preferred and simpler techniques is to call {@link #getTrails()}, which returns a GroupComponent that can be
 * added to a DocumentComponent or a LayerComponent. A DocumentComponent can handle the creation of an Illustrator 
 * document and export geometry to it with its {@link net.paulhertz.aifile.DocumentComponent#write(PrintWriter)} command.
 * </p>
 * 
 * @see net.paulhertz.aifile.DocumentComponent
 * @see net.paulhertz.aifile.LayerComponent 
 * @see net.paulhertz.aifile.ColorableINF
 * 
 */
public class Turtle implements ColorableINF {
	/** x-coordinate of point where a Turtle instance is instantiated. */
	private double homeX;
	/** y-coordinate of point where a Turtle instance is instantiated. */
	private double homeY;
	TurtleState state;
	/** array of stored turtle states (location, angle, pen state, visibility) */
	ArrayList<TurtleState> turtleStack;
	/** accumulate trails as BezShapes */
	ArrayList<BezShape> turtleTrails;
	/** maximum number of trails in turtleTrails, trail insertion will wrap around once the max is reached */
	int maxTrails = 255;
	/** index of most recently entered trail, -1 if no trails have been entered */
	int trailIndex = -1;
	/** current trail */
	BezShape trail;
	/** visible representation of a turtle as an oriented triangle */
	BezShape turtle;
	/** turtle radius */
	public float r = 8.0f;
	/** host PApplet for a Turtle instance */
	PApplet parent;
	/** true if shapes drawn by a Turtle instance are filled, false otherwise */
	boolean hasFill;
	/** true if shapes drawn by a Turtle instance are stroked, false otherwise */
	boolean hasStroke;
	/** true if shapes drawn by a Turtle instance are closed, false otherwise (applied on export to Adobe Illustrator) */
	boolean isClosed;
	/** current color for stroke */
	int strokeColor;
	/** current color for fill */
	int fillColor;
	/** current stroke weight */
	float weight;
	/** useful constant */
	public static double TWOPI = (Math.PI * 2);

	
	/**
	 * Instantiates a Turtle facing right in the center of Processing's display, ready to draw (pen down).
	 * @param parent   a PApplet, typically a reference to the host applet, as in <code>Turtle t = new Turtle(this);</code>
	 */
	public Turtle(PApplet parent) {
		this(parent, parent.width/2.0, parent.height/2.0);
	}
	
	/**
	 * Instantiates a Turtle facing right in the center of Processing's display, ready to draw (pen down).
	 * Obtains a reference to the host PApplet from the initialized library class IgnoCodeLib.
	 * @param x        x-coordinate
	 * @param y        y-coordinate 
	 */
	public Turtle(double x, double y) {
		this(IgnoCodeLib.getMyParent(), x, y);
	}
	
	/**
	 * Instantiates a Turtle facing right in the center of Processing's display, ready to draw (pen down).
	 * Obtains a reference to the host PApplet from the initialized library class IgnoCodeLib.
	 */
	public Turtle() {
		this(IgnoCodeLib.getMyParent());
	}
	
	/**
	 * Instantiates a Turtle facing right at coordinates (x, y), ready to draw (pen down).
	 * @param parent   a PApplet, typically a reference to the host applet, as in <code>Turtle t = new Turtle(this, x, y);</code>
	 * @param x        x-coordinate
	 * @param y        y-coordinate 
	 */
	public Turtle(PApplet parent, double x, double y) {
		this.homeX = x;
		this.homeY = y;
		this.parent = parent;
		this.state = new TurtleState();
		this.turtleStack = new ArrayList<TurtleState>();
		this.turtleTrails = new ArrayList<BezShape>();
		this.setTurtleX(x);
		this.setTurtleY(y);
		this.isClosed = false;
		if (parent.g.fill) {
			this.setFillColor(parent.g.fillColor);
			this.hasFill = true;
		} 
		else {
			this.setNoFill();
		}
		if (parent.g.stroke) {
			this.setStrokeColor(parent.g.strokeColor);
			this.hasStroke = true;
			this.setWeight(parent.g.strokeWeight);
		}
		else {
			this.setNoStroke();
		}
	}

	/**
	 * Gets ready to show the turtle as an oriented triangle on the next draw command.
	 */
	public void show() {
		this.setTurtleVisible(true);
	}

	/**
	 * Gets ready to hide the visible representation of the turtle on the next draw command.
	 */
	public void hide() {
		this.setTurtleVisible(false);
	}
	
	
	private void createTurtle() {
		  float x1 = 0;
		  float y1 = 0;
		  turtle = BezTriangle.makeThreePoints(parent, x1, y1, x1 - r * 2, y1 - r * 0.8f, x1 - r * 2, y1 + r * 0.8f);
		  turtle.setCenter(x1, y1);
		  turtle.setFillColor(parent.color(220));
		  turtle.setStrokeColor(parent.color(0));
	}

	/**
	 * Stops drawing by the turtle and adds the current trail (if there is one) to the 
	 * turtleTrails array. If the number of trails in the turtleTrails array exceeds maxTrails, 
	 * indexing will wrap around and trails will be overwritten. 
	 * The default value of maxTrails is 255.
	 */
	public void penUp() {
		if (null != this.trail) {
			if (size() < maxTrails) {
				trailIndex = (trailIndex + 1);
				turtleTrails.add(this.trail);
				this.trail = null;
			}
			else {
				trailIndex = (trailIndex + 1) % maxTrails;
				turtleTrails.set(trailIndex, this.trail);
				this.trail = null;
			}
		}
		this.setPenDown(false);
	}
	
	/**
	 * Prepares the turtle to create a trail on the next forward, backward, or move command. 
	 * Has no effect if the pen is already down.
	 */
	public void penDown() {
		this.setPenDown(true);
	}
	
	/**
	 * Moves the turtle by a specified distance in the current direction (turtle angle). If the pen is down, 
	 * adds a segment to the current trail or starts a new trail.
	 * @param distance
	 */
	public void move(double distance) {
		double x = this.getTurtleX();
		double y = this.getTurtleY();
		double angle = this.getTurtleAngle();
		double x2 = x + Math.cos(angle) * distance;
		double y2 = y + Math.sin(angle) * distance;
		if (this.isPenDown()) {
			if (null == trail) {
				this.createTrail(x, y);
			}
			trail.append((float) x2, (float) y2);
		}
		this.setTurtleX(x2);
		this.setTurtleY(y2);
	}
	
	
	/**
	 * Moves the turtle to a specified point (x2, y2). If the pen is down, 
	 * adds a segment to the current trail or starts a new trail.
	 * @param x2   the x-coordinate of the point to move to
	 * @param y2   the y-coordinate of the point to move to
	 */
	public void moveTo(double x2, double y2) {
		double x = this.getTurtleX();
		double y = this.getTurtleY();
		if (this.isPenDown()) {
			if (null == trail) {
				this.createTrail(x, y);
			}
			trail.append((float) x2, (float) y2);
		}
		this.setTurtleX(x2);
		this.setTurtleY(y2);
	}

	
	/**
	 * Moves the turtle in the current direction (turtle angle) by a specified distance.
	 * @param distance
	 */
	public void forward(double distance) {
		move(distance);
	}
	
	/**
	 * Moves the turtle in the direction opposite to the current direction (i.e., -turtle angle) 
	 * by a specified distance.
	 * @param distance
	 */
	public void backward(double distance) {
		move(-distance);
	}
	
	/**
	 * Turns the turtle a specified number of radians from its current direction (turtle angle).
	 * @param angle
	 */
	public void turn(double angle) {
		while (angle > TWOPI) {
			angle -= TWOPI;
		}
		double theta = this.getTurtleAngle() + angle;
		if (theta > TWOPI) {
			theta -= TWOPI;
		}
		this.setTurtleAngle(theta);
	}
	
	/**
	 * Turns the turtle to the left (counterclockwise) a specified number of radians 
	 * from its current direction (turtle angle).
	 * @param angle
	 */
	public void left(double angle) {
		turn(-angle);
	}
	
	/**
	 * Turns the turtle to the right (clockwise) a specified number of radians 
	 * from its current direction (turtle angle).
	 * @param angle
	 */
	public void right(double angle) {
		turn(angle);
	}
	
	/**
	 * returns the turtle to its original position with a turtle angle of 0 (facing right).
	 */
	public void home() {
		this.setTurtleX(homeX);
		this.setTurtleY(homeY);
		this.setTurtleAngle(0);
	}
	
	/**
	 * Clears all trails, both those saved in turtleTrails and the current trail.
	 */
	public void clear() {
		this.trail = null;
		this.turtleTrails.clear();
		this.trailIndex = -1;
	}
	
	/**
	 * Draws all trails, both those saved in turtleTrails and the current trail, to the display.
	 * If the turtle is visible, draws the turtle icon.
	 */
	public void draw() {
		for (BezShape bez : this.turtleTrails) {
			bez.draw();
		}
		if (null != this.trail) this.trail.draw();
		if (this.isTurtleVisible()) drawTurtle();
	}
	
	/**
	 * Draws all trails, both those saved in turtleTrails and the current trail, to 
	 * an instonce of PGraphics used as an offscreen buffer. User is responsible for calling 
	 * beginDraw() and endDraw() to enable drawing on the PGraphics instance. 
	 * If the turtle is visible, draws the turtle icon.
	 * @param pg   a PGraphics instance. 
	 */
	public void draw(PGraphics pg) {
		for (BezShape bez : this.turtleTrails) {
			bez.draw(pg);
		}
		if (null != this.trail) this.trail.draw(pg);
		if (this.isTurtleVisible()) drawTurtle();
	}
	
	/**
	 * Draws the current trail only to the display. If the turtle is visible, draws the turtle icon.
	 */
	public void drawCurrent() {
		if (null != this.trail) this.trail.draw();
		if (this.isTurtleVisible()) drawTurtle();
	}
	
	/**
	 * Draws the turtle as an oriented triangle with its point at the current turtle location. 
	 * If turtle is visible and the pen is down, draws a short line in the turtle's 
	 * current stroke color inside the turtle. 
	 */
	private void drawTurtle() {
		if (null == turtle) createTurtle();
		parent.pushMatrix();
		parent.translate((float)this.getTurtleX(), (float)this.getTurtleY());
		parent.rotate((float)this.getTurtleAngle());
		turtle.show();
		turtle.draw();
		if (isPenDown()) {
			parent.pushStyle();
			parent.stroke(this.strokeColor);
			parent.line(0,0,-r * 1.25f, 0);
			parent.popStyle();
		}
		parent.popMatrix();
	}
	
	/**
	 * Writes the geometry of all trails, both those saved in turtleTrails and the current trail, 
	 * in Adobe Illustrator 7.0 format, to the file or output stream specified. It is probably 
	 * simpler just to add the GroupComponent obtained from {@link #getTrails() getTrails} to a 
	 * Document component, as shown in the examples.
	 * @param pw   the output stream (file) to write to. 
	 */
	public void write(PrintWriter pw) {
		for (BezShape bez : this.turtleTrails) {
			bez.write(pw);
		}
		if (null != this.trail) this.trail.write(pw);
	}
	
	/**
	 * Returns a GroupComponent with all the trails created by this Turtle instance.
	 * Trails are ready to add to a DocumentComponent or LayerComponent and export to Adobe Illustrator.
	 * @see net.paulhertz.aifile.LayerComponent#add(DisplayComponent)
	 * @see net.paulhertz.aifile.DocumentComponent#add(DisplayComponent)
	 * @return a GroupComponent with all the trails created by this Turtle instance.
	 */
	public GroupComponent getTrails() {
		GroupComponent group = new GroupComponent(this.parent);
		group.add(turtleTrails);
		if (null != trail) group.add(trail);
		return group;
	}
	
	/**
	 * Creates a new BezShape and stores it in the current trail.
	 * @see net.paulhertz.aifile.BezShape#BezShape(PApplet, float, float)
	 * @param x   starting x-coordinate of trail
	 * @param y   starting y-coordinate of trail
	 */
	private void createTrail(double x, double y) {
		trail = new BezShape(this.parent, (float) x, (float) y, this.isClosed);
		trail.setFillColor(fillColor);
		trail.setStrokeColor(strokeColor);
		trail.setHasFill(hasFill);
		trail.setHasStroke(hasStroke);
		trail.setWeight(weight);
//		PApplet.println("created trail with fill "+ fillColor +", stroke "+ strokeColor 
//				+", weight "+ weight +", hasFill "+ hasFill +", hasStroke "+ hasStroke);
	}

	/**
	 * Returns the turtle stack, an array of turtle states. Useful with L-systems. 
	 * @see net.paulhertz.aifile.TurtleState
	 * @return the turtleStack
	 */
	public ArrayList<TurtleState> getTurtleStack() {
		return turtleStack;
	}

	/**
	 * Returns the list of all stored turtle trails, not including the current trail
	 * if it is still being draw (i.e., no penUp() command has been issued to store it).
	 * @return the turtleTrails
	 */
	public ArrayList<BezShape> getTurtleTrails() {
		return turtleTrails;
	}
	
	/**
	 * @return the maxTrails, maximum number of stored trails
	 */
	public int getMaxTrails() {
		return maxTrails;
	}

	/**
	 * @param maxTrails the maxTrails to set
	 */
	public void setMaxTrails(int maxTrails) {
		this.maxTrails = maxTrails;
	}
		
	/**
	 * @return the trailIndex, index to most recently inserted element in turtleTrails
	 */
	public int getTrailIndex() {
		return trailIndex;
	}
	
	/**
	 * @return the current trail, may be null
	 */
	public BezShape getCurrentTrail() {
		return trail;
	}

	

	/************* Delegated Turtle State Methods *************/
	
	/**
	 * Returns the current turtle angle.
	 * @return current turtle angle
	 * @see net.paulhertz.aifile.TurtleState#getTurtleAngle()
	 */
	public double getTurtleAngle() {
		return state.getTurtleAngle();
	}
	
	/**
	 * Returns current turtle angle as a normalized PVector
	 * @return current turtle angle as a PVector
	 * @see net.paulhertz.aifile.TurtleState#turtleVector()
	 */
	public PVector getTurtleVector() {
		return state.turtleVector();
	}

	/**
	 * Sets the orientation of the turtle by divine intervention.
	 * @param turtleAngle the angle the turtle is facing
	 * @see net.paulhertz.aifile.TurtleState#setTurtleAngle(double)
	 */
	public void setTurtleAngle(double turtleAngle) {
		state.setTurtleAngle(turtleAngle);
	}
	
	/**
	 * Sets the orientation of the turtle from a supplied PVector.
	 * @param vec   a PVector
	 * @see net.paulhertz.aifile.TurtleState#setTurtleAngle(PVector)
	 */
	public void setTurtleAngle(PVector vec) {
		state.setTurtleAngle(vec);
	}

	/**
	 * Returns true if the pen is down (ready to draw), false otherwise.
	 * @return true if the pen is down, false if it is up
	 * @see net.paulhertz.aifile.TurtleState#isPenDown()
	 */
	public boolean isPenDown() {
		return state.isPenDown();
	}

	/**
	 * Sets the current pen state.
	 * @param penDown   pass in true to set the pen down, false to set it up
	 * @see net.paulhertz.aifile.TurtleState#setPenDown(boolean)
	 */
	private void setPenDown(boolean penDown) {
		state.setPenDown(penDown);
	}

	/**
	 * Returns true if turtle is visible, false otherwise. Visibility is not yet implemented.
	 * @return true if turtle is visible, false if it is not
	 * @see net.paulhertz.aifile.TurtleState#isTurtleVisible()
	 */
	public boolean isTurtleVisible() {
		return state.isTurtleVisible();
	}

	/**
	 * Sets the visibility of the Turtle instance. Visibility is not yet implemented.
	 * @param turtleVisible true to set turtle to visible, false to set it to invisible
	 * @see net.paulhertz.aifile.TurtleState#setTurtleVisible(boolean)
	 */
	private void setTurtleVisible(boolean turtleVisible) {
		state.setTurtleVisible(turtleVisible);
	}

	/**
	 * Returns x-coordinate of turtle location.
	 * @return the x-coordinate of the turtle's location
	 * @see net.paulhertz.aifile.TurtleState#getTurtleX()
	 */
	public double getTurtleX() {
		return state.getTurtleX();
	}

	/**
	 * Sets the x-coordinate of the turtle by divine intervention. Does no drawing:
	 * Use {@link #moveTo(double,double) moveTo} instead to do drawing.
	 * @param turtleX x-coordinate to assign to the turtle's location
	 * @see net.paulhertz.aifile.TurtleState#setTurtleX(double)
	 */
	public void setTurtleX(double turtleX) {
		state.setTurtleX(turtleX);
	}

	/**
	 * Returns y-coordinate of turtle location.
	 * @return the y-ccordinate of the turtle's location
	 * @see net.paulhertz.aifile.TurtleState#getTurtleY()
	 */
	public double getTurtleY() {
		return state.getTurtleY();
	}

	/**
	 * Sets the y-coordinate of the turtle by divine intervention. Does no drawing:
	 * Use {@link #moveTo(double,double) moveTo} instead to do drawing.
	 * @param turtleY y-coordinate to assign to the turtle's location
	 * @see net.paulhertz.aifile.TurtleState#setTurtleY(double)
	 */
	public void setTurtleY(double turtleY) {
		state.setTurtleY(turtleY);
	}
	

	/************* Delegated Turtle State Stack Methods *************/
	
	/**
	 * Retrieves, but does not remove, the most recently pushed turtle state, 
	 * or returns null if turtleStack is empty.
	 * @return   most recently pushed turtle state
	 * @see java.util.ArrayList#get(int)
	 */
	public TurtleState peek() {
		return turtleStack.get(turtleStack.size() - 1);
	}

	/**
	 * Retrieves and removes the most recently pushed turtle state.
	 * Throws a NoSuchElementException if the turtleStack is empty.
	 * @return   a TurtleState
	 * @see java.util.ArrayList#remove(int)
	 */
	public TurtleState pop() {
		TurtleState state = turtleStack.get(turtleStack.size() - 1);
		turtleStack.remove(turtleStack.size() - 1);
		return state;
	}

	/**
	 * Pushes a turtle state onto the stack.
	 * @param state   the state to push
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public void push(TurtleState state) {
		turtleStack.add(state);
	}

	/**
	 * Returns the number of elements in the turtle state stack.
	 * @return   number of elements in the turtle state stack
	 * @see java.util.ArrayList#size()
	 */
	public int stackSize() {
		return turtleStack.size();
	}
	
	/**
	 * Clears the turtleStack of all stored states.
	 */
	public void clearStack() {
		this.turtleStack.clear();
	}
	
	

	/************* Turtle Trail Methods *************/
	
	/**
	 * Returns current value of isClosed, true if current trail is a closed shape.
	 * @return   current isClosed value of this Turtle instance
	 * @see net.paulhertz.aifile.BezShape#isClosed()
	 */
	public boolean isClosed() {
		return this.isClosed;
	}

	/**
	 * Sets isClosed of this Turtle instance to supplied value. 
	 * If a trail has been started, sets its isClosed value.
	 * @param newIsClosed
	 * @see net.paulhertz.aifile.BezShape#setIsClosed(boolean)
	 */
	public void setIsClosed(boolean newIsClosed) {
		this.isClosed = newIsClosed;
		if (null != this.trail) trail.setIsClosed(newIsClosed);
	}

	/**
	 * Returns true if current trail is filled, false otherwise.
	 * @return   current hasFill value of this Turtle instance
	 * @see net.paulhertz.aifile.BezShape#hasFill()
	 */
	public boolean hasFill() {
		return this.hasFill;
	}

	/**
	 * Returns the fill color of the current trail.
	 * @return   current fill color of this Turtle instance
	 * @see net.paulhertz.aifile.BezShape#fillColor()
	 */
	public int fillColor() {
		return this.fillColor;
	}

	/**
	 * Sets fill color of this Turtle instance. 
	 * If a trail has been started, sets fill color of trail.
	 * @param newFillColor
	 * @see net.paulhertz.aifile.BezShape#setFillColor(int)
	 */
	public void setFillColor(int newFillColor) {
		this.fillColor = newFillColor;
		this.hasFill = true;
		if (null != this.trail) trail.setFillColor(newFillColor);
	}

	/**
	 * Sets hasFill of this Turtle instance to false. 
	 * If a trail has been started, sets its hasFill to false.
	 * @see net.paulhertz.aifile.BezShape#setNoFill()
	 */
	public void setNoFill() {
		this.hasFill = false;
		if (null != this.trail) trail.setNoFill();
	}

	/**
	 * Returns true if the current trail is stroked. 
	 * @return   current hasStroke value of this Turtle instance.
	 * @see net.paulhertz.aifile.BezShape#hasStroke()
	 */
	public boolean hasStroke() {
		return this.hasStroke;
	}

	/**
	 * Returns current stroke color.
	 * @return   current stroke color of this Turtle instance.
	 * @see net.paulhertz.aifile.BezShape#strokeColor()
	 */
	public int strokeColor() {
		return this.strokeColor;
	}

	/**
	 * Sets stroke color of this Turtle instance. 
	 * If a trail has been started, sets stroke color of trail.
	 * @param newStrokeColor
	 * @see net.paulhertz.aifile.BezShape#setStrokeColor(int)
	 */
	public void setStrokeColor(int newStrokeColor) {
		this.strokeColor = newStrokeColor;
		this.hasStroke = true;
		if (null != this.trail) trail.setStrokeColor(newStrokeColor);
	}

	/**
	 * Sets hasStroke of this Turtle instance to false. 
	 * If a trail has been started, sets its hasStroke to false.
	 * @see net.paulhertz.aifile.BezShape#setNoStroke()
	 */
	public void setNoStroke() {
		this.hasStroke = false;
		if (null != this.trail) trail.setNoStroke();
	}

	/**
	 * Returns opacity component of current fill color, in the range 0..255.
	 * @return   opacity component of fill color of this Turtle instance
	 * @see net.paulhertz.aifile.BezShape#fillOpacity()
	 */
	public int fillOpacity() {
		int[] argb = Palette.argbComponents(this.fillColor);
		return argb[0];
	}

	/**
	 * Sets fill color opacity of this Turtle instance. 
	 * If a trail has been started, sets fill color opacity of trail.
	 * @param opacity   opacity to set (0..255)
	 * @see net.paulhertz.aifile.BezShape#setFillOpacity(int)
	 */
	public void setFillOpacity(int opacity) {
		int[] argb = Palette.argbComponents(this.fillColor);
		this.setFillColor(Palette.composeColor(argb[1], argb[2], argb[3], opacity));
		if (null != this.trail) trail.setFillOpacity(opacity);
	}

	/**
	 * Returns opacity component of current stroke color, in the range 0..255.
	 * @return   opacity component of stroke color of this Turtle instance
	 * @see net.paulhertz.aifile.BezShape#strokeOpacity()
	 */
	public int strokeOpacity() {
		int[] argb = Palette.argbComponents(this.strokeColor);
		return argb[0];
	}

	/**
	 * Sets opacity value of stroke color of this Turtle instance. 
	 * If a trail has been started, sets opacity of stroke color of trail.
	 * @param opacity   opacity to set (0..255)
	 * @see net.paulhertz.aifile.BezShape#setStrokeOpacity(int)
	 */
	public void setStrokeOpacity(int opacity) {
		int[] argb = Palette.argbComponents(this.strokeColor);
		this.setStrokeColor(Palette.composeColor(argb[1], argb[2], argb[3], opacity));
		if (null != this.trail) trail.setStrokeOpacity(opacity);
	}

	/**
	 * Returns weight of current stroke.
	 * @return   value of stroke weight of this Turtle instance
	 */
	public float weight() {
		return this.weight;
	}

	/**
	 * Sets value of stroke weight of this Turtle instance. 
	 * If a trail has been started, sets stroke weight of trail.
	 * @param newWeight
	 * @see net.paulhertz.aifile.BezShape#setWeight(float)
	 */
	public void setWeight(float newWeight) {
		this.weight = newWeight;
		if (null != this.trail) trail.setWeight(newWeight);
	}
	
	/**
	 * Returns number of vertices in the current trail.
	 * Returns 0 if the current trail is null.
	 * @return the number of vertices in the current trail
	 */
	public int trailSize() {
		if (null == this.trail) {
			return 0;
		}
		return trail.curves().size();
	}
	
	/************* Delegated Trail Array Methods *************/
	
	/**
	 * Gets trail (BezShape) at suppled index. Throws an error if index is out of bounds.
	 * @param index
	 * @return the BezShape located at the supplied index
	 * @see java.util.ArrayList#get(int)
	 */
	public BezShape get(int index) {
		return turtleTrails.get(index);
	}

	/**
	 * Returns true if there are no shapes stored in turtleTrails array.
	 * @return true if turtleTrails array is empty, false otherwise.
	 * @see java.util.ArrayList#isEmpty()
	 */
	public boolean isEmpty() {
		return turtleTrails.isEmpty();
	}
	
	/**
	 * Returns true if maximum number of trails have been stored in turtleTrails array.false otherwise.
	 * @return   true if the turtleTrails array holds maxTrails number of elements, false otherwise.
	 */
	public boolean isFull() {
		return (turtleTrails.size() == maxTrails);
	}

	/**
	 * Returns an iterator over the stored turtle trails. 
	 * @return and Interator that can step through the turtleTrails array.
	 * @see java.util.AbstractList#iterator()
	 */
	public Iterator<BezShape> iterator() {
		return turtleTrails.iterator();
	}

	/**
	 * Returns number of elements in the turtleTrails array.
	 * @return   size of the turtleTrails array.
	 * @see java.util.ArrayList#size()
	 */
	public int size() {
		return turtleTrails.size();
	}	
		
}
