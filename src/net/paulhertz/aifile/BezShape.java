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

import java.util.*;
import java.awt.geom.*;
import java.io.PrintWriter;

import net.paulhertz.geom.GeomUtils;
import net.paulhertz.geom.Matrix3;

/* internal imports */
import net.paulhertz.aifile.AIFileWriter;
import net.paulhertz.aifile.BezCircle;
import net.paulhertz.aifile.BezCurve;
import net.paulhertz.aifile.BezCurveShape;
import net.paulhertz.aifile.BezEllipse;
import net.paulhertz.aifile.BezLine;
import net.paulhertz.aifile.BezMultiCurve;
import net.paulhertz.aifile.BezMultiLine;
import net.paulhertz.aifile.BezPoly;
import net.paulhertz.aifile.BezRectangle;
import net.paulhertz.aifile.BezRegularPoly;
import net.paulhertz.aifile.BezShape;
import net.paulhertz.aifile.BezTriangle;
import net.paulhertz.aifile.BezVertex;
import net.paulhertz.aifile.ColorableINF;
import net.paulhertz.aifile.ComponentVisitor;
import net.paulhertz.aifile.DisplayComponent;
import net.paulhertz.aifile.LineVertex;
import net.paulhertz.aifile.Palette;
import net.paulhertz.aifile.Vertex2DINF;

import processing.core.*;

/**
 * @author Paul Hertz
 */ 
/** 
 * Implements shapes composed of cubic Bezier curves and lines and methods to draw geometry to a display, 
 * perform geometric transforms, and write geometry to an Adobe Illustrator 7.0 file format.
 * 
 * <p>The default initializations create a shape with the current fill, stroke, and stroke weight in Processing, 
 * closed unless you specify otherwise. You can call {@link #setColors(PApplet) setColors}  at any time
 * to capture Processing's current fill, stroke, and strokeWeight, and call {@link #setIsClosed(boolean) setIsClosed}
 * with a value of <code>false</code> to make an open shape. Unlike Processing, closed shapes 
 * will be drawn as closed only if the start point and the end point are equal: this is required
 * for correct output to Illustrator. You can also set fill, stroke, and weight with BezShape's own methods 
 * {@link #setFillColor(int) setFillColor}, {@link #setStrokeColor(int) setStrokeColor} and {@link #setWeight(float) setWeight}.</p>
 * 
 * <p>Geometry is stored as an initial vertex (startPoint) and a list of vertices ({@link net.paulhertz.aifile.Vertex2DINF Vertex2DINF}), which may be lines or Bezier curves.
 * The {@link #curveIterator() curveIterator} method returns an iterator over the vertices: note that the initial vertex is
 * not included, but is easily accessed with {@link #x() x} and {@link #y() y}. The center of the shape is initially set 
 * to (0,0), as it would be in Processing. You can set the center to the center point of the geometry with a call to 
 * {@link #calculateCenter() calculateCenter} or to any desired point with {@link #setCenter(float, float) setCenter}.</p>
 * 
 * <p>Geometric transforms around the current center point can be executed with calls to 
 * {@link #translateShape(float, float) translateShape}, {@link #rotateShape(float) rotateShape} and {@link #scaleShape(float) scaleShape}.
 * Generalized transforms around an arbitrary point can be executed by passing a transformation matrix to {@link #transform(Matrix3) transform}.
 * Calling <code>transform()</code> with no argument uses the most recent transformation matrix, effectively repeating the previous 
 * call to {@link #transform(Matrix3) transform}. On initialization, Processing's current transform is store in <code>ctm</code>,
 * but it is not used to transform the shape. You can retrieve <code>ctm</code> with {@link #getCtm() getCtm}.
 * A call to {@link #getMatrix(PApplet) getMatrix(PApplet)} will set the internal matrix 
 * to Processing's current transformation matrix. This way you can synchronize BezShape transforms 
 * to Processing transforms. BezShape transforms are affected by Processing's current transforms 
 * (i.e., by calls to translate, rotate, and scale). Calling a BezShape transform while a Processing transform 
 * is active may lead to unexpected results. See sample code for examples of 
 * how to use and synchronize transforms.
 * </p>
 * 
 * <p>The {@link #write(PrintWriter) write()} method writes the geometry and attributes of a shape in Adobe Illustrator 7.0 format. 
 * It is far easier to call {@link #write(PrintWriter) write()} than to call AIFileWriter directly to output geometry. See the 
 * various coding examples for different methods of writing an Illustrator file. In some instances, one line of code will do all the work.
 * </p>
 * 
 * <p>Static methods provide automatic creation of multiCurves, multiLines, regular polygons, equilateral triangles, general triangles, 
 * rectangles, lines, curves, multilines and multicurves. These may become subclasses in the future.</p>
 *  
 * @example SimpleShapeExport 
 * @example TransformShapeExport
 * 
 */
public class BezShape extends DisplayComponent implements ColorableINF {
	// TODO store initial vertex and center point as LineVertex (DONE)
	// TODO except static methods and constructors, methods do not require a PApplet argument.
	//      older methods that did require a PApplet are still available, but may be deprecated in the future. (DONE)
	// TODO full set of primitives using factories and hooks to primitives (DONE, more may be added in future, 
	//      i.e., blob, brushstroke). Consider implementing these methods in all primitives: getCoords (OK), 
	//      getArea, inset, contains, overlap, isCCW, reverse
	// TODO reset xcoords and ycoords to null after a transform of any kind (DONE)
	// TODO cache boundsRect and set to null after a transform (DONE)
	// TODO calculate more accurate center from xcoords and ycoords (? necessary ?)
	// TODO show/hide layers and groups (DONE)
	// TODO display vertices
	// TODO capture Processing stroke and cap state
	// ---- good to here for next release version ----
	// TODO assign transparency to layers and groups
	// TODO Bezier curve methods (split, intersect, subdivide, etc.)
	// TODO TreeWalker class, Command class
	// TODO export and import XML format
	// TODO consider implementing equals() for BezShape
	/** initial x-coordinate */
	protected float x;
	/** initial y-coordinate */
	protected float y;
	/** x-coordinate of center of transformation */
	protected float xctr;
	/** y-coordinate of center of transformation */
	protected float yctr;
	/** flags if shape has a center point set */
	protected boolean hasCenter;
	/** list of bezier vertices */
	private ArrayList<Vertex2DINF> curves;
	/** flags if shape is closed or not */
	protected boolean isClosed;
	/** flags if shape is filled or not */
	protected boolean hasFill;
	/** flags if shape has a stroke or not */
	protected boolean hasStroke;
	/** flags that we should draw control points and vertices */
	protected boolean isMarked = false;
	/** fill color for shape */
	protected int fillColor;
	/** stroke color for shape */
	protected int strokeColor;
	/** stroke weight for shape */
	protected float weight;
	/** x-coordinate array */
	private float[] xcoords;
	/** y-coordinate array */
	private float[] ycoords;
	/** default number of steps in curve segment as polyline */
	protected int polySteps = 16;
	/** bounding rectangle of this shape */
	private BezRectangle boundsRect;
	/** flag for line segment type, associated with LineVertex */
	public final static int LINE_SEGMENT = 1;
	/** flag for curve segment type, associated with BezVertex */
	public final static int CURVE_SEGMENT = 2;
	/** type of BEZ_PATH, BEZ_TRIANGLE, BEZ_CIRCLE, BEZ_ELLIPSE, BEZ_CURVE_POLY, BEZ_REGULAR_POLY, etc. */
	public BezType bezType;
	/**  */
	public static enum BezType {BEZ_PATH, BEZ_TRIANGLE, BEZ_CIRCLE, BEZ_ELLIPSE, BEZ_CURVE_POLY, BEZ_REGULAR_POLY, 
		                        BEZ_LINE, BEZ_CURVE, BEZ_MULTILINE, BEZ_MULTICURVE, BEZ_RECTANGLE, BEZ_POLY};
	/** 
	 *  KAPPA = (distance between Bezier anchor and its associated control point) / (circle radius)
	 *  when a circle is divided into 4 sectors of 90 degrees, approximately 0.5522847498.
	 *  see <a href="http://www.whizkidtech.redprince.net/bezier/circle/kappa/">http://www.whizkidtech.redprince.net/bezier/circle/kappa/</a>
	 */
	public final static double KAPPA = 0.5522847498;
  /**
   * LAMBDA = KAPPA/√2, a value for weighting Bezier splines based on the length of line segments between anchor points
   * derived from the ratio of the chord of a quarter circle to KAPPA, LAMBDA = KAPPA * (1/√2), about 0.5522847498
   *
   */
  public final static double LAMBDA = 0.39052429175;
	/** The most recent transform, set by calls to {@link #transform(Matrix3) transform} */
	protected Matrix3 ctm; 

	
	// TODO All constructors call this bottleneck constructor (DONE!)
	/**
	 * Creates a BezShape with initial point x,y, closed or open according to the value of isClosed.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform.
	 *  
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 * @param x		x-coordinate of initial point
	 * @param y		y-coordinate of initial point
	 * @param isClosed   true if shape is closed, false if it is open
	 */
	public BezShape(PApplet parent, float x, float y, boolean isClosed) {
		this.parent = parent;
		this.setStartPoint(new LineVertex(x, y));
		this.setCenter(new LineVertex(0, 0));
		this.curves = new ArrayList<Vertex2DINF>();
		this.isClosed = isClosed;
		this.setColors();
		this.setCtm(parent);
		this.setBezType(BezType.BEZ_PATH);
	   	this.id = DisplayComponent.counter++;
	}

	
    /**
	 * Creates a closed shape with initial point 0,0
 	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 */
	public BezShape(PApplet parent) {
		this(parent, 0, 0, true);
	}

	/**
	 * Creates a closed shape with initial point x,y.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 * @param x		x-coordinate of initial point
	 * @param y		y-coordinate of initial point
	 */
	public BezShape(PApplet parent, float x, float y) {
		this(parent, x, y, true);
	}

	/**
	 * Creates a closed BezShape with initial point ax1, ay1, and appends a curve segment.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 */
	public BezShape(PApplet parent, float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2) {
		this(parent, ax1, ay1, true);
		this.append(new BezVertex(cx1, cy1, cx2, cy2, ax2, ay2));
	}

	/**
	 * Creates a BezShape with initial point ax1, ay1, and appends a curve segment.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
     * @param parent   PApplet used for calls to the Processing environment, notably for drawing
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 * @param isClosed   true if shape is closed, false if it is open
	 */
	public BezShape(PApplet parent, float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2, boolean isClosed) {
		this(parent, ax1, ay1, isClosed);
		this.append(new BezVertex(cx1, cy1, cx2, cy2, ax2, ay2));
	}

	
	/**
	 * Creates a closed shape with initial point 0,0, obtains parent PApplet from initialized 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
 	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
	 */
	public BezShape() {
		this(IgnoCodeLib.getMyParent(), 0, 0, true);
	}

	/**
	 * Creates a closed shape with initial point x,y, obtains parent PApplet from initialized 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
	 * @param x		x-coordinate of initial point
	 * @param y		y-coordinate of initial point
	 */
	public BezShape(float x, float y) {
		this(IgnoCodeLib.getMyParent(), x, y, true);
	}

	/**
	 * Creates a closed BezShape with initial point ax1, ay1, and appends a curve segment, 
	 * obtains parent PApplet from initialized {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 */
	public BezShape(float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2) {
		this(IgnoCodeLib.getMyParent(), ax1, ay1, true);
		this.append(new BezVertex(cx1, cy1, cx2, cy2, ax2, ay2));
	}

	/**
	 * Creates a BezShape with initial point ax1, ay1, and appends a curve segment, 
	 * obtains parent PApplet from initialized {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * The Processing transformation matrix (set by calls to rotate, translate and scale) is saved
	 * to the instance variable <code>ctm</code>, but no transform is performed.  Note that drawing
	 * is affected by the current Processing transform. 
	 * 
 	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 * @param isClosed   true if shape is closed, false if it is open
	 */
	public BezShape(float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2, boolean isClosed) {
		this(IgnoCodeLib.getMyParent(), ax1, ay1, isClosed);
		this.append(new BezVertex(cx1, cy1, cx2, cy2, ax2, ay2));
	}

	
	/**
	 * Creates a deep copy of this BezShape.
	 * @see java.lang.Object#clone
	 */
	public BezShape clone() {
		BezShape copyThis = new BezShape(parent, this.x, this.y);
		if (this.hasCenter()) copyThis.setCenter(this.xctr, this.yctr);
		copyThis.setIsClosed(this.isClosed());
		int c = this.fillColor();
		copyThis.setFillColor(c);
		copyThis.setHasFill(this.hasFill);
		c = this.strokeColor();
		copyThis.setStrokeColor(c);
		copyThis.setHasStroke(this.hasStroke);
		copyThis.setWeight(this.weight());
		copyThis.setBezType(this.bezType());
		ListIterator<Vertex2DINF> it = curveIterator();
		while (it.hasNext()) {
			Vertex2DINF bez = it.next();
			copyThis.append(bez.clone());
		}
		return copyThis;
	}

	
	/** 
	 * @throws UnsupportedOperationException, BezShape is a terminal (leaf) node
	 */
	@Override
	public void add(DisplayComponent component) {
		throw new UnsupportedOperationException("Attempt to add child to a terminal node."); 
	}
	/** 
	 * @throws UnsupportedOperationException, BezShape is a terminal (leaf) node
	 */
	public void add(ArrayList<? extends DisplayComponent> comps) {
		throw new UnsupportedOperationException("Attempt to add child to a terminal node."); 
	}
	/** 
	 * @throws UnsupportedOperationException, BezShape is a terminal (leaf) node
	 */
	public boolean remove(DisplayComponent component) {
		throw new UnsupportedOperationException("Attempt to remove child from a terminal node."); 
	}
	/** 
	 * @throws UnsupportedOperationException, BezShape is a terminal (leaf) node
	 */
	public DisplayComponent get(int index) {
		throw new UnsupportedOperationException("Attempt to access child of a terminal node.");
	}
	/** 
	 * @throws UnsupportedOperationException, BezShape is a terminal (leaf) node
	 */
	public Iterator<DisplayComponent> iterator() {
		throw new UnsupportedOperationException("Attempt to access children array of a terminal node.");
	}
	/**
	 * Returns empty list: this is a terminal node, with no children.
	 * @return null, BezShape is a terminal (leaf) node
	 */
	public List<DisplayComponent> children() {
		// TODO refactor to return Collections.emptyList() 
		// this is good usage, according to Effective Java, but must change interface 
		// to return List--also good usage, according to Effective Java
		// OTOH, it is actually an error to call this method...
		return Collections.emptyList();
		// return null;
	}
	/**
	 * Returns <code>true</code>: this is a terminal component.
	 * @return true, BezShape is a terminal (leaf) node
	 */
	public boolean isTerminal() {
		return true;
	}


	/**
	 * Appends a Vertex2DINF to this BezShape
	 * @param vt   a Vertex2DINF (line segment or curve segment)
	 */
	public void append(Vertex2DINF vt) {
		curves.add(vt);
	}

	/**
	 * Appends a BezVertex (cubic B�zier segment) to this BezShape.
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param x     x-coordinate of terminal anchor point
	 * @param y     y-coordinate of terminal anchor point
	 */
	public void append(float cx1, float cy1, float cx2, float cy2, float x, float y) {
		this.append(new BezVertex(cx1, cy1, cx2, cy2, x, y));
	}


	/**
	 * Appends a LineVertex (line segment) to this BezShape.
	 * @param x
	 * @param y
	 */
	public void append(float x, float y) {
		this.append(new LineVertex(x, y));
	}
	
	/**
   * Returns size of number of vertices (BezVertex and LineVertex) in curves.
   * @return size of curves ArrayList.
   */
  public int size() {
    return curves.size();
  }
  
  /**
   * Returns number of points (anchor points and control points) in curves.
   * Dosn't count the start point.
   * @return total numbr of points in curves ArrayList data.
   */
  public int pointCount() {
    int count = 0;
    ListIterator<Vertex2DINF> it = curveIterator();
    while (it.hasNext()) {
      Vertex2DINF bez = it.next();
      if (bez.segmentType() == CURVE_SEGMENT) {
        count += 3;
      }
      else if (bez.segmentType() == LINE_SEGMENT) {
        count += 1;
      }
      else {
        // error! should never arrive here
      }
    }
    return count;
  }
	
	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS FOR STROKE, FILL AND WEIGHT ATTRIBUTES                                            */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/
	

	/**
	 * @return  {@code true} if this shape is closed, {@code false} otherwise.
	 */
	public boolean isClosed() {
		return isClosed;
	}
	/**
	 * @param newIsClosed   {@code true} if this shape is closed, {@code false} otherwise
	 */
	public void setIsClosed(boolean newIsClosed) {
		isClosed = newIsClosed;
	}


	/**
	 * Sets fill and stroke using the current graphics state stored in this.parent
	 * Called internally. 
	 */
	protected void setColors() {
		if (this.parent.g.fill) {
			this.setFillColor(this.parent.g.fillColor);
		} 
		else {
			setNoFill();
		}
		if (this.parent.g.stroke) {
			this.setStrokeColor(this.parent.g.strokeColor);
			this.setWeight(this.parent.g.strokeWeight);
		}
		else {
			setNoStroke();
		}
	}
	/**
	 * Sets fill and stroke using the current graphics state.
	 */
	public void setColors(PApplet host) {
		if (host.g.fill) {
			this.setFillColor(host.g.fillColor);
		} 
		else {
			setNoFill();
		}
		if (host.g.stroke) {
			this.setStrokeColor(host.g.strokeColor);
			this.setWeight(host.g.strokeWeight);
		}
		else {
			setNoStroke();
		}
	}
	
	
	/**
	 * @return   true if this shape is filled, false otherwise
	 */
	public boolean hasFill() {
		return hasFill;
	}
	/**
	 * @param newHasFill   pass true if this shape has a fill, false otherwise. Note that
	 * the current fillColor will not be discarded by setting hasFill to false: the shape
	 * simply won't display or save to file with a fill. 
	 */
	public void setHasFill(boolean newHasFill) {
		hasFill = newHasFill;
	}
	/**
	 * Equivalent to setHasFill(false).
	 */
	public void setNoFill() {
		setHasFill(false);
	}


	/**
	 * @return   true if this shape is stroked, false otherwise
	 */
	public boolean hasStroke() {
		return hasStroke;
	}
	/**
	 * @param newHasStroke   pass true if this shape has a stroke, false otherwise. Note that
	 * the current strokeColor will not be discarded by setting hasStroke to false: the shape
	 * simply won't display or save to file with a stroke.
	 */
	public void setHasStroke(boolean newHasStroke) {
		hasStroke = newHasStroke;
	}
	/**
	 * Equivalent to setHasStroke(false).
	 */
	public void setNoStroke() {
		setHasStroke(false);
	}


	/**
	 * @return the current fill color
	 */
	public int fillColor() {
		return fillColor;
	}
	/**
	 * @param newFillColor   a Processing color (32-bit int with ARGB bytes).
	 */
	public void setFillColor(int newFillColor) {
		fillColor = newFillColor;
		setHasFill(true);
	}


	/**
	 * @return the current stroke color
	 */
	public int strokeColor() {
		return this.strokeColor;
	}
	/**
	 * @param newStrokeColor   a Processing color (32-bit int with ARGB bytes).
	 */
	public void setStrokeColor(int newStrokeColor) {
		this.strokeColor = newStrokeColor;
		this.setHasStroke(true);
	}


	/**
	 * @return true if transparency is enabled, false otherwise.
	 */
	public static boolean useTransparency() {
		return AIFileWriter.useTransparency;
	}
	/**
	 * Pass a value of true to enable transparency when exporting to Adobe Illustrator, default is false. 
	 * Transparency markup is not supported in the AI7.0 specification, but it seems to work.
	 * Note that in Illustrator transparency affects the entire shape, both fill and stroke,
	 * unlike Processing, where fill and stroke can have separate transparency values. This
	 * means for stroked shapes, the stroke transparency will affect the whole shape in AI.
	 * See {@link #write(PrintWriter)}.
	 * @param useTransparency
	 * @deprecated  preferred usage is {@code AIFileWriter.setUseTransparency()}.
	 */
	public static void setUseTransparency(boolean useTransparency) {
		AIFileWriter.useTransparency = useTransparency;
	}
	
	// TODO setStrokeOpacity and setFillOpacity methods (DONE)
	/**
	 * Sets opacity of current fill color.
	 * @param opacity   a number in the range 0..255. Value is not checked!
	 */
	public void setFillOpacity(int opacity) {
		int[] argb = Palette.argbComponents(this.fillColor);
		this.setFillColor(Palette.composeColor(argb[1], argb[2], argb[3], opacity));
	}
	/**
	 * @return   the opacity value of the current fill color
	 */
	public int fillOpacity() {
		int[] argb = Palette.argbComponents(this.fillColor);
		return argb[0];
	}
	
	/**
	 * Sets opacity of current stroke color.
	 * @param opacity   a number in the range 0..255. Value is not checked!
	 */
	public void setStrokeOpacity(int opacity) {
		int[] argb = Palette.argbComponents(this.strokeColor);
		this.setStrokeColor(Palette.composeColor(argb[1], argb[2], argb[3], opacity));
	}
	/**
	 * @return   the opacity value of the current stroke color
	 */
	public int strokeOpacity() {
		int[] argb = Palette.argbComponents(this.strokeColor);
		return argb[0];
	}

	
	/**
	 * Returns the current weight (in points) of stroked lines.
	 * @return the current weight (in points) of stroked lines. One point = one pixel on screen.
	 */
	public float weight() {
		return weight;
	}
	/**
	 * @param newWeight the new weight of stroked lines.
	 */
	public void setWeight(float newWeight) {
		weight = newWeight;
	}


	/** 
	 * @return type of shape (BEZ_PATH, BEZ_TRIANG, BEZ_CIRCLE, BEZ_ELLIPSE, BEZ_CURVE_POLY, BEZ_REGULAR_POLY)
	 */
	public BezType bezType() {
		return bezType;
	}
	/** 
	 * @param newBezType  type of shape (BEZ_PATH, BEZ_TRIANG, BEZ_CIRCLE, BEZ_ELLIPSE, BEZ_CURVE_POLY, BEZ_REGULAR_POLY)
	 */
	public void setBezType(BezType newBezType) {
		bezType = newBezType;
	}

	/**
	 * @return  {@code true} if this shape is marked with vertices and control points, {@code false} otherwise.
     */
	public boolean isMarked() {
	  return isMarked;
	}
    /**
     * @param newIsMarked   {@code true} if this shape is marked with vertices and control points, {@code false} otherwise
     */
    public void setIsMarked(boolean newIsMarked) {
      isMarked = newIsMarked;
    }

	
	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS FOR START COORDINATES AND CENTER COORDINATES                                      */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/
	

	/**
	 * Returns the x-coordinate of the initial point of the geometry of this shape.
	 * @return   x-coordinate of initial vertex
	 */
	public float x() {
		return x;
	}
	/**
	 * Sets the x-coordinate of the initial point of the geometry of this shape.
	 * @param newX   new x-coordinate of initial vertex
	 */
	public void setX(float newX) {
		x = newX;
	}


	/**
	 * Returns the y-coordinate of the initial point of the geometry of this shape.
	 * @return   y-coordinate of initial vertex
	 */
	public float y() {
		return y;
	}
	/**
	 * Sets the y-coordinate of the initial point of the geometry of this shape.
	 * @param newY   new y-coordinate of initial vertex
	 */
	public void setY(float newY) {
		y = newY;
	}
	
	/**
	 * @return   a LineVertex with start point coordinates of this shape 
	 */
	public LineVertex startVertex() {
		return new LineVertex(this.x, this.y);
	}

	/**
	 * @param startPoint the startPoint to set
	 */
	public void setStartPoint(LineVertex startPoint) {
		this.x = startPoint.x;
		this.y = startPoint.y;
	}
	/**
	 * Sets a new initial vertex for this BezShape.
	 * @param pt   a Point2D
	 */
	public void setStartPoint(Point2D pt) {
		this.setStartPoint((float) pt.getX(), (float) pt.getY());
	}
	/**
	 * Sets a new initial vertex for this BezShape.
	 * @param newX
	 * @param newY
	 */
	public void setStartPoint(float newX, float newY) {
		this.setX(newX); 
		this.setY(newY); 
	}


	/**
	 * return initial vertex as an array of two floats
	 * @return x and y coordinates of initial vertex in an array
	 * @since Oct. 20, 2011 renamed from startVertex to startVertexArray
	 */
	public float[] startVertexArray() {
		float[] pt = new float[2];
		pt[0] = this.x;
		pt[1] = this.y;
		return pt;
	}


	/**
	 * Returns the value of the x-coordinate of center of transformation. Will calculate center
	 * of bounding box if center has not been set.
	 * @return   x-coordinate of center of transformation. 
	 */
	public float xctr() {
		if (!hasCenter) {
			calculateCenter();
		}
		return xctr;
	}

	/**
	 * Returns the of the y-coordinate of center of transformation. Will calculate center
	 * of bounding box if center has not been set.
	 * @return   y-coordinate of center of transformation.
	 */
	public float yctr() {
		if (!hasCenter) {
			calculateCenter();
		}
		return yctr;
	}

	/**
	 * @return   the center coordinates xctr, yctr as a LineVertex
	 */
	public LineVertex centerVertex() {
		return new LineVertex(this.xctr, this.yctr);
	}

	/**
	 * Sets center point for geometric transforms on this shape.
	 * @param xctr
	 * @param yctr
	 */
	public void setCenter(float xctr, float yctr) {
		this.xctr = xctr;
		this.yctr = yctr;
		setHasCenter(true);
	}
	/**
	 * Sets center point for geometric transforms on this shape.
	 * @param pt
	 */
	public void setCenter(Point2D pt) {
		this.setCenter((float) pt.getX(), (float) pt.getY());
	}
	/**
	 * Sets the values of {@code xctr} and {@code yctr}
	 * @param centerPoint   a LineVertext encapsulating the center coordinates to set
	 */
	public void setCenter(LineVertex centerPoint) {
		setCenter(centerPoint.x, centerPoint.y);
	}


	/**
	 * Returns true if a center of transformation has been set for this shape, false otherwise.
	 * @return   true if a center of transformation has been set for this shape, false otherwise
	 */
	private boolean hasCenter() {
		return hasCenter;
	}
	/**
	 * Sets value of hasCenter.
	 * Called by setCenter(), and by xctr() and yctr() if necessary. Generally no need for user to call.
	 * Calculation of center point will be forced by subsequent calls to xctr() and yctr() and the 
	 * rotate, translate, and scale methods if hasCenter is set to false. 
	 * @param newHasCenter
	 */
	private void setHasCenter(boolean newHasCenter) {
		hasCenter = newHasCenter;
	}

	/**
	 * Sets the center of transformation to the center of the bounding rectangle of this shape.
	 * Sets the {@code xcoords} and {@code ycoords} arrays, if required.
	 * By default, the center point is (0,0), as in Processing. If you want shapes to transform around their 
	 * center points (i.e., to scale or rotate in place), call calculateCenter before calling
	 * {@link #rotateShape(float) rotateShape} or {@link #scaleShape(float, float) scaleShape}. 
	 * {@link #transform(Matrix3) transform} uses the point of transformation set in its
	 * transformation matrix, so you can use it for transformations around any arbitrary point.
	 */
	public void calculateCenter() {
		float[] bounds = this.bounds();
		float left = bounds[0];
		float top = bounds[1];
		float right = bounds[2];
		float bottom = bounds[3];
		setCenter((right + left)/2.0f, (top + bottom)/2.0f);
	}

	/**
	 * Returns the center of the bounding rectangle of this shape as a LineVertex.
	 * Sets the {@code xcoords} and {@code ycoords} arrays, if required.
	 * @return   an array of two floats, the average x- and y-coordinates of the shape's anchor points.
	 * @since October 21, 2011
	 */
	public LineVertex getBoundsCenter() {
		float[] bounds = this.bounds();
		float left = bounds[0];
		float top = bounds[1];
		float right = bounds[2];
		float bottom = bounds[3];
		return new LineVertex((right + left)/2.0f, (top + bottom)/2.0f);
	}
	
	/**
	 * Returns the average of all anchor points of the vertices of this shape.
	 * @return    the average x- and y-coordinates of the shape's anchor points.
	 * @since October 21, 2011
	 */
	public LineVertex getAnchorCenter(BezShape shape) {
		// skip the start vertex at (x,y), it will show up in values from curveIterator
		float xsum = 0;
		float ysum = 0;
		float count = 0;
		ListIterator<Vertex2DINF> it = this.curveIterator();
		Vertex2DINF vertex = null;
		while (it.hasNext()) {
			vertex = it.next();
			xsum += vertex.x();
			ysum += vertex.y();
			count++;
		}
		// include the start point if it isn't already in the curves
		if (count > 0) {
			if (vertex.x() != this.x || vertex.y() != this.y) {
				xsum += this.x;
				ysum += this.y;
				count++;
			}
		}
		return new LineVertex(xsum/count, ysum/count);
	}
	
	/**
	 * Returns the average of the x- and y-coordinates of this shape as a {@code LineVertex}.
	 * The result is an approximation based on the derived polygon. Accuracy can be improved by first 
	 * calling asPolygon with an argument greater than the default of 16 segments per curve.
	 * @return   a LineVertex at the geometric mean of the points of this shape
	 */
	public LineVertex getGeoCenter() {
		float[] xc = this.xcoords(parent);
		float[] yc = this.ycoords(parent);
		float xsum = 0;
		float ysum = 0;
		float count = 0;
		for (float x : xc) {
			xsum += x;
			count++;
		}
		for (float y : yc) {
			ysum =+ y;
		}
		return new LineVertex(xsum/count, ysum/count);
	}
	

	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS FOR LINE AND CURVE VERTICES AND GEOMETRY                                          */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/
	
	
	/**
	 * Returns an iterator over the geometry of this shape. Preferred method for accessing geometry.
	 * @return an iterator over the Vertex2DINF segments that comprise the geometry of this shape
	 */
	public ListIterator <Vertex2DINF> curveIterator() {
		return curves.listIterator();
	}
	
	
	/**
	 * Returns the geometry of this shape as an {@code ArrayList}. Note that the startPoint is not included, 
	 * you can obtain that separately from {@link #startVertex()} or from {@link #x()} and {@link #y()}. 
	 * @return an ArrayList with all the Vertex2DINF segments that compose this shape, in order appended
	 * Use with caution, changing geometry directly instead of with {@link #transform()} and other
	 * built-in methods can have unexpected consequences.
	 * Call curvesCopy instead. 
	 */
	public ArrayList<Vertex2DINF> curves() {
		return curves;
	}
	/**
	 * Returns a copy of the geometry of this shape as an {@code ArrayList}.  Note that the startPoint is not included, 
	 * you can obtain that separately from {@link #startVertex()} or from {@link #x()} and {@link #y()}.
	 * @return an ArrayList with all the Vertex2DINF segments that compose this shape, in order appended
	 * @fixed build array with Vertex2DINF.clone(), June 14, 2012
	 * @since  October 3, 2011
	 */
	public ArrayList<Vertex2DINF> curvesCopy() {
		ArrayList<Vertex2DINF> curvesCopy = new ArrayList<Vertex2DINF>(curves.size());
		for (Vertex2DINF vt : curves) {			
			curvesCopy.add(vt.clone());
		}
		return curvesCopy;
	}
	/**
	 * @param newCurves   an ArrayList of Vertex2DINF segments that will define the geometry of this shape. 
	 * Does not change the startPoint. You can do that with {@link #setStartPoint(float, float)} or with 
	 * {@link #setX(float)} and {@link #setY(float)}. 
	 * Sets xcoords and ycoords to null.
	 * Does not update {@code centerPoint}. You can do that with {@link #calculateCenter()} or the various
	 * methods for setting {@code centerPoint} directly, {@link #setCenter(LineVertex)}, {@link #setCenter(float, float)}.
	 * Use with caution. 
	 */
	public void setCurves(ArrayList<Vertex2DINF> newCurves) {
		curves = newCurves;
	}
	
	
	/**
	 * Returns an array of all vertex coordinates. A {@code LineVertex} adds two values to the array
	 * and a {@code BezVertex} adds six values (two control points and an anchor point). There is no
	 * inherent way to distinguish which values represent control points and which represent anchor points. 
	 * @see #asPolygon(PApplet, int).
	 * @return   an array of {@code float} generated from the vertices of this shape.
	 */
	public float[] getCoords() {
		ListIterator<Vertex2DINF> it = curveIterator();
		// start counting points at 1, for start point
		int ct = 1;
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			// BezVertex stores three points = two control points and an anchor point
			if (CURVE_SEGMENT == segType) {
				ct += 3;
			}
			// LineVertex stores one point
			else if (LINE_SEGMENT == segType) {
				ct += 1;
			}
		}
		// each point is comprised of 2 floats
		float[] points = new float[ct * 2];
		int i = 0;
		points[i++] = this.x;
		points[i++] = this.y;
		it = curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			if (CURVE_SEGMENT == segType) {
				float[] knots = vt.coords();
				points[i++] = knots[0];		// cx1
				points[i++] = knots[1];		// cy1
				points[i++] = knots[2];		// cx2
				points[i++] = knots[3];		// cy2
				points[i++]  = knots[4];	// ax
				points[i++]  = knots[5];	// ay
			}
			else if (LINE_SEGMENT == segType) {
				points[i++] = vt.x();
				points[i++] = vt.y();
			}
		}
		return points;
	}


	/**
	 * Extracts an approximated polygon from path data, returning it as an array of floats.
	 * Rebuilds the {@code xcoords} and {@code ycoords} arrays. Polygon data is not cached, but the
	 * {@code xcoords} and {@code ycoords} arrays are. You can use them to construct a polygon once 
	 * they have been initialized. If, against our good advice, you munge around with
	 * shape geometry, you can reset {@code xcoords} and {@code ycoords} with a call to 
	 * this method, which always recalculates {@code xcoords} and {@code ycoords} and {@code boundsRect}
	 * @param steps    number of straight line segments to divide Bezier curves into
	 * @param parent   reference to a PApplet needed to build a polygon from a Bezier curve
	 */
	public float[] asPolygon(PApplet parent, int steps) {
		ListIterator<Vertex2DINF> it = curveIterator();
		// calculate number of points in the result array
		// start counting points at 1, since start point will begin the array
		int ct = 1;
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			if (CURVE_SEGMENT == segType) {
				// divide the curve into steps lines
				ct += steps;
			}
			else if (LINE_SEGMENT == segType) {
				ct += 1;
			}
		}
		// each point is comprised of 2 floats
		float[] points = new float[ct * 2];
		int i = 0;
		points[i++] = this.x;
		points[i++] = this.y;
		float currentX = points[i - 2];
		float currentY = points[i - 1];
		it = curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			if (CURVE_SEGMENT == segType) {
				float[] knots = vt.coords();
				float cx1 = knots[0];
				float cy1 = knots[1];
				float cx2 = knots[2];
				float cy2 = knots[3];
				float ax  = knots[4];
				float ay  = knots[5];
				for (int j = 1; j <= steps; j++) {
					float t = j / (float) steps;
					float segx = parent.bezierPoint(currentX, cx1, cx2, ax, t);
					float segy = parent.bezierPoint(currentY, cy1, cy2, ay, t);
					points[i++] = segx;
					points[i++] = segy;
				}
			}
			else if (LINE_SEGMENT == segType) {
				points[i++] = vt.x();
				points[i++] = vt.y();
			}
			currentX = points[i - 2];
			currentY = points[i - 1];
		}
		// recalculate xcoords and ycoords
		this.xcoords = GeomUtils.xCoords(points);
		this.ycoords = GeomUtils.yCoords(points);
		// force recalculation of boundsRect from xcoords and ycoords next time its method is called
		this.boundsRect = null;
		return points;
	}
	/**
	 * Extracts an approximated polygon from path data. Returns the polygon as an array of floats
	 * using a default value of 16 steps for curve segments. Call {@link #setPolySteps(int) setPolySteps()} 
	 * to change the level of approximation of the polygon to the actual paths. 
	 * @param parent   reference to a PApplet needed to build a polygon from a Bezier curve
	 */
	public float[] asPolygon(PApplet parent) {
		return asPolygon(parent, this.polySteps);
	}
	public float[] asPolygon(int steps) {
		return this.asPolygon(this.parent, steps);
	}
	public float[] asPolygon() {
		return asPolygon(this.parent, this.polySteps);
	}
	
	/**
	 * @TODO this is new
	 * @param steps number of line segments in a curve
	 * @return
	 */
	public BezShape asPolygonList(int steps) {
		BezShape bez;
		if (this.isClosed()) {
			bez = BezPoly.makePoly(this.asPolygon(steps));
		}
		else {
			bez = BezMultiLine.makeMultiLine(this.asPolygon(steps));
		}
		return bez;
	}

	/**
	 * Tests if a point is inside this shape by generating a polygon approximation to curved paths and 
	 * testing that. It is possible to get an erroneous answer. Precision may be increased by calling
	 * setPolySteps with a value above the default 16 steps. May be slow the first time you call it, 
	 * but will used cached data after that.
	 * @param parent   reference to a Processing PApplet, probably the one calling this code
	 * @param x		   x-coordinate of test point
	 * @param y        y-coordinate of test point
	 * @return         true if point is inside the polygon approximation of this shape
	 */
	public boolean containsPoint(PApplet parent, float x, float y) {
		return GeomUtils.pointInPoly(this.polySize(parent), this.xcoords(parent), this.ycoords(parent), x, y);
	}
	public boolean containsPoint(float x, float y) {
		return this.containsPoint(this.parent, x, y);
	}

	/**
	 * Returns x-coordinates of the geometry of this shape. The shape is first rendered as 
	 * a polygon (see {@link #asPolygon(PApplet) asPolygon}) for details. 
	 * @param parent   reference to a PApplet needed to build a polygon from a Bezier curve
	 * @return   x-coordinate array of the geometry of this shape 
	 */
	public float[] xcoords(PApplet parent) {
		if (null == this.xcoords) {
			asPolygon(parent);
		}
		return this.xcoords;
	}
	public float[] xcoords() {
		return this.xcoords(this.parent);
	}
	
	/** 
	 * Returns y-coordinates of the geometry of this shape. The shape is first rendered as 
	 * a polygon (see {@link #asPolygon(PApplet) asPolygon}) for details. 
	 * @param parent   reference to a PApplet needed to build a polygon from a Bezier curve
	 * @return   y-coordinate array 
	 */
	public float[] ycoords(PApplet parent) {
		if (null == this.ycoords) {
			asPolygon(parent);
		}
		return this.ycoords;
	}
	public float[] ycoords() {
		return this.ycoords(this.parent);
	}
	
	/**
	 * Sets {@code boundsRect}, {@code xcoords} and {@code ycoords} to null. Called internally after transforms.
	 * If you munge around with the geometry, you can reset {@code xcoords} and {@code ycoords} with a call to 
	 * {@link #asPolygon() asPolygon}, which always recalculates {@code xcoords} and {@code ycoords}.
	 */
	private void nullCoords() {
		this.xcoords = null;
		this.ycoords = null;
		this.boundsRect = null;
	}

	/** 
	 * @param parent   reference to a PApplet needed to build a polygon from a Bezier curve
	 * @return   number of points in polygon representation of this shape 
	 */
	public int polySize(PApplet parent) {
		return xcoords(parent).length;
	}
	public int polySize() {
		return this.polySize(this.parent);
	}

	/** 
	 * Returns the number of straight line segments used in fast calculation
	 * @return  default number of steps in polyline representation of a curve segment
	 */
	public int polySteps() {
		return polySteps;
	}
	/** 
	 * @param newPolySteps   default number of steps in polyline representation of a curve segment 
	 */
	public void setPolySteps(int newPolySteps) {
		this.polySteps = newPolySteps;
	}
	
	
	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS FOR BOUNDING BOX GEOMETRY                                                         */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/

	
	/** 
	 * returns array of floats with top, left, bottom, right coordinates of bounding box
	 * These are an approximation based on the derived polygon, accuracy can be improved by first 
	 * calling asPolygon with an argument greater than the default of 16 segments per curve.
	 * @param parent   reference to a PApplet needed to build a polygon from a Bezier curve
	 * @return   array of float: left, top, right, bottom coordinates
	 */
	public float[] bounds(PApplet parent) {
		float[] xc = this.xcoords(parent);
		float[] yc = this.ycoords(parent);
		float xMin = xc[0];
		float yMin = yc[0];
		float xMax = xMin;
		float yMax = yMin;
		for(int i = 1; i < xcoords.length; i++) {
			float x = xc[i];
			float y = yc[i];
			if (x < xMin) xMin = x;
			if (y < yMin) yMin = y;
			if (x > xMax) xMax = x;
			if (y > yMax) yMax = y;
		}
		float[] result = new float[4];
		result[0] = xMin;
		result[1] = yMin;
		result[2] = xMax;
		result[3] = yMax;
		return result;
	}
	/** 
	 * returns array of floats with top, left, bottom, right coordinates of bounding box
	 * These are an approximation based on the derived polygon, accuracy can be improved by first 
	 * calling asPolygon with an argument greater than the default of 16 segments per curve.
	 * For speed, it is better to call the {@code boundsRect()} method, which returns cached data.
	 * @return   array of float: left, top, right, bottom coordinates
	 */
	public float[] bounds() {
		return this.bounds(this.parent);
	}
	
	/**
	 * Returns the bounding rectangle of this shape. If it has not yet been calculated,
	 * creates it from the calculated xcoords and ycoords arrays. The {@code boundsRect} is
	 * aligned to the x- and y-axes. It is cached in between calls, and set to null after a transform. 
	 *
	 * @return   the bounding rectangle of this shape
	 */
	public BezRectangle boundsRect() {
		if (null == this.boundsRect) {
			float[] bounds = this.bounds(this.parent);
			boundsRect = BezRectangle.makeLeftTopRightBottom(this.parent, bounds[0], bounds[1], bounds[2], bounds[3]);
		}		
		return boundsRect;
	}


	/*-------------------------------------------------------------------------------------------------------*/
	/*                                                                                                       */
	/* GEOMETRIC TRANSFORMS                                                                                  */ 
	/*                                                                                                       */
	/*-------------------------------------------------------------------------------------------------------*/

	
	/**
	 * Translates this shape. Moves center point, calculating it if necessary.
	 * Sets xcoords and ycoords arrays to null: they will have to be recalculated after a transform,
	 * which will be done through lazy initialization when {@code xcoords()} or {@code ycoords()} are called.
	 * @param xTrans   translation on x-axis
	 * @param yTrans   translation on y-axis
	 * @since 2011.10.07 faster code, uses instance vars directly
	 */
	public void translateShape(float xTrans, float yTrans) {
		this.setCenter(this.xctr + xTrans, this.yctr + yTrans);
		this.setStartPoint(this.x + xTrans, this.y + yTrans);
		ListIterator<Vertex2DINF> it = this.curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			if (CURVE_SEGMENT == segType) {
				BezVertex bv = (BezVertex) vt;
				float[] coords = bv.coords();
				coords[0] += xTrans;
				coords[1] += yTrans;
				coords[2] += xTrans;
				coords[3] += yTrans;
				coords[4] += xTrans;
				coords[5] += yTrans;
				bv.setCx1(coords[0]);
				bv.setCy1(coords[1]);
				bv.setCx2(coords[2]);
				bv.setCy2(coords[3]);
				bv.setX(coords[4]);
				bv.setY(coords[5]);
			}
			else if (LINE_SEGMENT == segType) {
				LineVertex lv = (LineVertex) vt;
				lv.setX(lv.x + xTrans);
				lv.setY(lv.y + yTrans);
			}
		}
		this.nullCoords();
	}
	
	//LEGACY VERSION
/*	public void translateShape(float xTrans, float yTrans) {
		Point2D.Float pt = GeomUtils.translateCoor(this.xctr(), this.yctr(), xTrans, yTrans);
		this.setCenter(pt);
		pt = GeomUtils.translateCoor(this.x, this.y, xTrans, yTrans);
		this.setStartPoint(pt);
		ListIterator<Vertex2DINF> it = this.curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			float[] coords = vt.coords();
			if (CURVE_SEGMENT == segType) {
				BezVertex bv = (BezVertex) vt;
				pt = GeomUtils.translateCoor(coords[0], coords[1], xTrans, yTrans);
				bv.setCx1(pt.x);
				bv.setCy1(pt.y);
				pt = GeomUtils.translateCoor(coords[2], coords[3], xTrans, yTrans);
				bv.setCx2(pt.x);
				bv.setCy2(pt.y);
				pt = GeomUtils.translateCoor(coords[4], coords[5], xTrans, yTrans);
				bv.setX(pt.x);
				bv.setY(pt.y);
			}
			else if (LINE_SEGMENT == segType) {
				LineVertex lv = (LineVertex) vt;
				pt = GeomUtils.translateCoor(coords[0], coords[1], xTrans, yTrans);
				lv.setX(pt.x);
				lv.setY(pt.y);
			}
		}
		this.nullCoords();
	}
*/	

	
	/**
	 * Handy method to translate center point (xctr, yctr) and shape to a new location.
	 * @param newXCtr
	 * @param newYCtr
	 */
	public void moveTo(float newXCtr, float newYCtr) {
		translateShape(newXCtr - this.xctr(), newYCtr - this.yctr());
	}

	
	/**
	 * Scales this shape around a given point. 
	 * Sets xcoords and ycoords arrays to null: they will have to be recalculated after a transform,
	 * which will be done through lazy initialization when {@code xcoords()} or {@code ycoords()} are called.
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 */
	public void scaleShape(float xScale, float yScale, float x0, float y0) {
		this.x = (x0 + (this.x - x0) * xScale);
		this.y = (y0 + (this.y - y0) * yScale);
		this.xctr = (x0 + (this.xctr - x0) * xScale);
		this.yctr = (y0 + (this.yctr - y0) * yScale);
		ListIterator<Vertex2DINF> it = this.curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			float[] coords = vt.coords();
			Point2D.Float pt;
			if (CURVE_SEGMENT == segType) {
				BezVertex bv = (BezVertex) vt;
				pt = GeomUtils.scaleCoorAroundPoint(coords[0], coords[1], xScale, yScale, x0, y0);
				bv.setCx1(pt.x);
				bv.setCy1(pt.y);
				pt = GeomUtils.scaleCoorAroundPoint(coords[2], coords[3], xScale, yScale, x0, y0);
				bv.setCx2(pt.x);
				bv.setCy2(pt.y);
				pt = GeomUtils.scaleCoorAroundPoint(coords[4], coords[5], xScale, yScale, x0, y0);
				bv.setX(pt.x);
				bv.setY(pt.y);
			}
			else if (LINE_SEGMENT == segType) {
				LineVertex lv = (LineVertex) vt;
				pt = GeomUtils.scaleCoorAroundPoint(coords[0], coords[1], xScale, yScale, x0, y0);
				lv.setX(pt.x);
				lv.setY(pt.y);
			}
		}
		this.nullCoords();
	}
	/**
	 * Scales this shape around its center point. You can change the center point by
	 * calling {@link #setCenter(float, float) setCenter} or {@link #calculateCenter() calculateCenter}.
	 * Sets xcoords and ycoords arrays to null: they will have to be recalculated after a transform,
	 * which will be done through lazy initialization when {@code xcoords()} or {@code ycoords()} are called.
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 */
	public void scaleShape(float xScale, float yScale) {
		this.scaleShape(xScale, yScale, this.xctr, this.yctr);
	}
	/**
	 * Uniformly scales this shape around its center point. You can change the center point by
	 * calling {@link #setCenter(float, float) setCenter} or {@link #calculateCenter() calculateCenter}.
	 * Sets xcoords and ycoords arrays to null: they will have to be recalculated after a transform,
	 * which will be done through lazy initialization when {@code xcoords()} or {@code ycoords()} are called.
	 * @param xyScale   uniform scaling on x-axis and y-axis
	 */
	public void scaleShape(float xyScale) { 
		this.scaleShape(xyScale, xyScale, this.xctr, this.yctr);
	}
	/**
	 * Uniformly scales this shape around a given point.
	 * @param xyScale   uniform scaling on x-axis and y-axis
	 */
	public void scaleShape(float xyScale, float x0, float y0) {
		this.scaleShape(xyScale, xyScale, x0, y0);
	}
	

	/**
	 * Rotates this shape around its center point. You can change the center point by
	 * calling {@link #setCenter(float, float) setCenter} or {@link #calculateCenter() calculateCenter}.
	 * Sets xcoords and ycoords arrays to null: they will have to be recalculated after a transform,
	 * which will be done through lazy initialization when {@code xcoords()} or {@code ycoords()} are called.
	 * @param theta    degrees to rotate (in radians)
	 * TODO for theta very near PI, 0, or TWO_PI, insure correct rotation.  
	 */
	public void rotateShape(float theta) {
		float x = this.xctr();
		float y = this.yctr();
		Point2D.Float pt = GeomUtils.rotateCoorAroundPoint(this.x(), this.y(), x, y, theta);
		this.setX(pt.x);
		this.setY(pt.y);
		ListIterator<Vertex2DINF> it = this.curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			float[] coords = vt.coords();
			if (CURVE_SEGMENT == segType) {
				BezVertex bv = (BezVertex) vt;
				pt = GeomUtils.rotateCoorAroundPoint(coords[0], coords[1], x, y, theta);
				bv.setCx1(pt.x);
				bv.setCy1(pt.y);
				pt = GeomUtils.rotateCoorAroundPoint(coords[2], coords[3], x, y, theta);
				bv.setCx2(pt.x);
				bv.setCy2(pt.y);
				pt = GeomUtils.rotateCoorAroundPoint(coords[4], coords[5], x, y, theta);
				bv.setX(pt.x);
				bv.setY(pt.y);
			}
			else if (LINE_SEGMENT == segType) {
				LineVertex lv = (LineVertex) vt;
				pt = GeomUtils.rotateCoorAroundPoint(coords[0], coords[1], x, y, theta);
				lv.setX(pt.x);
				lv.setY(pt.y);
			}
		}
		this.nullCoords();
	}
	

	/**
	 * Rotates this shape around a supplied center point.
	 * @param theta    degrees to rotate (in radians)
	 * TODO for theta very near PI, 0, or TWO_PI, insure correct rotation.  
	 */
	public void rotateShape(float xctr, float yctr, float theta) {
		Point2D.Float pt = GeomUtils.rotateCoorAroundPoint(this.x(), this.y(), xctr, yctr, theta);
		this.setX(pt.x);
		this.setY(pt.y);
		ListIterator<Vertex2DINF> it = this.curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			int segType = vt.segmentType();
			float[] coords = vt.coords();
			if (CURVE_SEGMENT == segType) {
				BezVertex bv = (BezVertex) vt;
				pt = GeomUtils.rotateCoorAroundPoint(coords[0], coords[1], xctr, yctr, theta);
				bv.setCx1(pt.x);
				bv.setCy1(pt.y);
				pt = GeomUtils.rotateCoorAroundPoint(coords[2], coords[3], xctr, yctr, theta);
				bv.setCx2(pt.x);
				bv.setCy2(pt.y);
				pt = GeomUtils.rotateCoorAroundPoint(coords[4], coords[5], xctr, yctr, theta);
				bv.setX(pt.x);
				bv.setY(pt.y);
			}
			else if (LINE_SEGMENT == segType) {
				LineVertex lv = (LineVertex) vt;
				pt = GeomUtils.rotateCoorAroundPoint(coords[0], coords[1], xctr, yctr, theta);
				lv.setX(pt.x);
				lv.setY(pt.y);
			}
		}
		this.nullCoords();
	}

	
	
	/**
	 * Captures a transformation matrix from a PApplet, for later use. 
	 * @param applet   a PApplet, probably the one that's running the show.
	 * @return   a Matrix3 that encapsulates Processing's current transformation matrix
	 */
	public static Matrix3 getMatrix(PApplet applet) {
		processing.core.PMatrix2D m = applet.getMatrix(new PMatrix2D());
		Matrix3 matx = new Matrix3();
		matx.setCTM(m.m00, m.m01, m.m10, m.m11, m.m02, m.m12);
		return matx;
	}
	
	/**
	 * Returns a copy of the most recent transformation matrix.
	 * Returns the identity matrix if no transforms have been performed.
	 * The ctm keeps a record of the most recent transformation matrix 
	 * passed as an argument to {@link #transform(Matrix3) transform}.
	 * It can be used directly (and repeatedly) with a call to {@link #transform() transform}.
	 * @return a copy of the ctm
	 */
	public Matrix3 getCtm() {
		if (null == this.ctm) this.ctm = new Matrix3();
		return Matrix3.copyMatrix3(this.ctm, new Matrix3());
	}

	/**
	 * @param ctm the ctm to set
	 */
	public void setCtm(Matrix3 ctm) {
		this.ctm = ctm;
	}
	/**
	 * Captures the current transformation matrix from a PApplet and uses it to set our ctm.
	 * @param applet    a PApplet from which the current matrix will be used to set the ctm
	 */
	public void setCtm(PApplet applet) {
		processing.core.PMatrix2D m = applet.getMatrix(new PMatrix2D());
		this.getCtm().setCTM(m.m00, m.m01, m.m10, m.m11, m.m02, m.m12);
	}
	/**
	 * Sets our ctm from a PMatrix2D.
	 * @param m a PMatrix2D to set the ctm
	 */
	public void setCtm(PMatrix2D m) {
		this.getCtm().setCTM(m.m00, m.m01, m.m10, m.m11, m.m02, m.m12);
	}

	/**
	 * Alternate method of calling {@link #transform(Matrix3) transform}.
	 * @deprecated
	 * @param matx
	 */
	public void transformShape(Matrix3 matx) {
		transform(matx);
	}
	
	
	/**
	 * Performs an affine geometric transformation on this shape using 3x3 matrix multiplication.
	 * Sets xcoords and ycoords arrays to null: they will have to be recalculated after a transform,
	 * which will be done through lazy initialization when {@code xcoords()} or {@code ycoords()} are called.
	 * @param matx   a 3x3 matrix see Matrix3 class for methods of loading transforms
	 */
	public void transform(Matrix3 matx) {
		this.ctm = new Matrix3(matx);
		Point2D.Double pt = new Point2D.Double();
		ListIterator<Vertex2DINF> it = this.curveIterator();
		if (Matrix3.isNormalMatrix3(matx)) {
			pt = matx.multiplyPointByNormalCTM(this.xctr(), this.yctr(), pt);
			this.setCenter(pt);
			pt = matx.multiplyPointByNormalCTM(this.x(), this.y(), pt);
			this.setStartPoint(pt);
			while (it.hasNext()) {
				Vertex2DINF vt = it.next();
				int segType = vt.segmentType();
				float[] coords = vt.coords();
				if (CURVE_SEGMENT == segType) {
					BezVertex bv = (BezVertex) vt;
					pt = matx.multiplyPointByNormalCTM(coords[0], coords[1], pt);
					bv.setCx1((float) pt.getX());
					bv.setCy1((float) pt.getY());
					pt = matx.multiplyPointByNormalCTM(coords[2], coords[3], pt);
					bv.setCx2((float) pt.getX());
					bv.setCy2((float) pt.getY());
					pt = matx.multiplyPointByNormalCTM(coords[4], coords[5], pt);
					bv.setX((float) pt.getX());
					bv.setY((float) pt.getY());
				}
				else if (LINE_SEGMENT == segType) {
					LineVertex lv = (LineVertex) vt;
					pt = matx.multiplyPointByNormalCTM(coords[0], coords[1], pt);
					lv.setX((float) pt.getX());
					lv.setY((float) pt.getY());
				}
			}
		}
		else {
			pt = matx.multiplyPointByProjCTM(this.xctr(), this.yctr(), pt);
			this.setCenter(pt);
			pt = matx.multiplyPointByProjCTM(this.x(), this.y(), pt);
			this.setStartPoint(pt);
			while (it.hasNext()) {
				Vertex2DINF vt = it.next();
				int segType = vt.segmentType();
				float[] coords = vt.coords();
				if (CURVE_SEGMENT == segType) {
					BezVertex bv = (BezVertex) vt;
					pt = matx.multiplyPointByProjCTM(coords[0], coords[1], pt);
					bv.setCx1((float) pt.getX());
					bv.setCy1((float) pt.getY());
					pt = matx.multiplyPointByProjCTM(coords[2], coords[3], pt);
					bv.setCx2((float) pt.getX());
					bv.setCy2((float) pt.getY());
					pt = matx.multiplyPointByProjCTM(coords[4], coords[5], pt);
					bv.setX((float) pt.getX());
					bv.setY((float) pt.getY());
				}
				else if (LINE_SEGMENT == segType) {
					LineVertex lv = (LineVertex) vt;
					pt = matx.multiplyPointByProjCTM(coords[0], coords[1], pt);
					lv.setX((float) pt.getX());
					lv.setY((float) pt.getY());
				}
			}
			
		}
		this.nullCoords();
	}
	/**
	 * Calls transform with this shape's ctm, current transformation matrix.
	 */
	public void transform() {
		transform(this.getCtm());
	}

	
	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS TO DRAW TO DISPLAY                                                                */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/

	/** 
	 * Draws this shape to the display. Calls beginShape and endShape on its own.
	 * If isMarked is true, will mark anchor and control points.
	 */
	public void draw() {
		if (!this.isVisible) return;
		parent.beginShape();
		if (hasFill()) {
			parent.fill(fillColor);
		}
		else {
			parent.noFill();
		}
		if (hasStroke()) {
			parent.stroke(strokeColor);
		}
		else {
			parent.noStroke();
		}
		parent.strokeWeight(weight);
		// equivalent to startPoint.draw(this.parent);
		parent.vertex(this.x, this.y);
		ListIterator<Vertex2DINF> it = curveIterator();
		int i = 0;
		while (it.hasNext()) {
			Vertex2DINF bez = it.next();
			bez.draw(parent);
		  if (isMarked) {
			if (bez.segmentType() == CURVE_SEGMENT) {
			 parent.pushStyle();
			 parent.noFill();
			 parent.stroke(192);
			 parent.strokeWeight(1);
			 BezVertex bz = (BezVertex)bez;
			 if (i > 0) {
				 parent.line(curves.get(i-1).x(), curves.get(i-1).y(), bz.cx1(), bz.cy1());
				 parent.line(bz.x(), bz.y(), bz.cx2(), bz.cy2());
			 }
			 else {
			   int w = 6;
			   parent.pushStyle();
			   parent.noStroke();
			   parent.fill(160);
			   // parent.square(x - w/2, y - w/2, w);
			   parent.rect(x - w/2, y - w/2, w, w);
			   parent.popStyle();
			   parent.line(x, y, bz.cx1(), bz.cy1());
			   parent.line(bz.x(), bz.y(), bz.cx2(), bz.cy2());
			 }
			 parent.popStyle();
		   }
		   bez.mark(parent);
		  }
		  i++;
			}
			if (isClosed()) {
				parent.endShape(PApplet.CLOSE);
			}
			else {
				parent.endShape();
			}
	}

	/** 
	 * Draws this shape to an offscreen PGraphics. Calls beginShape and endShape on its own. 
	 * It's up to the user to call beginDraw() and endDraw() on the PGraphics instance.
	 * If isMarked is true, draws marks for anchor and control points.
	 * @param pg   a PGraphics instance
	 */
	public void draw(PGraphics pg) {
		if (!this.isVisible) return;
		pg.beginShape();
		if (hasFill()) {
			pg.fill(fillColor);
		}
		else {
			pg.noFill();
		}
		if (hasStroke()) {
			pg.stroke(strokeColor);
		}
		else {
			pg.noStroke();
		}
		pg.strokeWeight(weight);
		// equivalent to startPoint.draw(this.parent);
		pg.vertex(this.x, this.y);
		ListIterator<Vertex2DINF> it = curveIterator();
		int i = 0;
		while (it.hasNext()) {
			Vertex2DINF bez = it.next();
			bez.draw(pg);
      if (isMarked) {
        if (bez.segmentType() == CURVE_SEGMENT) {
         pg.pushStyle();
         pg.noFill();
         pg.stroke(192);
         pg.strokeWeight(1);
         BezVertex bz = (BezVertex)bez;
         if (i > 0) {
        	 pg.line(curves.get(i-1).x(), curves.get(i-1).y(), bz.cx1(), bz.cy1());
        	 pg.line(bz.x(), bz.y(), bz.cx2(), bz.cy2());
         }
         else {
           int w = 6;
           pg.pushStyle();
           pg.noStroke();
           pg.fill(160);
           // pg.square(x - w/2, y - w/2, w);
           pg.rect(x - w/2, y - w/2, w, w);
           pg.popStyle();
           pg.line(x, y, bz.cx1(), bz.cy1());
           pg.line(bz.x(), bz.y(), bz.cx2(), bz.cy2());
         }
         pg.popStyle();
       }
       bez.mark(pg);
      }
      i++;
		}
		if (isClosed()) {
			pg.endShape(PApplet.CLOSE);
		}
		else {
			pg.endShape();
		}
	}


	/** 
	 * Draws this shape to the display. Calls beginShape and endShape on its own.
	 * Uses current fill, stroke and weight from Processing environment. Doesn't mark vertices.
	 */
	public void drawQuick() {
		parent.beginShape();
		// equivalent to startPoint.draw(this.parent);
		parent.vertex(this.x, this.y);
		ListIterator<Vertex2DINF> it = curveIterator();
		while (it.hasNext()) {
			Vertex2DINF bez = it.next();
			bez.draw(parent);
		}
		if (isClosed()) {
			parent.endShape(PApplet.CLOSE);
		}
		else {
			parent.endShape();
		}
	}


	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS TO DRAW TO WRITE TO ADOBE ILLUSTRATOR FILE FORMAT                                 */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/

	
	/** 
	 * Writes Adobe Illustrator 7.0 tags describing the geometry of this shape to PrintWriter.
	 * Brackets calls to AIFIleWriter curve and line drawing methods with AIFIleWriter.psMoveTo and AIFIleWriter.paintPath. 
	 * Sets fill, stroke, and weight from values for fillColor, strokeColor and weight, where applicable. If you
	 * want to output transparency, call {@link #setUseTransparency(boolean) setUseTransparency()} with a argument of
	 * <code>true</code> before calling <code>write()</code>. Because Illustrator does not support separate transparency
	 * for fill and stroke, assigning a transparent color to either on will make the the entire object transparent. 
	 * If stroke and fill transparency have different values, stroke transparency will be assigned to the object.
	 */
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
		boolean transparencySet = false;
		int pathOp = 0;
		if (hasFill()) {
			int[] colors = Palette.argbComponents(fillColor);
			if (colors[0] < 255 && AIFileWriter.useTransparency) {
				AIFileWriter.setTransparency(colors[0]/255.0, pw);
				transparencySet = true;
			}
			AIFileWriter.setRGBFill(colors[1]/255.0, colors[2]/255.0, colors[3]/255.0, pw);
			pathOp += AIFileWriter.FILL;
		}
		if (hasStroke()) {
			int[] colors = Palette.argbComponents(strokeColor);
			if (colors[0] < 255 && AIFileWriter.useTransparency) {
				if (!transparencySet) {
					AIFileWriter.setTransparency(colors[0]/255.0, pw);
					transparencySet = true;
				}
			}
			AIFileWriter.setRGBStroke(colors[1]/255.0, colors[2]/255.0, colors[3]/255.0, pw);
			pathOp += AIFileWriter.STROKE;
			AIFileWriter.setWeight(weight(), pw);
		}
		// the startPoint differently, without calling its write() method. 
		AIFileWriter.psMoveTo(x, y, pw);
		ListIterator<Vertex2DINF> it = curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			vt.write(pw);
		}
		if (isClosed()) {
			pathOp += AIFileWriter.CLOSE;
		}
		AIFileWriter.paintPath(pathOp, pw);
		if (AIFileWriter.useTransparency && transparencySet) {
			AIFileWriter.noTransparency(pw);
		}
		if (bracketVisible) AIFileWriter.setVisible(true, pw);
		if (bracketLocked) AIFileWriter.setLocked(false, pw);
	}

	/** 
	 * Writes Adobe Illustrator 7.0 tags describing the geometry of this shape to PrintWriter.
	 * Brackets calls to AIFIleWriter curve and line drawing methods with AIFIleWriter.psMoveTo and AIFIleWriter.paintPath. 
	 * Omits fill and stroke and weight calls: these are assumed to be already set in current Illustrator graphics state. 
	 * Omits visibility and locking status: these are also assumed to be set in the current graphics state.
	 * Closure, fill, and stroke rendering of the path are determined by the pathOp parameter. If you have set transparency, 
	 * you'll need to call AIFileWriter.noTransparency when your graphics calls are finished. This is not called from the 
	 * DisplayComponent hierarchy, which always calls {@link #write(PrintWriter) write(PrintWriter)}. It is primarily of use
	 * in writing out files with direct calls to AiFileWriter. 
	 * @param pathOp   @see net.paulhertz.aifile.AIFileWriter#paintPath
	 * @param output   a PrintWriter that writes vector graphics in the Adobe Illustrator 7.0 file format
	 */
	public void write(int pathOp, PrintWriter output) {
		AIFileWriter.psMoveTo(x, y, output);
		ListIterator<Vertex2DINF> it = curveIterator();
		while (it.hasNext()) {
			Vertex2DINF vt = it.next();
			vt.write(output);
		}
		AIFileWriter.paintPath(pathOp, output);
	}


	/*-------------------------------------------------------------------------------------------*/
	/*                                                                                           */
	/* METHODS TO IMPLEMENT VISITABLE INTERFACE                                                  */ 
	/*                                                                                           */
	/*-------------------------------------------------------------------------------------------*/
	
	
	/* (non-Javadoc)
	 * This is a terminal node, no children to visit
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor) {
		visitor.visitBezShape(this);
	}

	/* (non-Javadoc)
	 * This is a terminal node, no children to visit
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor, boolean order) {
		visitor.visitBezShape(this);
		
	}
	


	
	/*-------------------------------------------------------------------------------------------------------*/
	/*                                                                                                       */
	/* STATIC METHODS FOR SHAPE CREATION                                                                     */ 
	/*                                                                                                       */
	/* These are mostly here for legacy purposes. There are now subclasses implementing each type of shape   */
	/* and they return instances of the subclass, not of the parent BezShape class. The subclasses are       */
	/* built on the factory method pattern, and offer several different signatures for creating shapes.      */
	/*                                                                                                       */
	/* In IgnoCodeLib 0.3 these methods are deprecated, though still supported.                              */
	/*                                                                                                       */
	/*-------------------------------------------------------------------------------------------------------*/

	
	/**
	 * Creates a circular BezShape with sectors number of Bezier curves.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * @param sectors   number of equal divisions of circle
	 * @param radius    radius of circle
	 * @param xctr      x-coordinate of center of circle
	 * @param yctr      y-coordinate of center of circle
	 * @return          a circular BezShape of type BezShape.BEZ_CIRCLE
	 * @deprecated		Call factory methods in the returned class instead. 
	 */
	public static BezCircle bezCircle(PApplet parent, float xctr, float yctr, float radius, int sectors) {
		return BezCircle.makeCenterRadiusSectors(parent, xctr, yctr, radius, sectors);
	}
	
	/**
	 * Creates an elliptical BezShape with sectors number of Bezier curves.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * @param sectors   integer for number of equal divisions of circle
	 * @param width     width of the ellipse
	 * @param height    height of the ellipse
	 * @param xctr      x-coordinate of center of circle
	 * @param yctr      y-coordinate of center of circle
	 * @return          an elliptical BezShape of type BezShape.BEZ_ELLIPSE
	 * @deprecated		Call factory methods in the returned class instead. 
	 */
	public static BezEllipse bezEllipse(PApplet parent, float xctr, float yctr, float width, float height, int sectors) {
		return BezEllipse.makeCenterWidthHeightSectors(parent, xctr, yctr, width, height, sectors);
	}
	
	
	/**
	 * Constructs a regular polygon BezShape with <code>sides</code> edges and radius <code>radius</code>.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * @param xctr     x-coordinate of center of polygon
	 * @param yctr     y-coordinate of center of polygon
	 * @param radius   radius of the polygon
	 * @param sides    number of sides of the polygon
	 * @return         a polygonal BezShape of type BezShape.BEZ_REGULAR_POLY
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezRegularPoly bezRegularPoly(PApplet parent, float xctr, float yctr, float radius, int sides) {
		return BezRegularPoly.makeCenterRadiusSides(parent, xctr, yctr, radius, sides);
	}
	
	
	/**
	 * Returns a multi-segment B�zier curve from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The first coordinate pair is the initial anchor point, the following coordinate pairs correspond
	 * to the first control point, second control point, and final anchor point of each additional curve.
	 * The final anchor point and the first coordinate pair should be identical for a properly closed shape.
	 * The shape will be closed and consist only of Bezier curves with no straight lines. 
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment curved line of type BezShape.BEZ_MULTICURVE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezCurveShape bezCurveShape(PApplet parent, float[] coords) {
		return BezCurveShape.makeCurvePoly(parent, coords);
	}

	
	/**
	 * Constructs an equilateral triangle with supplied center (xctr, yctr) and radius.
	 * The base is aligned with the x-axis, apex points down. 
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * @param xctr     x-coordinate of center of triangle
	 * @param yctr     y-coordinate of center of triangle
	 * @param radius   radius of the triangle
	 * @return         an equilateral triangle BezShape of type BezShape.BEZ_TRIANGLE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezTriangle bezTriangle(PApplet parent, float xctr, float yctr, float radius) {
		return BezTriangle.makeCenterRadius(parent, xctr, yctr, radius);
	}
	
	/**
	 * Constructs a triangle from three points.
	 * Fill, stroke, and weight of shapes are set from their values in the Processing environment.
	 * @param x1   x-coordinate of first point
	 * @param y1   y-coordinate of first point
	 * @param x2   x-coordinate of second point
	 * @param y2   y-coordinate of second point
	 * @param x3   x-coordinate of third point
	 * @param y3   y-coordinate of third point
	 * @return     a triangular BezShape of type BezShape.BEZ_TRIANGLE
	 * @deprecated		Call factory methods in the returned class instead. 
	 */
	public static BezTriangle bezTriangle(PApplet parent, float x1, float y1, float x2, float y2, float x3, float y3) {
		return BezTriangle.makeThreePoints(parent, x1, y1, x2, y2, x3, y3);
	}

	/**
	 * Constructs a rectangular BezShape.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * @param left     the left x-coordinate of the rectangle
	 * @param top      the top y-coordinate of the rectangle
	 * @param right    the right x-coordinate of the rectangle
	 * @param bottom   the bottom y-coordinate of the rectangle
	 * @return         a rectangular BezShape of type BezShape.BEZ_RECTANGLE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezRectangle bezRectangle(PApplet parent, float left, float top, float right, float bottom) {
		return BezRectangle.makeLeftTopRightBottom(parent, left, top, right, bottom);
	}
	

	/**
	 * Constructs a straight line between (x1, y1) and (x2, y2).
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * @param x1   x-coordinate of first point
	 * @param y1   y-coordinate of first point
	 * @param x2   x-coordinate of second point
	 * @param y2   y-coordinate of second point
	 * @return     a straight line BezShape of type BezShape.BEZ_LINE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezLine bezLine(PApplet parent, float x1, float y1, float x2, float y2) {
		return BezLine.makeCoordinates(parent, x1, y1, x2, y2);
	}

	
	/**
	 * Constructs a single Bezier curve.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * @param ax1   x-coordinate of initial anchor point
	 * @param ay1   y-coordinate of initial anchor point
	 * @param cx1   x-coordinate of first control point 
	 * @param cy1   y-coordinate of first control point
	 * @param cx2   x-coordinate of second control point
	 * @param cy2   y-coordinate of second control point
	 * @param ax2   x-coordinate of terminal anchor point
	 * @param ay2   y-coordinate of terminal anchor point
	 * @return      a curved line BezShape of type BezShape.BEZ_CURVE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezShape bezCurve(PApplet parent, float ax1, float ay1, float cx1, float cy1, float cx2, float cy2, 
			float ax2, float ay2) {
		return BezCurve.makeCurve(parent, ax1, ay1, cx1, cy1, cx2, cy2, ax2, ay2);
	}

	
	/**
	 * Constructs a multi-segment line from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment line of type BezShape.BEZ_MULTILINE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezMultiLine bezMultiLine(PApplet parent, float[] coords) {
		return BezMultiLine.makeMultiLine(parent, coords);
	}
	
	/**
	 * Constructs a multi-segment line from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment. 
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment line of type BezShape.BEZ_MULTILINE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezPoly bezPoly(PApplet parent, float[] coords) {
		return BezPoly.makePoly(parent, coords);
	}
	
	/**
	 * Constructs a multi-segment B�zier curve from an array of float.
	 * Fill, stroke, and weight are set from their values in the Processing environment.
	 * The first coordinate pair is the initial anchor point, the following coordinate pairs correspond
	 * to the first control point, second control point, and final anchor point of each additional curve.
	 * @param coords   an array of coordinate pairs
	 * @return         a multi-segment curved line of type BezShape.BEZ_MULTICURVE
	 * @deprecated	   Call factory methods in the returned class instead. 
	 */
	public static BezMultiCurve bezMultiCurve(PApplet parent, float[] coords) {
		return BezMultiCurve.makeMultiCurve(parent, coords);
	}

}
