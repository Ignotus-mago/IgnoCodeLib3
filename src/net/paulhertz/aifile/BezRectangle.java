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

import net.paulhertz.geom.Matrix3;

import processing.core.PApplet;

/**
 * Provides factory methods to create and operate on closed rectangular shapes. 
 * In methods that do not include a reference to a PApplet in the signature, 
 * the PApplet used for calls to the Processing environment is obtained from 
 * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
 * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
 */
public class BezRectangle extends BezShape {
	protected float left;
	protected float top;
	protected float right;
	protected float bottom;
	protected float width;
	protected float height;
	protected boolean needsUpdate;
	protected boolean isAligned;

	protected BezRectangle(PApplet parent) {
		this(parent, 0, 0, 1, 1);
	}
	
	protected BezRectangle(PApplet parent, float left, float top, float width, float height) {
		super(parent, left, top, true);
		this.left = left;
		this.top = top;
		this.right = left + width;
		this.bottom = top + height;
		this.width = width;
		this.height = height;
		this.append(right, top);
		this.append(right, bottom);
		this.append(left, bottom);
		this.append(left, top);
		this.setBezType(BezType.BEZ_RECTANGLE);
		this.needsUpdate = false;
		this.isAligned = true;
		this.setCenter(left + width/2.0f, top + height/2.0f);
	}
	
	public static BezRectangle makeLeftTopWidthHeight(PApplet parent, float left, float top, float width, float height) {
		return new BezRectangle(parent, left, top, width, height);
	}
	public static BezRectangle makeLeftTopWidthHeight(float left, float top, float width, float height) {
		return new BezRectangle(IgnoCodeLib.getMyParent(), left, top, width, height);
	}
	
	public static BezRectangle makeCenterWidthHeight(PApplet parent, float xctr, float yctr, float width, float height) {
		return new BezRectangle(parent, xctr - width/2, yctr - height/2, width, height);
	}
	public static BezRectangle makeCenterWidthHeight(float xctr, float yctr, float width, float height) {
		return new BezRectangle(IgnoCodeLib.getMyParent(), xctr - width/2, yctr - height/2, width, height);
	}

	public static BezRectangle makeLeftTopRightBottom(PApplet parent, float left, float top, float right, float bottom) {
		return new BezRectangle(parent, left, top, right - left, bottom - top);
	}
	public static BezRectangle makeLeftTopRightBottom(float left, float top, float right, float bottom) {
		return new BezRectangle(IgnoCodeLib.getMyParent(), left, top, right - left, bottom - top);
	}
	
	public static BezRectangle makeRectangle(BezRectangle r) {
		BezRectangle newRectangle =  new BezRectangle(r.parent, r.getLeft(), r.getTop(), r.width, r.height);
		newRectangle.setHasStroke(r.hasStroke());
		newRectangle.setStrokeColor(r.strokeColor());
		newRectangle.setStrokeOpacity(r.strokeOpacity());
		newRectangle.setWeight(r.weight());
		newRectangle.setHasFill(r.hasFill());
		newRectangle.setFillColor(r.fillColor());
		newRectangle.setFillOpacity(r.fillOpacity());
		return newRectangle;
	}
	
	
	/**
	 * Updates rectangle coordinates, width and height.
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public void setLeftTopWidthHeight(float left, float top, float width, float height) {
		this.left = left;
		this.top = top;
		this.right = left + width;
		this.bottom = top + height;
		this.width = width;
		this.height = height;
		this.setX(left);
		this.setY(top);
		LineVertex vtx = (LineVertex) this.curves().get(0);
		vtx.setX(right);
		vtx.setY(top);
		vtx = (LineVertex) this.curves().get(1);
		vtx.setX(right);
		vtx.setY(bottom);
		vtx = (LineVertex) this.curves().get(2);
		vtx.setX(left);
		vtx.setY(bottom);
		vtx = (LineVertex) this.curves().get(3);
		vtx.setX(left);
		vtx.setY(top);
		this.needsUpdate = false;
		this.isAligned = true;
		this.setCenter(left + width/2.0f, top + height/2.0f);		
	}
	
	/**
	 * Updates rectangle coordinates, width and height.
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setLeftTopRightBottom(float left, float top, float right, float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.width = right - left;
		this.height = bottom - top;
		this.setX(left);
		this.setY(top);
		LineVertex vtx = (LineVertex) this.curves().get(0);
		vtx.setX(right);
		vtx.setY(top);
		vtx = (LineVertex) this.curves().get(1);
		vtx.setX(right);
		vtx.setY(bottom);
		vtx = (LineVertex) this.curves().get(2);
		vtx.setX(left);
		vtx.setY(bottom);
		vtx = (LineVertex) this.curves().get(3);
		vtx.setX(left);
		vtx.setY(top);
		this.needsUpdate = false;
		this.isAligned = true;
		this.setCenter(left + width/2.0f, top + height/2.0f);		
	}
		
	// TODO consider how best to obtain and format this
	public float[] getCoords() {
		// include startPoint in the array 
		float[] coords = new float[this.curves().size() * 2 + 2];
		int i = 0;
		coords[i++] = this.x();
		coords[i++] = this.y();
		for (Vertex2DINF pt : super.curves()) {
			coords[i++] = pt.x();
			coords[i++] = pt.y();
		}
		return coords;
	}
	
	public float getLeft() {
		return this.left;
	}
	
	public float getTop() {
		return this.top;
	}
	
	public float getRight() {
		return this.right;
	}
	
	public float getBottom() {
		return this.bottom;		
	}
	
	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}
	
	public LineVertex getTopLeft() {
		// return this.curves().get(3).clone();
		return new LineVertex(this.left, this.top);
	}
	
	public Vertex2DINF getBottomRight() {
		// return this.curves().get(1).clone();
		return new LineVertex(this.right, this.bottom);
	}
	
	public LineVertex getCenterPoint() {
		return new LineVertex((this.right + this.left)/2.0f, (this.bottom + this.top)/2.0f);
	}

	public float getArea() {
		return this.width * this.height;
	}
	
	public double getDiagonalLength() {
		return Math.sqrt(this.width * this.width + this.height * this.height);
	}

	/**
	 * Determines if one rectangle contains another rectangle
	 * @param one       the possibly containing rectangle
	 * @param another   the rectangle to check for containment
	 * @return          {@code true} if one rectangle contains another, {@code false} otherwise
	 */
	public static boolean contains(BezRectangle one, BezRectangle another) {
        return (   another.getTop()    >= one.getTop()
                && another.getBottom() <= one.getBottom()
                && another.getLeft()   >= one.getLeft()
                && another.getRight()  <= one.getRight());
	}

	/**
	 * Determines if this rectangle contains a specified rectangle.
	 * @param r   the rectangle to check 
	 * @return    {@code true} if this rectangle contains r, {@code false} otherwise
	 */
	public boolean contains(BezRectangle r) {
        return (   r.getTop()    >= this.top
                && r.getBottom() <= this.bottom
                && r.getLeft()   >= this.left
                && r.getRight()  <= this.right);
	}
	
	/**
	 * Determines if one rectangle overlaps another rectangle
	 * @param one       one rectangle to check for overlap
	 * @param another   another rectangle to check for overlap
	 * @return          {@code true} if one rectangle overlaps another, {@code false} otherwise
	 */
	public static boolean overlaps(BezRectangle one, BezRectangle another) {
		return (   another.getBottom() >= one.getTop()
				&& another.getTop()    <= one.getBottom()
				&& another.getRight()  >= one.getLeft()
				&& another.getLeft()   <= one.getRight());
	}

	/**
	 * Determines if this rectangle overlaps a specified rectangle
	 * @param r   the rectangle to check against this one
	 * @return    {@code true} if this rectangle overlaps r, {@code false} otherwise
	 */
	public boolean overlaps(BezRectangle r) {
		return (   r.getBottom() >= this.top
				&& r.getTop()    <= this.bottom
				&& r.getRight()  >= this.left
				&& r.getLeft()   <= this.right);
	}
	
	/**
	 * @param lim     the limiting rectangle
	 * @return        outcode indicating relation of this rectangle to lim as follows:
	 * 
	 *       |   |
	 *     1 | 4 | 7
	 *     ___|___|___
	 *       |   |
	 *     2 | 5 | 8 
	 *     ___|___|___
	 *       |   |
	 *     3 | 6 | 9
	 *       |   |   	  
	 *       
	 * where 5 indicates the two rectangles overlap, all other outcodes indicate no overlap
	 * with this rectangle located in one of 8 sectors, as shown.
	 */
	public int overlapOutcode(BezRectangle lim) {
		  int code = 0;
		  if (this.right < lim.left ) {
			  if (this.bottom < lim.top) {
				  code = 1;
			  }
			  else if (this.top > lim.bottom) {
				  code = 3;
			  }
			  else {
				  code = 2;
			  }
		  }
		  else if (this.left > lim.right) {
			  if (this.bottom < lim.top) {
				  code = 7;
			  }
			  else if (this.top > lim.bottom) {
				  code = 9;
			  }
			  else {
				  code = 8;
			  }
		  }
		  else {
			  if (this.bottom < lim.top) {
				  code = 4;
			  }
			  else if (this.top > lim.bottom) {
				  code = 6;
			  }
			  else {
				  code = 5;
			  }
		  }
		  
		  return code;
	}

	/**
	 * @param targ    rectangle to test against a limiting rectangle
	 * @param lim     the limiting rectangle
	 * @return        outcode indicating relation of two rectangles as follows:
	 * 
	 *       |   |
	 *     1 | 4 | 7
	 *     ___|___|___
	 *       |   |
	 *     2 | 5 | 8 
	 *     ___|___|___
	 *       |   |
	 *     3 | 6 | 9
	 *       |   |   	  
	 *       
	 * where 5 indicates the two rectangles overlap, all other outcodes indicate no overlap
	 * with targ located in one of 8 sectors, as shown.
	 */
	public static int overlapOutcode(BezRectangle targ, BezRectangle lim) {
		  int code = 0;
		  if (targ.right < lim.left ) {
			  if (targ.bottom < lim.top) {
				  code = 1;
			  }
			  else if (targ.top > lim.bottom) {
				  code = 3;
			  }
			  else {
				  code = 2;
			  }
		  }
		  else if (targ.left > lim.right) {
			  if (targ.bottom < lim.top) {
				  code = 7;
			  }
			  else if (targ.top > lim.bottom) {
				  code = 9;
			  }
			  else {
				  code = 8;
			  }
		  }
		  else {
			  if (targ.bottom < lim.top) {
				  code = 4;
			  }
			  else if (targ.top > lim.bottom) {
				  code = 6;
			  }
			  else {
				  code = 5;
			  }
		  }
		  
		  return code;
	}

	
    /**
     * @param x   x-coordinate of point to test
     * @param y   y-coordinate of point to test
     * @param r   rectangle to test
     * @return    {@code true} if r contains the specified point
     */
    public static boolean containsPoint(float x, float y, BezRectangle r) {
        return (    x >= r.getLeft() && x <= r.getRight()
                 && y >= r.getTop()  && y <= r.getBottom());
    }

    /* (non-Javadoc)
     * @see net.paulhertz.aifile.BezShape#containsPoint(float, float)
     */
    public boolean containsPoint(float x, float y) {
        return (    x >= this.left && x <= this.right
                 && y >= this.top  && y <= this.bottom);
    }
	
	
    /**
     * Updates local tracking of rectangle top, left, bottom, right, width and height.
     * Sets needsUpdate to false. If the {@link #bounds(PApplet)} of this BezRectangle
     * no longer corresponds to the top, left, bottom and right, {@link #isAligned} will 
     * be set to false to flag that the rectangle has been transformed out of alignment with
     * the x and y axes of the world system.
     * Call after any geometric transformation of the points of this rectangle by your code.
     * Methods in this class that change geometry will call update for you.
     */
    public void update() {
		this.left = this.x();
		this.top = this.y();
		Vertex2DINF br = this.curves().get(1);
		this.right = br.x();
		this.bottom = br.y();
		this.width = this.right - this.left;
		this.height = this.bottom - this.top;
		this.needsUpdate = false;
		float[] box = super.bounds(parent);
		if (  box[0] == this.left 
			  && box[1] == this.top
			  && box[2] == this.right
			  && box[3] == this.bottom) {
			this.isAligned = true;
		}
		else {
			this.isAligned = false;
		}
	}

	/**
	 * convenience method to allow you to check the need for an update, which you set earlier
	 * @return the needsUpdate
	 */
	public boolean isNeedsUpdate() {
		return needsUpdate;
	}

	/**
	 * convenience method to allow you to flag the need for an update
	 * @param needsUpdate the needsUpdate to set
	 */
	public void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}

	/**
	 * @return the isAligned
	 */
	public boolean isAligned() {
		return isAligned;
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#rotateShape(float)
	 */
	@Override
	public void rotateShape(float theta) {
		super.rotateShape(theta);
		update();
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#scaleShape(float, float)
	 */
	@Override
	public void scaleShape(float xScale, float yScale) {
		super.scaleShape(xScale, yScale);
		update();
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#scaleShape(float)
	 */
	@Override
	public void scaleShape(float xyScale) {
		super.scaleShape(xyScale);
		update();
	}
	
	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#scaleShape(float, float, float, float)
	 */
	@Override
	public void scaleShape(float xScale, float yScale, float x0, float y0) {
		super.scaleShape(xScale, yScale, x0, y0);
		update();
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#scaleShape(float, float, float)
	 */
	@Override
	public void scaleShape(float xyScale, float x0, float y0) {
		super.scaleShape(xyScale, x0, y0);
		update();
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#transform(net.paulhertz.geom.Matrix3)
	 */
	@Override
	public void transform(Matrix3 matx) {
		super.transform(matx);
		update();
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.BezShape#translateShape(float, float)
	 */
	@Override
	public void translateShape(float xTrans, float yTrans) {
		super.translateShape(xTrans, yTrans);
		update();
	}
	
	/**
	 * @return	a BezRectangle with coordinates rounded to the nearest integer value
	 */
	public BezRectangle roundCoordinates() {
		return BezRectangle.makeLeftTopWidthHeight(this.parent, (float) Math.round(this.left), (float) Math.round(this.top), 
				(float) Math.round(this.width), (float) Math.round(this.height));
	}
	
	/**
	 * Returns a rectangle inset by the specified values. Negative values will outset the 
	 * new rectangle with respect to this rectangle. Works with aligned rectangles.
	 * @param insetWidth
	 * @param insetHeight
	 * @return 	a new BezRectangle inset (or outset) from the original.
	 */
	public BezRectangle inset(float insetWidth, float insetHeight) {
		insetWidth = Math.min(insetWidth, this.width/2);
		insetHeight = Math.min(insetHeight, this.height/2);
		return BezRectangle.makeLeftTopWidthHeight(this.parent, this.left + insetWidth, this.top + insetHeight, 
				width - 2 * insetWidth, height - 2 * insetHeight);
	}
	
	/**
	 * Calculates the intersection of this rectangle with a supplied rectangle, returns the result as a rectangle.
	 * Results will not be accurate unless both rectangles are aligned with x and y axes, i.e., {@link #isAligned} == true.
	 * @param r
	 * @return	a new rectangle created by intersection of this rectangle with the supplied rectangle.
	 */
	public BezRectangle intersect(BezRectangle r) {
        return BezRectangle.makeLeftTopRightBottom(
        		this.parent,
                Math.max(this.left, r.left),
                Math.max(this.top, r.top),
                Math.min(this.right, r.right),
                Math.min(this.bottom, r.bottom));
	}
	
	/**
	 * Clip the supplied rectangle to this rectangle;  both rectangles should be aligned to x and y axes.
	 * @param r  rectangle to clip
	 * @return   a new rectangle clipped to the bounds of this rectangle.
	 */
	public BezRectangle pin(BezRectangle r) {
		// Pin top and left of r to this rectangle
		float result_left = 
			Math.min(Math.max(this.left, r.left), this.right);
		float result_top = 
			Math.min(Math.max(this.top, r.top), this.bottom);
		// Slide other rectangle inside this (if possible)
		float max_width = width - result_left;
		if (r.width > max_width)
			result_left = Math.max(this.right - r.width, this.left);
		float max_height = height - result_top;
		if (r.height > max_height)
			result_top = Math.max(this.bottom - r.height, this.top);
		// Clip the resulting rectangle to this rectangle's width and height
		float result_width =
			Math.min(r.width, this.width - result_left);
		float result_height =
			Math.min(r.height, this.height - result_top);
		// Return resulting rectangle
		return BezRectangle.makeLeftTopWidthHeight(this.parent, result_left, 
				result_top, result_width, result_height);
	}

	/**
	 * Stick the nearest edge of this rectangle to limitRect, if it doesn't overlap.
	 * @param r   the "sticky" rectangle
	 * @return    a rectangle with the same dimensions as this, stuck to limitRect.
	 */
	public BezRectangle stick(BezRectangle r) {
		int outcode = overlapOutcode(r);
		switch(outcode) {
		case 1: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					r.left - this.width, r.top - this.height, this.width, this.height);
		}
		case 2: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					r.left - this.width, this.top, this.width, this.height);
		}
		case 3: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					r.left - this.width, r.bottom, this.width, this.height);
		}
		case 4: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					this.left, r.top - this.height, this.width, this.height);
		}
		case 5: {
			// rectangles overlap
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					this.left, this.top, this.width, this.height);
		}
		case 6: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					this.left, r.bottom, this.width, this.height);
		}
		case 7: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					r.right, r.top - this.height, this.width, this.height);
		}
		case 8: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					r.right, this.top, this.width, this.height);
		}
		case 9: {
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					r.right, r.bottom, this.width, this.height);
		}
		default: {
			// rectangles overlap
			return BezRectangle.makeLeftTopWidthHeight(this.parent, 
					this.left, this.top, this.width, this.height);
		}
		}
	}


   /**
     * Wrap supplied rectangle in the smallest rectangle that contains it and this rectangle;
     * Both rectangles should be aligned.
     * @param r
     * @return  a new rectangle that contains both this and the supplied rectangle.
     */
    public BezRectangle wrap(BezRectangle r) {
        return BezRectangle.makeLeftTopRightBottom(this.parent, 
            Math.min(this.left, r.left), Math.min(this.top, r.top),
            Math.max(this.right, r.right), Math.max(this.bottom, r.bottom));
    }
    
	/** 
	 * Interpolates a value within a range.
	 * linear interpolation from l (when a=0) to h (when a=1)
	 * (equal to (a * h) + (1 - a) * l) 
	 */
	public static double lerp(double a, double l, double h) { return l + a * (h - l); }

	/**
	 * Maps a value from one range to another.
	 * @param v     value to interpolage
	 * @param vlo   low value of source range
	 * @param vhi   high value of source range
	 * @param dlo   low value of destination range
	 * @param dhi   high value of destination range
	 * @return      value linearly interpolated from source range to destination range
	 */
	public static double map(double v, double vlo, double vhi, double dlo, double dhi) {
		double a = (v - vlo)/(vhi - vlo);
		return lerp(a, dlo, dhi);
	}

	/**
	 * @param vt1   a LineVertex for mapping from this rectangle to another
	 * @param r2    the destination rectangle
	 * @return      a LineVertex mapped to the destination rectangle
	 */
	public Vertex2DINF map(Vertex2DINF vt1, BezRectangle r2) {
    	double x0 = vt1.x();
    	double vlo = this.getLeft();
    	double vhi = this.getRight();
    	double dlo = r2.getLeft();
    	double dhi = r2.getRight();
    	double x1 = BezRectangle.map(x0, vlo, vhi, dlo, dhi);
    	double y0 = vt1.y();
    	vlo = this.getTop();
    	vhi = this.getBottom();
    	dlo = r2.getTop();
    	dhi = r2.getBottom();
    	double y1 = BezRectangle.map(y0, vlo, vhi, dlo, dhi);
    	return new LineVertex((float) x1, (float) y1);
    }

	/**
	 * @param vt1   a LineVertex for interpolation
	 * @param r1	a source rectangle for mapping
	 * @param r2    a destination rectangle for mapping 
	 * @return      a LineVertex mapped to the destination rectangle
	 */
	public static Vertex2DINF map(Vertex2DINF vt1, BezRectangle r1, BezRectangle r2) {
    	double x0 = vt1.x();
    	double vlo = r1.getLeft();
    	double vhi = r1.getRight();
    	double dlo = r2.getLeft();
    	double dhi = r2.getRight();
    	double x1 = BezRectangle.map(x0, vlo, vhi, dlo, dhi);
    	double y0 = vt1.y();
    	vlo = r1.getTop();
    	vhi = r1.getBottom();
    	dlo = r2.getTop();
    	dhi = r2.getBottom();
    	double y1 = BezRectangle.map(y0, vlo, vhi, dlo, dhi);
     	return new LineVertex((float) x1, (float) y1);
    }

	
}
