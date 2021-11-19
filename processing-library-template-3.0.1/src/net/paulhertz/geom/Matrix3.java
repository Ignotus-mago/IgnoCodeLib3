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

import java.awt.geom.Point2D;
import java.util.ArrayList;


// TODO add matrix inversion, determinant
/**
 * @author Paul Hertz
 */
/**
 * Maintains a 3 x 3 matrix for accumulating affine transforms (translate, scale, rotate, shear).
 * The matrix is used as a CTM or current transformation matrix for 2D graphics state.
 * Also provides static utility methods for operations with 3 x 3 matrices.
 * Parts of Matrix3 are adapted from the 2d and 3d Vector C Library by Andrew Glassner 
 * from "Graphics Gems", Academic Press, 1990. See http://www.graphicsgems.org/.
 */
public class Matrix3 {
	/** a 3 x 3 matrix that accumulates transforms, aka the current transformation matrix or CTM */
	protected double 	element[][];
	/** x-component of most recent translation */
	protected double    gTX;
	/** y-component of most recent translation */
	protected double    gTY;
	/** x-component of most recent scale */
	protected double    gSX;
	/** y-component of most recent scale */
	protected double    gSY;
	/** angle in radians of most recent rotation */
	protected double    gANG;
	/** x-component of most recent shear */
	protected double    gShX;
	/** y-component of most recent shear */
	protected double    gShY;
	/** flag to indicate that matrix is normal, i.e., last column is 0 0 1 */
	protected boolean   gIsNormal = false;

	
	/**
	 * instantiates a Matrix3 from a 3 x 3 array of doubles 
	 * @param element   a 3 x 3 array of doubles
	 */
	public Matrix3(double element[][]) {
	    this.element = new double[3][3];
	    for (int i = 0; i < 3; i++) {
	      for (int j=0; j<3; j++) {
	        this.element[i][j] = element[i][j];
	      }
	    }
	}

	/**
	 * instantiates a Matrix3 from the element array of another matrix3 
	 * @param matx   a Matrix3
	 */
	public Matrix3(Matrix3 matx) {
		this(matx.getElements());
	}

	/**
	 * instantiates this Matrix3 as a unit matrix, i.e.:
	 * <pre>
	 *    1  0  0
	 *    0  1  0
	 *    0  0  1
	 * </pre>
	 */
	public Matrix3() {
		element = new double[3][3];
		init();
	}

	/**
	 * sets the internal matrix to the unit matrix:
	 * <pre>
	 *    1  0  0
	 *    0  1  0
	 *    0  0  1
	 * </pre>
	 */
	private void init() {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j=0; j<3; j++) {
				element[i][j] = (i == j) ? 1.0 : 0.0;
			}
		}
	}


	/**
	 * Multiplies together matrices a and b, returns the result in c.
	 * Note that c must not point to either of the input matrices.
	 * @param a   a Matrix3
	 * @param b   a Matrix3
	 * @param c   a Matrix that will contain result of multiplying a and b
	 * @return    a Matrix3 (c)
	 */
	public static Matrix3 matrixMultiply(Matrix3 a, Matrix3 b, Matrix3 c) {
		int i, j, k;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				c.element[i][j] = 0;
				for (k=0; k<3; k++) c.element[i][j] +=
					a.element[i][k] * b.element[k][j];
			}
		}
		return(c);
	}
	/**
	 * Multiplies together matrices a and b, returns the result in c.
	 * Note that c must not point to either of the input matrices.
	 * @param a   a 3 x 3 array of doubles
	 * @param b   a 3 x 3 array of doubles
	 * @param c   a 3 x 3 array of doubles that will contain result of multiplying a and b
	 * @return    a 3 x 3 array of doubles (c)
	 */
	public static double[][] matrixMultiply(double[][] a, double[][] b, double[][] c) {
		int i, j, k;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				c[i][j] = 0;
				for (k=0; k<3; k++) c[i][j] +=
					a[i][k] * b[k][j];
			}
		}
		return(c);
	}

	/**
	 * Transposes matrix a, returns the result in matrix b.
	 * @param a   a Matrix3
	 * @param b   a Matrix3 that receives the transpose of a
	 * @return    a Matrix3 (b)
	 */
	public static Matrix3 transposeMatrix3(Matrix3 a, Matrix3 b) {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++)
				b.element[i][j] = a.element[j][i];
		}
		return(b);
	}
	/**
	 * Transposes matrix a, returns the result in matrix b.
	 * @param a   a 3 x 3 array of doubles
	 * @param b   a 3 x 3 array of doubles that receives the transpose of a 
	 * @return    a 3 x 3 array of doubles
	 */
	public static double[][] transposeMatrix3(double[][] a, double[][] b) {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++)
				b[i][j] = a[j][i];
		}
		return(b);
	}


	/* ------------------------------------------------------------------------ */
	/*                                                                          */
	/*          LOW LEVEL ROUTINES for matrix as CTM                            */
	/*                                                                          */
	/* ------------------------------------------------------------------------ */
	

	/**
	 * Low level method to set gSX and gSY, used internally.
	 * Call {@link #scaleCTM(double, double) <code>scaleCTM</code>} to set CTM and gSX, gSY.
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 */
	public void setScale(double xScale, double yScale) {
		gSX = xScale;
		gSY = yScale;
	}

	/**
	 * Low level method to set gTX and gTY, used internally. 
	 * Call {@link #translateCTM(double, double) <code>translateCTM</code>} to set CTM and gTX, gTY.
	 * @param xTrans   translation along x-axis
	 * @param yTrans   translatiion along y-axis
	 */
	public void setTranslation(double xTrans, double yTrans) {
		gTX = xTrans;
		gTY = yTrans;
	}

	/**
	 * Low level method to set gAng, used internally. 
	 * Call {@link #rotateCTM(double) <code>rotateCTM</code>} to set CTM and gAng.
	 * @param theta   angle to rotate, in radians
	 */
	public void setRotation(double theta) {
		gANG = theta;
	}

	/**
	 * Low level method to set gShX and gShY, used internally. 
	 * Call {@link #shearCTM(double, double) <code>shearCTM</code>} to set CTM and gShX, gShY.
	 * @param xShear
	 * @param yShear
	 */
	public void setShear(double xShear, double yShear) {
		gShX = xShear;
		gShY = yShear;
	}

	/**
	 * Low level method to concatenate scaling operations gSX and gSY into internal matrix.
	 * Generally, you should call {@link #translateCTM(double, double) <code>translateCTM</code>} instead.
	 */
	public void multiplyInScale() {
		int i;
		/* column 3 of the CTM remains unchanged */
		for (i=0; i<3; i++) {
			this.element[i][0] *= gSX;
			this.element[i][1] *= gSY;
		}
	} 

	/**
	 * Low level method to concatenate translation operations gTX and gTY into internal matrix.
	 * Generally, you should call {@link #translateCTM(double, double) <code>translateCTM</code>} instead.
	 */
	public void multiplyInTranslation() {
		int i;
		/* column 3 of the CTM remains unchanged */
		if (gIsNormal) {
			this.element[2][0] += gTX;
			this.element[2][1] += gTY;
		}
		else {
			for (i=0; i<3; i++) {
				this.element[i][0] += (gTX * this.element[i][2]);
				this.element[i][1] += (gTY * this.element[i][2]);
			}
		}
	} 

	/**
	 * Low level method to concatenate rotation operation gAng into internal matrix.
	 * Generally, you should call {@link #rotateCTM(double) rotateCTM} instead.
	 */
	public void multiplyInRotation() {
		double  cs, sn, temp;
		int   i;
		/* column 3 of the CTM remains unchanged */
		cs = Math.cos(gANG);
		sn = Math.sin(gANG);
		for (i=0; i<3; i++) {
			temp = (this.element[i][0] * cs - this.element[i][1] * sn);
			this.element[i][1] = (this.element[i][0] * sn + this.element[i][1] * cs);
			this.element[i][0] = temp;
		}
	}


	/**
	 * Low level method to concatenate shearing operations gShX and gShY into internal matrix.
	 * Generally, you should call {@link #shearCTM(double, double) <code>shearCTM</code>} instead.
	 */
	public void multiplyInShear() {
		if (gIsNormal) {
			this.element[0][0] = this.element[0][0] + this.element[1][0] * gShX;
			this.element[0][1] = this.element[0][1] + this.element[1][1] * gShX;
			this.element[1][0] = this.element[0][0] * gShY + this.element[1][0];
			this.element[1][1] = this.element[0][1] * gShY + this.element[1][1];
		}
		else {
			this.element[0][0] = this.element[0][0] + this.element[1][0] * gShX;
			this.element[0][1] = this.element[0][1] + this.element[1][1] * gShX;
			this.element[0][2] = this.element[0][2] + this.element[1][2] * gShX;
			this.element[1][0] = this.element[0][0] * gShY + this.element[1][0];
			this.element[1][1] = this.element[0][1] * gShY + this.element[1][1];
			this.element[1][2] = this.element[0][2] * gShY + this.element[1][2];
		}
	}


	/* ------------------------------------------------------------------------ */
	/*                                                                          */
	/*          HIGH LEVEL ROUTINES for our matrix                              */
	/*                                                                          */
	/* ------------------------------------------------------------------------ */


	/**
	 * High level method to initialize internal matrix to unit matrix. 
	 * Sets gAng, gTX and gTY to 0.
	 * Sets gTX and gTY to 1.
	 */
	public void initMatxGlobals() {
		gANG = gTX = gTY = 0;
		gSX = gSY = 1;
		/* create a new initialized CTM */
		this.init();
	}

	/**
	 * Copies the internal matrix of newCTM to the internal matrix of this Matrix3.
	 * Values of gTX, gTY, gAng, gSX, gSY, gShX and gShY remain unchanged.
	 * @param newCTM   a Matrix3 
	 */
	public void setCTM (Matrix3 newCTM) {
		int i, j;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				this.element[i][j] = newCTM.element[i][j];
			}
		}
	}

	/**
	 * Copies the values in the 3x3 array elems to the internal matrix of this Matrix3.
	 * Values of gTX, gTY, gAng, gSX, gSY, gShX and gShY remain unchanged.
	 * @param elems   a 3 x 3 array of double
	 */
	public void setCTM (double[][] elems) {
		int i, j;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				this.element[i][j] = elems[i][j];
			}
		}
	}

	/**
	 * Sets the values of first two columns of matrix elems.
	 * Values of gTX, gTY, gAng, gSX, gSY, gShX and gShY remain unchanged.
	 * This 6 value format is typical of Adobe Illustrator, and similar to 
	 * Processing's 3x2 matrix structure PMatrix2D.
	 * @param a    value for element[0][0], scaling on x-axis
	 * @param b    value for element[0][1]
	 * @param c    value for element[1][0]
	 * @param d    value for element[1][1], scaling on y-axis
	 * @param tx   value for element[2][0], translation on x-axis
	 * @param ty   value for element[2][1], translation on y-axis
	 */
	public void setCTM (double a, double b, double c, double d, double tx, double ty) {
		this.element[0][0] = a;
		this.element[0][1] = b;
		this.element[1][0] = c;
		this.element[1][1] = d;
		this.element[2][0] = tx;
		this.element[2][1] = ty;
	}

	
	/**
	 * Returns a copy of the internal matrix of this Matrix3.
	 * @return a copy of the internal matrix of this Matrix3
	 */
	public double[][] getCTM() {
		return this.getElements();
	}

	/**
	 * Copies the internal matrix of this Matrix3 into Matrix3 mout.
	 * @param mout   a Matrix3, target of copy
	 */
	void copyCTM(Matrix3 mout) {
		int i, j;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				mout.element[i][j] = this.element[i][j];
			}
		}
	}
	/**
	 * Copies the internal matrix of this Matrix3 into 3x3 array mout.
	 * @param mout   a 3x3 array of double, target of copy
	 */
	void copyCTM(double[][] mout) {
		int i, j;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				mout[i][j] = this.element[i][j];
			}
		}
	}

	/**
	 * Sets the internal matrix of this CTM to the unit matrix.
	 * Values of gTX, gTY, gAng, gSX, gSY, gShX and gShY remain unchanged.
	 */
	public void initCTM() {
		int i, j;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
				this.element[i][j] = (i == j) ? 1.0 : 0.0;
			}
		}
	} 
	
	/**
	 * Returns a copy of the internal matrix of this Matrix3 as a 3x3 array.
	 * @return a copy of the internal matrix of this Matrix3
	 */
	public double[][] getElements() {
		double[][] elems = new double[3][3];
		int i, j;
		for (i=0; i<3; i++) {
			for (j=0; j<3; j++) {
			elems[i][j] = this.element[i][j];
			}
		}
		return elems;
	}

	/**
	 * Checks whether the internal matrix of this Matrix3 is normal, sets gIsNormal.
	 * @return true if the internal matrix of this Matrix3 is normal (column 3 is 0 0 1), false otherwise.
	 */
	public boolean isNormalCTM() {
		if ((this.element[0][2] == 0.0) 
				&& (this.element[1][2] == 0.0) 
				&& (this.element[2][2] == 1.0)) {
			gIsNormal = true;
		}
		else {
			gIsNormal = false;
		}
		return(gIsNormal);
	}

	/**
	 * High level method to concatenate translation in x and y into the internal matrix of this Matrix3.
	 * Sets the values of gTX and gTY. 
	 * @param xTrans   translation on the x-axis
	 * @param yTrans   translation on the y-axis
	 */
	public void translateCTM(double xTrans, double yTrans) {
		setTranslation(xTrans, yTrans);
		multiplyInTranslation();
	}

	/**
	 * High level method to concatenate scaling in x and y into the internal matrix of this Matrix3.
	 * Sets the values of gSX and gSY. 
	 * @param xScale   scaling on the x-axis
	 * @param yScale   scaling on the y-axis
	 */
	public void scaleCTM(double xScale, double yScale) {
		setScale(xScale, yScale);
		multiplyInScale();
	}

	/**
	 * High level method to concatenate rotation by angle radians into the internal matrix of this Matrix3.
	 * Sets the value of gAng. 
	 * @param angle   the angle to rotate
	 */
	public void rotateCTM (double angle) {
		setRotation(angle);
		multiplyInRotation();
	}

	/**
	 * High-level method to concatenate a translation followed by a reflection on the x- or y-axis 
	 * into the internal matrix of this Matrix3.
	 * Changes values of gSX, gSY, gTX and gTY.
	 * @param reflectHorizontal   true if reflection in on x-axis, false if reflection in on y-axis
	 * @param xTrans   displacement on x-axis applied prior to reflection
	 * @param yTrans   displacement on y-axis applied prior to reflection
	 */
	public void reflectCTM (boolean reflectHorizontal, double xTrans, double yTrans) {
		this.translateCTM(xTrans, yTrans);
		if (reflectHorizontal) {
			scaleCTM(1.0, -1.0);
		}
		else {
			scaleCTM(-1.0, 1.0);
		}
	}
	/**
	 * Concatenates a reflection on the x- or y-axis into the internal matrix of this Matrix3.
	 * Changes values of gSX and gSY.
	 * @param reflectHorizontal   true if reflection is on x-axis, false if reflection is on y-axis
	 */
	public void reflectCTM (boolean reflectHorizontal) {
		if (reflectHorizontal) {
			scaleCTM(1.0, -1.0);
		}
		else {
			scaleCTM(-1.0, 1.0);
		}
	}

	/**
	 * Concatenates a shear on the x and y axes into the internal matrix of this Matrix3.
	 * Sets gShX and gShY to xShear and yShear.
	 * @param xShear   shearing on x-axis
	 * @param yShear   shearing on y-axis
	 */
	public void shearCTM (double xShear, double yShear ) {
		setShear(xShear, yShear);
		multiplyInShear(); 
	}
	/*
    void shearCTM (boolean shearHorizontal, double angle ) {
      // not implemented yet
    }
	 */


	 /* ------------------------------------------------------------------------ */
	 /*                                                                          */
	 /*       matrix multiplication operation on points for this matrix          */
	 /*                                                                          */
	 /* ------------------------------------------------------------------------ */

	
	/**
	 * Multiplies point pin by the internal matrix of this Matrix3, returns result in pout. 
	 * It is the user's responsibility to ensure the the internal matrix is normal.
	 * @param pin    point to transform by current transformation matrix
	 * @param pout   stores value of point that results from transform
	 * @return       the transformed point
	 */
	public Point2D.Double multiplyPointByNormalCTM(Point2D.Double pin, Point2D.Double pout) {
		 pout.x = (pin.x * this.element[0][0]) + 
		 (pin.y * this.element[1][0]) + this.element[2][0];
		 pout.y = (pin.x * this.element[0][1]) + 
		 (pin.y * this.element[1][1]) + this.element[2][1];
		 return(pout);
	 }
	 /**
	 * Multiplies point (x, y) by the internal matrix of this Matrix3, returns result in pout. 
	 * It is the user's responsibility to ensure the the internal matrix is normal.
	 * @param x      x-coordinate of point
	 * @param y      y-coordinate of point
	 * @param pout   stores value of point that results from transform
	 * @return       the transformed point
	 */
	public Point2D.Double multiplyPointByNormalCTM(double x, double y, Point2D.Double pout) {
		 pout.x = (x * this.element[0][0]) + 
		 (y * this.element[1][0]) + this.element[2][0];
		 pout.y = (x * this.element[0][1]) + 
		 (y * this.element[1][1]) + this.element[2][1];
		 return(pout);
	 }


	/**
	 * Multiplies point pin by the internal matrix of this Matrix3, returns result in pout. 
	 * Column 3 of internal matrix will affect results if it is not normal (0 0 1).
	 * @param pin    point to transform by current transformation matrix
	 * @param pout   stores value of point that results from transform
	 * @return       the transformed point
	 */
	 public Point2D.Double multiplyPointByProjCTM (Point2D.Double pin, Point2D.Double pout) {
		 double w;
		 pout.x = (pin.x * this.element[0][0]) + 
		 (pin.y * this.element[1][0]) + this.element[2][0];
		 pout.y = (pin.x * this.element[0][1]) + 
		 (pin.y * this.element[1][1]) + this.element[2][1];
		 w = (pin.x * this.element[0][2]) + 
		 (pin.y * this.element[1][2]) + this.element[2][2];
		 if (w != 0.0) { pout.x /= w;  pout.y /= w; }
		 return(pout);
	 }
	/**
	 * Multiplies point (x, y) by the internal matrix of this Matrix3, returns result in pout. 
	 * Column 3 of internal matrix will affect results if it is not normal (0 0 1).
	 * @param x      x-coordinate of point
	 * @param y      y-coordinate of point
	 * @param pout   stores value of point that results from transform
	 * @return       the transformed point
	 */
	 public Point2D.Double multiplyPointByProjCTM (double x, double y, Point2D.Double pout) {
		 double w;
		 pout.x = (x * this.element[0][0]) + 
		 (y * this.element[1][0]) + this.element[2][0];
		 pout.y = (x * this.element[0][1]) + 
		 (y * this.element[1][1]) + this.element[2][1];
		 w = (x * this.element[0][2]) + 
		 (y * this.element[1][2]) + this.element[2][2];
		 if (w != 0.0) { pout.x /= w;  pout.y /= w; }
		 return(pout);
	 }


	/**
	 * Transforms a list of points by current transformation matrix. User does not need to
	 * determine if the CTM is normal, the methods checks and performs the appropriate transform. 
	 * @param pts   an ArrayList of points
	 * @return the list with its points transformed
	 */
	public ArrayList<Point2D.Double> transformList(ArrayList<Point2D.Double> pts) {
		 Point2D.Double currentPt = new Point2D.Double();
		 if (this.isNormalCTM()) {
			 for (Point2D.Double pt : pts) {
				 pt = multiplyPointByNormalCTM(pt, currentPt);
			 }
		 }
		 else {
			 for (Point2D.Double pt : pts) {
				 pt = multiplyPointByProjCTM(pt, currentPt);
			 }
		 }
		 return pts;
	 }



	 /* ------------------------------------------------------------------------ */
	 /*                                                                          */
	 /*          Affine transforms and utility methods for matrices              */
	 /*                                                                          */
	 /* ------------------------------------------------------------------------ */

	/**
	 * Copies one matrix to another matrix. Matrices are represented as Matrix3.
	 * @param min    the source matrix
	 * @param mout   the target matrix
	 * @return       the copied matrix (mout)
	 */
	public static Matrix3 copyMatrix3 (Matrix3 min, Matrix3 mout) {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				mout.element[i][j] = min.element[i][j];
			}
		}
		return(mout);
	}
	/**
	 * Copies one matrix to another matrix. Matrices are represented as 3x3 arrays.
	 * @param min    the source matrix, 3x3 array of double
	 * @param mout   the target matrix, 3x3 array of double
	 * @return       the copied matrix, 3x3 array of double (mout)
	 */
	public static double[][] copyMatrix3 (double[][] min, double[][] mout) {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				mout[i][j] = min[i][j];
			}
		}
		return(mout);
	}


	/**
	 * Initializes a matrix (Matrix3) to the unit matrix.
	 * @param anyM3   the matrix to be initialized
	 * @return the initialized matrix
	 */
	public static Matrix3 initMatrix3(Matrix3 anyM3) {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				anyM3.element[i][j] = (i == j) ? 1.0 : 0.0;
			}
		}
		return(anyM3);
	}
	/**
	 * Initializes a matrix (3x3 array) to the unit matrix.
	 * @param anyM3   the matrix to be initialized, a 3x3 array of double
	 * @return the initialized matrix, a 3x3 array of double
	 */
	public static double[][] initMatrix3(double[][] anyM3) {
		int i, j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				anyM3[i][j] = (i == j) ? 1.0 : 0.0;
			}
		}
		return(anyM3);
	}


	/**
	 * Checks whether a matrix is normal (column 3 is 0 0 1).
	 * @param anyM3   a Matrix3
	 * @return true if matrix is normal, false otherwise.
	 */
	public static boolean isNormalMatrix3(Matrix3 anyM3) {
		boolean isNormal;
		if ((anyM3.element[0][2] == 0.0)
				&& (anyM3.element[1][2] == 0.0)
				&& (anyM3.element[2][2] == 1.0)) {
			isNormal = true;
		}
		else {
			isNormal = false;
		}
		return(isNormal);
	}
	/**
	 * Checks whether a matrix is normal (column 3 is 0 0 1).
	 * @param anyM3   a 3x3 array of double
	 * @return true if matrix is normal, false otherwise.
	 */
	public static boolean isNormalMatrix3(double[][] anyM3) {
		boolean isNormal;
		if ((anyM3[0][2] == 0.0)
				&& (anyM3[1][2] == 0.0)
				&& (anyM3[2][2] == 1.0)) {
			isNormal = true;
		}
		else {
			isNormal = false;
		}
		return(isNormal);
	}


	/**
	 * Concatenates translations on x and y axis into a matrix. Does not set globals gTX, gTY.
	 * @param xTrans   translation on x-axis
	 * @param yTrans   translation on y-axis
	 * @param anyM3    matrix into which translations are concatenated
	 * @return         the transformed matrix
	 */
	public static Matrix3 translateMatrix3(double xTrans, double yTrans, Matrix3 anyM3) {
		int i;
		/* column 3 of the matrix remains unchanged */
		if (isNormalMatrix3(anyM3)) {
			anyM3.element[2][0] += xTrans;
			anyM3.element[2][1] += yTrans;
		}
		else {
			for (i=0; i<3; i++) {
				anyM3.element[i][0] += (xTrans * anyM3.element[i][2]);
				anyM3.element[i][1] += (yTrans * anyM3.element[i][2]);
			}
		}
		return(anyM3);
	}
	/**
	 * Concatenates translations on x and y axis into a matrix.  Does not set globals gTX, gTY.
	 * @param xTrans   translation on x-axis
	 * @param yTrans   translation on y-axis
	 * @param anyM3    matrix into which translations are concatenated, a 3x3 array of double
	 * @return         the transformed matrix, a 3x3 array of double
	 */
	public static double[][] translateMatrix3(double xTrans, double yTrans, double[][] anyM3) {
		int i;
		/* column 3 of the matrix remains unchanged */
		if (isNormalMatrix3(anyM3)) {
			anyM3[2][0] += xTrans;
			anyM3[2][1] += yTrans;
		}
		else {
			for (i=0; i<3; i++) {
				anyM3[i][0] += (xTrans * anyM3[i][2]);
				anyM3[i][1] += (yTrans * anyM3[i][2]);
			}
		}
		return(anyM3);
	}


	/**
	 * Concatenates scaling on x and y axis into a matrix. Does not set globals gSX and gSY.
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 * @param anyM3    matrix into which scalings are concatenated
	 * @return         the transformed matrix
	 */
	public static Matrix3 scaleMatrix3(double xScale, double yScale, Matrix3 anyM3) {
		int i;
		/* column 3 of the matrix remains unchanged */
		for (i=0; i<3; i++) {
			anyM3.element[i][0] *= xScale;
			anyM3.element[i][1] *= yScale;
		}
		return(anyM3);
	}
	/**
	 * Concatenates scaling on x and y axis into a matrix. Does not set globals gSX and gSY.
	 * @param xScale   scaling on x-axis
	 * @param yScale   scaling on y-axis
	 * @param anyM3    matrix into which scalings are concatenated, a 3x3 arry of double
	 * @return         the transformed matrix, a 3x3 arry of double
	 */
	public static double[][] scaleMatrix3(double xScale, double yScale, double[][] anyM3) {
		int i;
		/* column 3 of the matrix remains unchanged */
		for (i=0; i<3; i++) {
			anyM3[i][0] *= xScale;
			anyM3[i][1] *= yScale;
		}
		return(anyM3);
	}

	
	/**
	 * Concatenates a rotation into a matrix. Does not set global gAng.
	 * @param angle   angle of rotation in radians
	 * @param anyM3   the matrix to transform
	 * @return        the transformed matrix
	 */
	public static Matrix3 rotateMatrix3(double angle, Matrix3 anyM3) {
		double  cs, sn, temp;
		int   i;
		/* column 3 of the CTM remains unchanged */
		cs = Math.cos(angle);
		sn = Math.sin(angle);
		for (i = 0; i < 3; i++) {
			temp = (anyM3.element[i][0] * cs - anyM3.element[i][1] * sn);
			anyM3.element[i][1] = (anyM3.element[i][0] * sn + anyM3.element[i][1] * cs);
			anyM3.element[i][0] = temp;
		}
		return(anyM3);
	}
	/**
	 * Concatenates a rotation into a matrix. Does not set global gAng.
	 * @param angle   angle of rotation in radians
	 * @param anyM3   the matrix to transform, a 3x3 array of double
	 * @return        the transformed matrix, a 3x3 array of double
	 */
	public static double[][] rotateMatrix3(double angle, double[][] anyM3) {
		double  cs, sn, temp;
		int   i;
		/* column 3 of the CTM remains unchanged */
		cs = Math.cos(angle);
		sn = Math.sin(angle);
		for (i = 0; i < 3; i++) {
			temp = (anyM3[i][0] * cs - anyM3[i][1] * sn);
			anyM3[i][1] = (anyM3[i][0] * sn + anyM3[i][1] * cs);
			anyM3[i][0] = temp;
		}
		return(anyM3);
	}


	/**
	 * Reflects a matrix about the horizontal or vertical axis. Does not set globals gSX and gSY.
	 * @param reflectHorizontal   true if reflection is around the x-axis, false if it's around the y-axis
	 * @param anyM3    a matrix encapsulated as a Matrix3.
	 * @return         the resulting matrix
	 */
	public static Matrix3 reflectMatrix3(boolean reflectHorizontal, Matrix3 anyM3 ) {
		if (reflectHorizontal) {
			scaleMatrix3(1.0, -1.0, anyM3);
		}
		else {
			scaleMatrix3(-1.0, 1.0, anyM3);
		}
		return(anyM3);
	}
	/**
	 * Reflects a matrix about the horizontal or vertical axis. Does not set globals gSX and gSY.
	 * @param reflectHorizontal   true if reflection is around the x-axis, false if it's around the y-axis
	 * @param anyM3    a 3x3 array of double
	 * @return         a 3x3 array of double (the supplied matrix)
	 */
	public static double[][] reflectMatrix3(boolean reflectHorizontal, double[][] anyM3 ) {
		if (reflectHorizontal) {
			scaleMatrix3(1.0, -1.0, anyM3);
		}
		else {
			scaleMatrix3(-1.0, 1.0, anyM3);
		}
		return(anyM3);
	}


	/**
	 * Shears a matrix. Does not set the globals gShX and gShY.
	 * @param shX     x-axis shearing
	 * @param shY     y-axis shearing
	 * @param anyM3   a Matrix3 to which the shearing operation will be concatenated. 
	 * @return        the sheared matrix
	 */
	public static Matrix3 shearMatrix3(double shX, double shY, Matrix3 anyM3) {
		 if (isNormalMatrix3(anyM3)) {
			 anyM3.element[0][0] = anyM3.element[0][0] + anyM3.element[1][0] * shX;
			 anyM3.element[0][1] = anyM3.element[0][1] + anyM3.element[1][1] * shX;
			 anyM3.element[1][0] = anyM3.element[0][0] * shY + anyM3.element[1][0];
			 anyM3.element[1][1] = anyM3.element[0][1] * shY + anyM3.element[1][1];
		 }
		 else {
			 anyM3.element[0][0] = anyM3.element[0][0] + anyM3.element[1][0] * shX;
			 anyM3.element[0][1] = anyM3.element[0][1] + anyM3.element[1][1] * shX;
			 anyM3.element[0][2] = anyM3.element[0][2] + anyM3.element[1][2] * shX;
			 anyM3.element[1][0] = anyM3.element[0][0] * shY + anyM3.element[1][0];
			 anyM3.element[1][1] = anyM3.element[0][1] * shY + anyM3.element[1][1];
			 anyM3.element[1][2] = anyM3.element[0][2] * shY + anyM3.element[1][2];
		 }
		 return anyM3;
	 }
	 /**
	 * Shears a matrix. Does not set the globals gShX and gShY.
	 * @param shX     x-axis shearing
	 * @param shY     y-axis shearing
	 * @param anyM3   a 3x3 array to which the shearing operation will be concatenated. 
	 * @return        the sheared matrix
	 */
	public static double[][] shearMatrix3(double shX, double shY, double[][] anyM3) {
		 if (isNormalMatrix3(anyM3)) {
			 anyM3[0][0] = anyM3[0][0] + anyM3[1][0] * shX;
			 anyM3[0][1] = anyM3[0][1] + anyM3[1][1] * shX;
			 anyM3[1][0] = anyM3[0][0] * shY + anyM3[1][0];
			 anyM3[1][1] = anyM3[0][1] * shY + anyM3[1][1];
		 }
		 else {
			 anyM3[0][0] = anyM3[0][0] + anyM3[1][0] * shX;
			 anyM3[0][1] = anyM3[0][1] + anyM3[1][1] * shX;
			 anyM3[0][2] = anyM3[0][2] + anyM3[1][2] * shX;
			 anyM3[1][0] = anyM3[0][0] * shY + anyM3[1][0];
			 anyM3[1][1] = anyM3[0][1] * shY + anyM3[1][1];
			 anyM3[1][2] = anyM3[0][2] * shY + anyM3[1][2];
		 }
		 return anyM3;
	 }
	 /*
  public static Matrix3 shearMatrix3(boolean shearHorizontal, double angle, Matrix3 anyM3) {
    // NOT YET IMPLEMENTED 
    return null;
  }
	  */


	 /* ---- matrix multiplication routines for any Matrix3 ---- */


	 /**
	  * Multiplies a point by a normal matrix.
	  * @param pin     a point to transform
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByNormalMatrix3( Point2D.Double pin, Point2D.Double pout, Matrix3 anyM3) {
		 pout.x = (pin.x * anyM3.element[0][0]) + 
		 (pin.y * anyM3.element[1][0]) + anyM3.element[2][0];
		 pout.y = (pin.x * anyM3.element[0][1]) + 
		 (pin.y * anyM3.element[1][1]) + anyM3.element[2][1];
		 return(pout);
	 }
	 /**
	  * Multiplies a point by a normal matrix.
	  * @param x       x-coordinate of point
	  * @param y       y-coordinate of point
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByNormalMatrix3( double x, double y, Point2D.Double pout, Matrix3 anyM3) {
		 pout.x = (x * anyM3.element[0][0]) + 
		 (y * anyM3.element[1][0]) + anyM3.element[2][0];
		 pout.y = (x * anyM3.element[0][1]) + 
		 (y * anyM3.element[1][1]) + anyM3.element[2][1];
		 return(pout);
	 }
	 /**
	  * Multiplies a point by a normal matrix.
	  * @param pin     a point to transform
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform, a 3x3 array of double
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByNormalMatrix3( Point2D.Double pin, Point2D.Double pout, double[][] anyM3) {
		 pout.x = (pin.x * anyM3[0][0]) + 
		 (pin.y * anyM3[1][0]) + anyM3[2][0];
		 pout.y = (pin.x * anyM3[0][1]) + 
		 (pin.y * anyM3[1][1]) + anyM3[2][1];
		 return(pout);
	 }
	 /**
	  * Multiplies a point by a normal matrix.
	  * @param x       x-coordinate of point
	  * @param y       y-coordinate of point
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform, a 3x3 array of double
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByNormalMatrix3( double x, double y, Point2D.Double pout, double[][] anyM3) {
		 pout.x = (x * anyM3[0][0]) + 
		 (y * anyM3[1][0]) + anyM3[2][0];
		 pout.y = (x * anyM3[0][1]) + 
		 (y * anyM3[1][1]) + anyM3[2][1];
		 return(pout);
	 }


	 /**
	  * Multiplies a point by a projective matrix.
	  * @param pin     a point to transform
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByProjMatrix3(Point2D.Double pin, Point2D.Double pout, Matrix3 anyM3) {
		 double w;
		 pout.x = (pin.x * anyM3.element[0][0]) + 
		 (pin.y * anyM3.element[1][0]) + anyM3.element[2][0];
		 pout.y = (pin.x * anyM3.element[0][1]) + 
		 (pin.y * anyM3.element[1][1]) + anyM3.element[2][1];
		 w = (pin.x * anyM3.element[0][2]) + 
		 (pin.y * anyM3.element[1][2]) + anyM3.element[2][2];
		 if (w != 0.0) { pout.x /= w;  pout.y /= w; }
		 return(pout);
	 }
	 /**
	  * Multiplies a point by a projective matrix.
	  * @param x       x-coordinate of point
	  * @param y       y-coordinate of point
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByProjMatrix3(double x, double y, Point2D.Double pout, Matrix3 anyM3) {
		 double w;
		 pout.x = (x * anyM3.element[0][0]) + 
		 (y * anyM3.element[1][0]) + anyM3.element[2][0];
		 pout.y = (x * anyM3.element[0][1]) + 
		 (y * anyM3.element[1][1]) + anyM3.element[2][1];
		 w = (x * anyM3.element[0][2]) + 
		 (y * anyM3.element[1][2]) + anyM3.element[2][2];
		 if (w != 0.0) { pout.x /= w;  pout.y /= w; }
		 return(pout);
	 }
	 /**
	  * Multiplies a point by a projective matrix.
	  * @param pin     a point to transform
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform, a 3x3 array of double
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByProjMatrix3(Point2D.Double pin, Point2D.Double pout, double[][] anyM3) {
		 double w;
		 pout.x = (pin.x * anyM3[0][0]) + 
		 (pin.y * anyM3[1][0]) + anyM3[2][0];
		 pout.y = (pin.x * anyM3[0][1]) + 
		 (pin.y * anyM3[1][1]) + anyM3[2][1];
		 w = (pin.x * anyM3[0][2]) + 
		 (pin.y * anyM3[1][2]) + anyM3[2][2];
		 if (w != 0.0) { pout.x /= w;  pout.y /= w; }
		 return(pout);
	 }
	 /**
	  * Multiplies a point by a projective matrix.
	  * @param x       x-coordinate of point
	  * @param y       y-coordinate of point
	  * @param pout    the transformed point
	  * @param anyM3   the matrix performing the transform, a 3x3 array of double
	  * @return        the transformed point
	  */
	 public static Point2D.Double multiplyPointByProjMatrix3(double x, double y, Point2D.Double pout, double[][] anyM3) {
		 double w;
		 pout.x = (x * anyM3[0][0]) + 
		 (y * anyM3[1][0]) + anyM3[2][0];
		 pout.y = (x * anyM3[0][1]) + 
		 (y * anyM3[1][1]) + anyM3[2][1];
		 w = (x * anyM3[0][2]) + 
		 (y * anyM3[1][2]) + anyM3[2][2];
		 if (w != 0.0) { pout.x /= w;  pout.y /= w; }
		 return(pout);
	 }


}
