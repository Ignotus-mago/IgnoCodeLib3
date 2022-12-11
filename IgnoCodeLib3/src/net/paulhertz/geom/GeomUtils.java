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
package net.paulhertz.geom;
import java.awt.geom.*;

// rotations are CCW in Cartesian system, but CW in Processing display coordinate system

/**
 * @author Paul Hertz
 */
/**
 * Provides static methods for basic geometric transforms.
 * Methods for translating, scaling, and rotating a point, with single precision (using floats).
 * Rotation and scaling center on (0,0) unless you call scaleCoorAroundPoint or rotateCoorAroundPoint.
 * Rotation is counterclockwise in Cartesian coordinate system (positive y-axis points up)
 * and clockwise in screen display system (positive y-axis points down).
 * Methods that accept separate x and y coordinates return a new Point2D.
 * Methods that accept a Point2D modify and return the point (this allows chaining of methods).
 * It's possible to do all this with matrices, of course, and often (but not always) more efficient. 
 */
public class GeomUtils {
	public static final double TWO_PI = 2 * Math.PI;
	// private GeomUtils gu;

	private GeomUtils() {

	}

	/** take sign of a, either -1, 0, or 1 */
	public static int zsgn(double a) { return ((a) < 0) ? -1 : (a) > 0 ? 1 : 0; }
	/** take sign of a, either -1, 0, or 1 */
	public static int zsgn(float a)  { return ((a) < 0) ? -1 : (a) > 0 ? 1 : 0; }
	/** take sign of a, either -1, 0, or 1 */
	public static int zsgn(int a)    { return ((a) < 0) ? -1 : (a) > 0 ? 1 : 0; }
	/** take sign of a, either -1, 0, or 1 */
	public static int zsgn(long a)   { return ((a) < 0) ? -1 : (a) > 0 ? 1 : 0; }

	/** take binary sign of a, either -1, or 1 if >= 0 */
	public static int sgn(double a) { return ((a) < 0) ? -1 : 1; }
	/** take binary sign of a, either -1, or 1 if >= 0 */
	public static int sgn(float a) { return ((a) < 0) ? -1 : 1; }
	/** take binary sign of a, either -1, or 1 if >= 0 */
	public static int sgn(int a) { return ((a) < 0) ? -1 : 1; }
	/** take binary sign of a, either -1, or 1 if >= 0 */
	public static int sgn(long a) { return ((a) < 0) ? -1 : 1; }

	/** 
	 * Interpolates a value within a range.
	 * linear interpolation from l (when a=0) to h (when a=1)
	 * (equal to (a * h) + (1 - a) * l) 
	 */
	public static double lerp(double a, double l, double h) { return l + a * (h - l); }
	/** 
	 * Interpolates a value within a range.
	 * linear interpolation from l (when a=0) to h (when a=1)
	 * (equal to (a * h) + (1 - a) * l) 
	 */
	public static float lerp(float a, float l, float h) { return l + a * (h - l); }

	/** 
	 * clamps input v to the specified range from l to h
	 */
	public static double clamp(double v, double l, double h) { return (v < l) ? l : (v > h) ? h : v; }
	/** 
	 * clamps input v to the specified range from l to h
	 */
	public static float clamp(float v, float l, float h) { return (v < l) ? l : (v > h) ? h : v; }


	/*----   CARTESIAN GRAPHICS   ----*/
	/*---- Point Transformations ----*/

	/**
	 * scales a point by xScale and yScale, returns a new point
	 * @param x        x coordinate of point
	 * @param y        y coordinate of point
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 * @return         a new scaled point
	 */
	public static Point2D.Float scaleCoor(float x, float y, float xScale, float yScale) {
		return new Point2D.Float(x * xScale, y * yScale);
	}
	public static Point2D.Double scaleCoor(double x, double y, double xScale, double yScale) {
		return new Point2D.Double(x * xScale, y * yScale);
	}

	/**
	 * scales a point by xScale and yScale, modifies and returns the point
	 * @param pt       the point to scale
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 * @return         the scaled point
	 */
	public static Point2D.Float scaleCoor(Point2D.Float pt, float xScale, float yScale) {
		pt.setLocation(pt.x * xScale, pt.y * yScale);
		return pt;
	}
	public static Point2D.Double scaleCoor(Point2D.Double pt, double xScale, double yScale) {
		pt.setLocation(pt.x * xScale, pt.y * yScale);
		return pt;
	}


	/**
	 * scales a point by xScale and yScale arround a point (xctr, yctr), returns a new point
	 * @param x        x coordinate of point
	 * @param y        y coordinate of point
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 * @param xctr     x coordinate of center of transformation
	 * @param yctr     y coordinate of center of transformation
	 * @return         a new scaled point
	 */
	public static Point2D.Float scaleCoorAroundPoint(float x, float y, float xScale, float yScale, float xctr, float yctr) {
		float xout = xctr + (x - xctr) * xScale;
		float yout = yctr + (y - yctr) * yScale;
		return new Point2D.Float(xout, yout);
	}
	public static Point2D.Double scaleCoorAroundPoint(double x, double y, double xScale, double yScale, double xctr, double yctr) {
		double xout = xctr + (x - xctr) * xScale;
		double yout = yctr + (y - yctr) * yScale;
		return new Point2D.Double(xout, yout);
	}

	/**
	 * scales a point by xScale and yScale around a point (xctr, yctr), modifies and returns the point
	 * @param pt       the point to scale
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 * @param xctr     x coordinate of center of transformation
	 * @param yctr     y coordinate of center of transformation
	 * @return         the scaled point
	 */
	public static Point2D.Float scaleCoorAroundPoint(Point2D.Float pt, float xScale, float yScale, float xctr, float yctr) {
		translateCoor(pt, -xctr, -yctr);
		scaleCoor(pt, xScale, yScale);
		translateCoor(pt, xctr, yctr);
		return pt;
	}


	/**
	 * translates a point by xOffset and yOffset, returns a new point
	 * @param x         x coordinate of point
	 * @param y         y coordinate of point
	 * @param xOffset   distance to translate on x-xis
	 * @param yOffset   distance to translate on y-axis
	 * @return          a new translated point
	 */
	public static Point2D.Float translateCoor(float x, float y, float xOffset, float yOffset) {
		return new Point2D.Float(x + xOffset, y + yOffset);
	}
	public static Point2D.Double translateCoor(double x, double y, double xOffset, double yOffset) {
		return new Point2D.Double(x + xOffset, y + yOffset);
	}

	/**
	 * translates a point by xOffset and yOffset, modifies and returns the point
	 * @param pt        the point to translate
	 * @param xOffset   distance to translate on x-xis
	 * @param yOffset   distance to translate on y-axis
	 * @return          the translated point
	 */
	public static Point2D.Float translateCoor(Point2D.Float pt, float xOffset, float yOffset) {
		// move a vector or point (x,y) distances xOffset,yOffset
		// modifies and returns point
		pt.setLocation(pt.x + xOffset, pt.y + yOffset);
		return pt;
	}
	public static Point2D.Double translateCoor(Point2D.Double pt, double xOffset, double yOffset) {
		// move a vector or point (x,y) distances xOffset,yOffset
		// modifies and returns point
		pt.setLocation(pt.x + xOffset, pt.y + yOffset);
		return pt;
	}


	/**
	 * rotates a point theta radians, returns a new point
	 * rotation is counterclockwise for positive theta in Cartesian system, 
	 * clockwise in screen display coordinate system
	 * @param x       x coordinate of point
	 * @param y       y coordinate of point
	 * @param theta   angle to rotate, in radians
	 * @return        a new point
	 */
	public static Point2D.Float rotateCoor(float x, float y, float theta) {
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		return new Point2D.Float((float) (x * costheta - y * sintheta), (float) (x * sintheta + y * costheta));
	}
	public static Point2D.Double rotateCoor(double x, double y, double theta) {
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		return new Point2D.Double((x * costheta - y * sintheta), (x * sintheta + y * costheta));
	}

	/**
	 * rotates a point theta radians, modifies and returns the point
	 * rotation is counterclockwise for positive theta in Cartesian system, 
	 * clockwise in screen display coordinate system
	 * @param pt      the point to rotate
	 * @param theta   angle to rotate, in radians
	 * @return        the rotated point
	 */
	public static Point2D.Float rotateCoor(Point2D.Float pt, float theta) {
		// Rotate vector or point (x,y) through an angle theta
		// degrees in radians, rotation is counterclockwise from the coordinate axis
		// modifies and returns same point
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		pt.setLocation((pt.x * costheta - pt.y * sintheta), (pt.x * sintheta + pt.y * costheta));
		return pt;
	}
	public static Point2D.Double rotateCoor(Point2D.Double pt, double theta) {
		// Rotate vector or point (x,y) through an angle theta
		// degrees in radians, rotation is counterclockwise from the coordinate axis
		// modifies and returns same point
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		pt.setLocation((pt.x * costheta - pt.y * sintheta), (pt.x * sintheta + pt.y * costheta));
		return pt;
	}


	/**
	 * rotates a point theta radians around a point (xctr, yctr), returns a new point
	 * rotation is counterclockwise for positive theta in Cartesian system, 
	 * clockwise in screen display coordinate system
	 * @param x       x coordinate of point
	 * @param y       y coordinate of point
	 * @param xctr    x coordinate of center of rotation
	 * @param yctr    y coordinate of center of rotation
	 * @param theta   angle to rotate, in radians
	 * @return        a new rotated point
	 */
	public static Point2D.Float rotateCoorAroundPoint(float x, float y, float xctr, float yctr, float theta) {
		// Rotate vector or point (x,y) around point (xctr, yctr) through an angle theta
		// degrees in radians, rotation is counterclockwise from the coordinate axis
		// returns a new point
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		Point2D.Float pt = translateCoor(x, y, -xctr, -yctr);
		pt.setLocation((pt.x * costheta - pt.y * sintheta), (pt.x * sintheta + pt.y * costheta));
		return translateCoor(pt, xctr, yctr);
	}
	public static Point2D.Double rotateCoorAroundPoint(double x, double y, double xctr, double yctr, double theta) {
		// Rotate vector or point (x,y) around point (xctr, yctr) through an angle theta
		// degrees in radians, rotation is counterclockwise from the coordinate axis
		// returns a new point
		double sintheta = Math.sin(theta);
		double costheta = Math.cos(theta);
		Point2D.Double pt = translateCoor(x, y, -xctr, -yctr);
		pt.setLocation((pt.x * costheta - pt.y * sintheta), (pt.x * sintheta + pt.y * costheta));
		return translateCoor(pt, xctr, yctr);
	}

	/**
	 * rotates a point theta radians around a point (xctr, yctr), modifies and returns the point
	 * rotation is counterclockwise for positive theta in Cartesian system, 
	 * clockwise in screen display coordinate system
	 * @param pt      the point to rotate
	 * @param xctr    x coordinate of center of rotation
	 * @param yctr    y coordinate of center of rotation
	 * @param theta   angle to rotate, in radians
	 * @return        the rotated point
	 */
	public static Point2D.Float rotateCoorAroundPoint(Point2D.Float pt, float xctr, float yctr, float theta) {
		// Rotate vector or point (x,y) around point (xctr, yctr) through an angle theta
		// degrees in radians, rotation is counterclockwise from the coordinate axis
		// modifies and returns same point
		// center point around 0,0
		translateCoor(pt, -xctr, -yctr);
		// rotate
		rotateCoor(pt, theta);
		// translate back to xctr, yctr
		translateCoor(pt, xctr, yctr);
		return pt;
	}
	public static Point2D.Double rotateCoorAroundPoint(Point2D.Double pt, double xctr, double yctr, double theta) {
		// Rotate vector or point (x,y) around point (xctr, yctr) through an angle theta
		// degrees in radians, rotation is counterclockwise from the coordinate axis
		// modifies and returns same point
		// center point around 0,0
		translateCoor(pt, -xctr, -yctr);
		// rotate
		rotateCoor(pt, theta);
		// translate back to xctr, yctr
		translateCoor(pt, xctr, yctr);
		return pt;
	}


	/****************** UTILITY METHODS ******************/


	/**
	 * Given dx and dy displacements on x axis and y axis, returns the 
	 * angle of rotation, measured counterclockwise from 0 in Cartesian system, in radians.
	 * angle will be measured closckwise in screen display system
	 * @param dx   x coordinate of point
	 * @param dy   y coordinate of point
	 * @return     angle in radians
	 */
	public static float getAngle(float dx, float dy) {
		return (float) getAngle((double) dx, (double) dy);
	}
	public static double getAngle(double dx, double dy) {
		double angle;
		if (dx != 0) {
			angle = Math.atan(Math.abs(dy /dx));
		}
		else if (dy != 0) {
			angle = Math.PI / 2;
		}
		else {
			angle = 0;
		}
		if (dx < 0) {
			if (dy < 0) {
				angle = angle + Math.PI;
			}
			else {
				angle = Math.PI - angle;
			}
		}
		else if (dy < 0) {
			angle = TWO_PI - angle;
		}
		return angle;
	}


	/**
	 * decides if a point is inside a polygon
	 * @param npol   number of points in polygon
	 * @param xp     array of x-coordinates
	 * @param yp     array of y-coordinates
	 * @param x      x-coordinate of point
	 * @param y      y-coordinate of point
	 * @return       true if point is in polygon, false otherwise
	 */
	public static boolean pointInPoly(int npol, float[] xp, float[] yp, float x, float y) {
		int i, j = 0;
		boolean inside = false;
		for (i = 0, j = npol-1; i < npol; j = i++) {
			if (
					(((yp[i] <= y) && (y < yp[j])) || ((yp[j] <= y) && (y < yp[i]))) &&
					(x < (xp[j] - xp[i]) * (y - yp[i]) / (yp[j] - yp[i]) + xp[i])
			)
				inside = !inside;
		}
		return inside;
	}
	public static boolean pointInPoly(int npol, double[] xp, double[] yp, double x, double y) {
		int i, j = 0;
		boolean inside = false;
		for (i = 0, j = npol-1; i < npol; j = i++) {
			if (
					(((yp[i] <= y) && (y < yp[j])) || ((yp[j] <= y) && (y < yp[i]))) &&
					(x < (xp[j] - xp[i]) * (y - yp[i]) / (yp[j] - yp[i]) + xp[i])
			)
				inside = !inside;
		}
		return inside;
	}


	/**
	 * extracts array of x-coordinates from an array of points in x,y order
	 * @param arr   array of points in x,y order
	 * @return      array of x-coordinates (even index values)
	 */
	public static float[] xCoords( float[] arr ) {
		float[] xcoords = new float[arr.length/2];
		int index = 0;
		for ( int i = 0; i < arr.length; i += 2 ) {
			xcoords[index] = arr[i];
			index++;
		}
		return xcoords;
	}
	public static double[] xCoords( double[] arr ) {
		double[] xcoords = new double[arr.length/2];
		int index = 0;
		for ( int i = 0; i < arr.length; i += 2 ) {
			xcoords[index] = arr[i];
			index++;
		}
		return xcoords;
	}


	/**
	 * extracts array of y-coordinates from an array of points in x,y order
	 * @param arr   array of points in x,y order
	 * @return      array of y-coordinates (odd index values)
	 */
	public static float[] yCoords( float[] arr ) {
		float[] ycoords = new float[arr.length/2];
		int index = 0;
		for ( int i = 1; i < arr.length; i += 2 ) {
			ycoords[index] = arr[i];
			index++;
		}
		return ycoords;
	}
	public static double[] yCoords( double[] arr ) {
		double[] ycoords = new double[arr.length/2];
		int index = 0;
		for ( int i = 1; i < arr.length; i += 2 ) {
			ycoords[index] = arr[i];
			index++;
		}
		return ycoords;
	}


	/**
	 * finds squared distance between two points
	 * @param x0   x-coordinate of first point
	 * @param y0   y-coordinate of first point
	 * @param x1   x-coordinate of second point
	 * @param y1   y-coordinate of second point
	 * @return     squared distance between points
	 */
	public static float distSquared(float x0, float y0, float x1, float y1) {
		return (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
	}
	/**
	 * finds squared distance between two points
	 * @param x0   x-coordinate of first point
	 * @param y0   y-coordinate of first point
	 * @param x1   x-coordinate of second point
	 * @param y1   y-coordinate of second point
	 * @return     squared distance between points
	 */
	public static double distSquared(double x0, double y0, double x1, double y1) {
		return (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);
	}

	
	/**
	 * outputs area of polygon, negative if vertices are in CCW order.
	 * if last coordinate pair is a copy of first coordinate pair, area is unaffected
	 * @since October 21, 2011
	 */
	public static float area(int[] coords) {
		int len = coords.length;
		float area = 0.0f;
		int xold = coords[len - 2];
		int yold = coords[len - 1];
		for (int i = 0; i < len; i += 2) {
			int x = coords[i];
			int y = coords[i + 1];
		    area += (xold - x) * (yold + y);
		    xold = x;
		    yold = y;			
		}
		return area/2.0f;
	}
	/**
	 * outputs area of polygon, negative if vertices are in CCW order.
	 * if last coordinate pair is a copy of first coordinate pair, area is unaffected
	 * @since October 21, 2011
	 */
	public static float area(float[] coords) {
		int len = coords.length;
		float area = 0.0f;
		float xold = coords[len - 2];
		float yold = coords[len - 1];
		for (int i = 0; i < len; i += 2) {
			float x = coords[i];
			float y = coords[i + 1];
		    area += (xold - x) * (yold + y);
		    xold = x;
		    yold = y;			
		}
		return area/2.0f;
	}
	/**
	 * outputs area of polygon, negative if vertices are in CCW order.
	 * if last coordinate pair is a copy of first coordinate pair, area is unaffected
	 * @since October 21, 2011
	 */
	public static double area(double[] coords) {
		int len = coords.length;
		double area = 0.0f;
		double xold = coords[len - 2];
		double yold = coords[len - 1];
		for (int i = 0; i < len; i += 2) {
			double x = coords[i];
			double y = coords[i + 1];
		    area += (xold - x) * (yold + y);
		    xold = x;
		    yold = y;			
		}
		return area/2.0;
	}


}
