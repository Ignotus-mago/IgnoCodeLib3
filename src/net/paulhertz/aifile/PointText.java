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

import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import net.paulhertz.geom.Matrix3;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 * @author paulhz
 */
/** 
 * Implements an Adobe Illustrator 7.0 Point Text object with a subset of the available tags.
 * Provides methods to set the fill, stroke, and stroke weight of the text, although these 
 * are part of the graphics state and not part of the text object itself. Be aware that they will remain
 * in effect in the file until they are changed.
 * 
 */
public class PointText extends DisplayComponent {
	// TODO include overridden methods for a terminal node
	protected PFont font;
	/** transformation matrix in compact form, default {1, 0, 0, 1, 0, 0} */
	protected double[] matrix = {1, 0, 0, 1, 0, 0};
	/** x translation, default 0 */
	protected double tx;
	/** y translation, default 0 */
	protected double ty;
	/** origin point on text path, default 0 */
	protected double startPt = 0;
	/** the text to display */
	protected String text;
	/** text object type, default 0, other types unsupported as yet */
	public final int objectType = 0;
	/** render mode, default 0 (filled) */
	protected int render = 0;
	/** font name, as used by Adobe Illustrator */
	protected String fontname;
	/** the Processing font name */
	protected String pFontname;
	/** font size, default 12 */
	protected double size = 12;
	/** leading between lines */
	protected double leading = size * 1.25;
	/** leading between paragraphs */
	protected double paragraphLeading = leading;
	/** text alignment, default 0 (left) */
	protected int alignment = 0;
	// attributes for fill, stroke and weight belong to the graphics state, 
	// they are not part of the text object itself
	/** flags if user called fillColor() or strokeColor() */
	protected boolean userSetFillOrStroke = false;
	/** flags if shape is filled or not */
	protected boolean hasFill = false;
	/** flags if shape has a stroke or not */
	protected boolean hasStroke = false;
	/** fill color for shape */
	protected int fillColor;
	/** stroke color for shape */
	protected int strokeColor;
	/** stroke weight for shape */
	/** optional transparency flag. Transparency is not part of the AI 7.0 spec, but we try to support it. */
	protected static boolean useTransparency = false;
	protected float weight = 1;
	private static DecimalFormat fourPlaces = AIFileWriter.fourPlaces;

	/*
	 * Here's an example of the markup in a version 3.0 file:
	 * <pre>
	 * 		u									group (optional)
	 * 		0 To								open text object (optional?)
	 * 		1 0 0 1 117.625 539.2188 0 Tp		text path: a b c d tx ty startPt
	 * 		TP									close text path
	 * 		1 0 0 1 117.625 539.2188 Tm			matrix (same values as text path, minus startPt, may be omitted)
	 * 		0 Tr								render mode
	 * 		/_Times-Italic 12 Tf				font name and size
	 * 		120 Tz								scale (omitted here)
	 * 		1 Ta								alignment
	 * 		0 Tt								tracking (omitted here)
	 * 		0 0 Tl								line and paragraph leading
	 * 		0 Tc								computed intercharacter spacing
	 * 		(bananas) Tx 1 0 Tk					text and kerning
	 * 		TO									close text object (optional?)
	 * 		U									close group (optional)
	 * </pre>
	 * 
	 */
	
	/**
	 * Creates a new PointText at (0,0).
	 */
	public PointText(PApplet parent) {
		this(parent, 0, 0, "");
	}
	/**
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * Creates a new PointText at (0,0).
	 */
	public PointText() {
		this(IgnoCodeLib.getMyParent(), 0, 0, "");
	}
	
	// Bottleneck constructor
	/**
	 * Creates a new PointText displaced (ty,ty) from the origin (0,0).
	 * @param parent PApplet used for calls to the Processing environment.
	 * @param tx     x translation
	 * @param ty     y translation
	 * @param text   the text
	 */
	public PointText(PApplet parent, double tx, double ty, String text) {
		this.parent = parent;
		this.setTx(tx);
		this.setTy(ty);
		this.setText(text);
	   	this.id = DisplayComponent.counter++;
	}
	/**
	 * Creates a new PointText displaced (ty,ty) from the origin (0,0).
     * PApplet used for calls to the Processing environment is obtained from 
     * {@link net.paulhertz.aifile.IgnoCodeLib IgnoCodeLib}, which must be correctly initialized in setup. 
     * If IgnoCodeLib does not have a reference to a PApplet, it throws a NullPointerException. 
	 * @param tx     x translation
	 * @param ty     y translation
	 * @param text   the text
	 */
	public PointText(double tx, double ty, String text) {
		this(IgnoCodeLib.getMyParent(), tx, ty, text);
	}
	
	/**
	 * @return the matrix, last two entries are tx and ty.
	 */
	public double[] matrix() {
		return matrix;
	}
	/**
	 * @param matrix the matrix to set, concatenated with CTM (current transformation matrix).
	 * It's generally safe to use the default values 1 0 0 1 tx ty.
	 * An exact explanation of what the matrix represents may be found in Adobe's Postscript Language
	 * Reference Manual, section 4.3.3 Matrix Representation and Manipulation (page 201). 
	 * Briefly, if a transformation matrix is represented as:
	 *     a  b  0
	 *     c  d  0
	 *     tx ty 1
	 * then the representation in the Adobe Illustrator file omits the third column, thus:
	 *     a  b  c  d  tx  ty
	 * The net.paulhertz.geom.Matrix3 class provides utility methods for concatenating transforms
	 * and can be used to derive useful values for the matrix.
	 */
	public void setMatrix(double[] matrix) {
		this.matrix = matrix;
		setTx(matrix[4]);
		setTy(matrix[5]);
	}
	/**
	 * Uses a Matrix3 to set the internal matrix used by Adobe Illustrator. Code shows exactly how 
	 * values in the supplied matrix correspond to the 6-element matrix used by Illustrator.
	 * @param matx   a Matrix3 that encapsulates a transform. Third column is ignored.
	 */
	public void setMatrix(Matrix3 matx) {
		double[][] elems = matx.getElements();
		double a = elems[0][0];
		double b = elems[0][1];
		double c = elems[1][0];
		double d = elems[1][1];
		double tx = elems[2][0];
		double ty = elems[2][1];
		double[] xform = new double[6];
		xform[0] = a;
		xform[1] = b;
		xform[2] = c;
		xform[3] = d;
		xform[4] = tx;
		xform[5] = ty;
		this.setMatrix(xform);
	}

	/**
	 * @return tx, translation along x-axis.
	 */
	public double tx() {
		return tx;
	}
	/**
	 * @param tx   translation along x-axis to set
	 */
	public void setTx(double tx) {
		this.tx = tx;
	}


	/**
	 * @return ty, translation along y-axis.
	 */
	public double ty() {
		return ty;
	}
	/**
	 * @param ty   the translation along y-axis to set
	 */
	public void setTy(double ty) {
		this.ty = ty;
	}


	/**
	 * @return the startPt
	 */
	public double startPt() {
		return startPt;
	}
	/**
	 * @param startPt    the startPt to set, only meaningful for text on a path, where it indicates a position on the path.
	 */
	public void setStartPt(double startPt) {
		this.startPt = startPt;
	}


	/**
	 * @return the text
	 */
	public String text() {
		return text;
	}
	/**
	 * @param text    the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * @return the render
	 */
	public int render() {
		return render;
	}
	/**
	 * Sets the render mode of text, generally safe to stick with the default 0, or with 1 or 2. 
	 * @param render the render to set
	 * 0 Ñ filled text
	 * 1 Ñ stroked text
	 * 2 Ñ filled and stroked text
	 * 3 Ñ invisible text
	 * 4 Ñ masked and filled text
	 * 5 Ñ masked and stroked text
	 * 6 Ñ masked, filled, and stroked text
	 * 7 Ñ masked (only) text
	 * 8 Ñ filled text followed by render mode 9 (pattern prototype only)
	 * 9 Ñ stroked text (preceded by render mode 8 text, pattern prototype only)
	 */
	public void setRender(int render) {
		this.render = render;
	}


	/**
	 * @return the fontname. 
	 */
	public String fontname() {
		return fontname;
	}
	/**
	 * @param fontname    the fontname to set. If no font is supplied, Illustrator uses its default font. 
	 * You may need to determine AI's naming conventions by inspecting a legacy file format in a text editor. 
	 * Typical names include:
	 * MyriadPro-Regular, Helvetica, Times-Roman, Times-Bold, Times-Italic, Times-BoldItalic, Tekton-Bold, etc.
	 * These will be bracketed by the appropriate tags when written to file. 
	 */
	public void setFontname(String fontname) {
		this.fontname = fontname;
	}


	/**
	 * @return the pFontname, name of the font used by Processing. 
	 */
	public String pFontname() {
		if (null == pFontname) {
			return "ERROR: no font has been initialized";
		}
		return this.pFontname;
	}
	/**
	 * @param pFontname    the Processing font to use. See http://processing.org/reference/text_.html 
	 */
	public void setPFontname(String pFontname) {
		this.pFontname = pFontname;
	}
	
	/**
	 * @param name   the name of the .vlw file to use
	 */
	public void loadFont(String name) {
		this.setPFontname(name);
		this.font = parent.loadFont(this.pFontname);		
	}

	/**
	 * @return the font
	 */
	public PFont getFont() {
		return font;
	}

	/**
	 * Sets the Processing font to the supplied PFont. This is the preferred method for setting the
	 * Processing font, with the least chance of errors. 
	 * @param font the font to set
	 */
	public void setFont(PFont font) {
		this.font = font;
		this.setPFontname(font.getName());
	}

	/**
	 * @return the size in points
	 */
	public double size() {
		return size;
	}
	/**
	 * @param size the size to set, in points
	 */
	public void setSize(double size) {
		this.size = size;
	}


	/**
	 * @return the leading between lines, in points (default 0 = auto)
	 */
	public double leading() {
		return leading;
	}
	/**
	 * @param leading   the leading between lines, in points
	 */
	public void setLeading(double leading) {
		this.leading = leading;
	}
	
	
	/**
	 * @param fontname    name of the font used in Illustrator
	 * @param pFontname   name of the font used in Processing
	 * @param weight      font weight (size in points)
	 * @param leading     leading between lines, commonly 1.25 * weight
	 */
	public void setFont(String fontname, String pFontname, double weight, double leading) {
		this.setFontname(fontname);
		this.setPFontname(pFontname);
		this.setSize(weight);
		this.setLeading(leading);
	}


	/**
	 * @return the leading between paragraphs, in points
	 */
	public double paragraphLeading() {
		return paragraphLeading;
	}
	/**
	 * @param paragraphLeading   the leading between paragraphs, in points (default 0 = auto)
	 */
	public void setParagraphLeading(double paragraphLeading) {
		this.paragraphLeading = paragraphLeading;
	}


	/**
	 * @return the alignment
	 */
	public int alignment() {
		return alignment;
	}
	/**
	 * @param alignment   the alignment to set
	 * 0Ñleft aligned
	 * 1Ñcenter aligned
	 * 2Ñright aligned
	 * 3Ñjustified (right and left)
	 * 4Ñjustified including last line
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}


	/**
	 * @return the objectType
	 */
	public int getObjectType() {
		return objectType;
	}
	
	
	private boolean hasFill() {
		return hasFill;
	}
	private void setHasFill(boolean newHasFill) {
		hasFill = newHasFill;
	}


	private boolean hasStroke() {
		return hasStroke;
	}
	private void setHasStroke(boolean newHasStroke) {
		hasStroke = newHasStroke;
	}


	public int fillColor() {
		return fillColor;
	}
	public void setFillColor(int newFillColor) {
		userSetFillOrStroke = true;
		fillColor = newFillColor;
		setHasFill(true);
	}
	public void setFillColor(RGBColor newFillColor) {
		userSetFillOrStroke = true;
		fillColor = Palette.rgb2int(newFillColor);
		setHasFill(true);
	}
	public void setNoFill() {
		userSetFillOrStroke = true;
		setHasFill(false);
	}


	public int strokeColor() {
		return this.strokeColor;
	}
	public void setStrokeColor(int newStrokeColor) {
		userSetFillOrStroke = true;
		strokeColor = newStrokeColor;
		setHasStroke(true);
	}
	public void setStrokeColor(RGBColor newStrokeColor) {
		userSetFillOrStroke = true;
		strokeColor = Palette.rgb2int(newStrokeColor);
		setHasStroke(true);
	}
	public void setNoStroke() {
		userSetFillOrStroke = true;
		setHasStroke(false);
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

	public boolean useTransparency() {
		return PointText.useTransparency;
	}
	public void setUseTransparency(boolean useTransparency) {
		PointText.useTransparency = useTransparency;
	}


	public float weight() {
		return weight;
	}
	public void setWeight(float newWeight) {
		weight = newWeight;
	}

	
	private String rgbFillString(int argb) {
		int colors[] = Palette.argbComponents(argb);
		/*
		boolean transparencySet = false;
		double trans = colors[0]/255.0;
		if (colors[0] < 255 && PointText.useTransparency) {
			// "0 " + fourPlaces.format(trans) + " 0 0 0 Xy"
			transparencySet = true;
		}
		*/
		String rs = fourPlaces.format(colors[1]/255.0);
		String gs = fourPlaces.format(colors[2]/255.0);
		String bs = fourPlaces.format(colors[3]/255.0);
		return rs +" "+ gs +" "+ bs +" Xa";
	}
	private String rgbStrokeString(int argb) {
		int colors[] = Palette.argbComponents(argb);
		/*
		boolean transparencySet = false;
		double trans = colors[0]/255.0;
		if (colors[0] < 255 && PointText.useTransparency) {
			// "0 " + fourPlaces.format(trans) + " 0 0 0 Xy"
			transparencySet = true;
		}
		*/
		String rs = fourPlaces.format(colors[1]/255.0);
		String gs = fourPlaces.format(colors[2]/255.0);
		String bs = fourPlaces.format(colors[3]/255.0);
		return rs +" "+ gs +" "+ bs +" XA";
	}

	
	public String outputString() {
		StringBuffer sb = new StringBuffer(1024);
		// render operator, set to fill
		if (userSetFillOrStroke) {
			if (this.hasFill()) {
				sb.append(this.rgbFillString(this.fillColor) +"\n");
				if (this.hasStroke()) {
					this.setRender(2);		// filled and stroked
					sb.append(rgbStrokeString(this.strokeColor) +"\n");
					sb.append(fourPlaces.format(this.weight) + " w\n");
				} 
				else {
					this.setRender(0);		// filled
				}
			}
			else {
				if (this.hasStroke()) {
					sb.append(rgbStrokeString(this.strokeColor) +"\n");
					sb.append(fourPlaces.format(this.weight) + " w\n");
					this.setRender(1);		// stroked
				} 
				else {
					this.setRender(3);		// invisible
				}
			}
		}
		String aStr = fourPlaces.format(this.matrix[0]);
		String bStr = fourPlaces.format(this.matrix[1]);
		String cStr = fourPlaces.format(this.matrix[2]);
		String dStr = fourPlaces.format(this.matrix[3]);
		String txStr = fourPlaces.format(this.tx);
		String tyStr = fourPlaces.format(this.ty);
		sb.append("u\n0 To\n")
		.append(aStr +" "+ bStr +" "+ cStr +" "+ dStr +" "+ txStr +" "+ tyStr +" "+ 
				fourPlaces.format(startPt) +" Tp\nTP\n")
		.append(render() +" Tr\n")
		.append("/_"+ fontname() +" "+ size() +" Tf\n")
		.append(alignment() +" Ta\n")
		.append(leading() +" "+ paragraphLeading() +" Tl\n")
		.append("("+ text() +") Tx 1 0 Tk\n")
		.append("TO\n")
		.append("U\n");
		sb.trimToSize();
		return sb.toString();
	}
	
	/**
	 * @throws UnsupportedOperationException, this is a terminal component
	 */
	public void add(DisplayComponent component) {
		throw new UnsupportedOperationException("Attempt to add child to a terminal node."); 
	}
	/**
	 * @throws UnsupportedOperationException, this is a terminal component
	 */
	public void add(ArrayList<? extends DisplayComponent> comps) {
		throw new UnsupportedOperationException("Attempt to add child to a terminal node."); 
	}
	/**
	 * @throws UnsupportedOperationException, this is a terminal component
	 */
	public boolean remove(DisplayComponent component) {
		throw new UnsupportedOperationException("Attempt to remove child from a terminal node."); 
	}
	/**
	 * @throws UnsupportedOperationException, this is a terminal component
	 */
	public DisplayComponent get(int index) {
		throw new UnsupportedOperationException("Attempt to access child of a terminal node.");
	}
	/**
	 * @throws UnsupportedOperationException, this is a terminal component
	 */
	public Iterator<DisplayComponent> iterator() {
		throw new UnsupportedOperationException("Attempt to access children array of a terminal node.");
	}
	/**
	 * @return null, this is a terminal component
	 */
	public ArrayList<DisplayComponent> children() {
		return null;
	}
	/**
	 * @return true, this is a terminal component
	 */
	public boolean isTerminal() {
		return true;
	}
	
	public void draw() {
		if (!this.isVisible) return;
		if (null != this.font) {
			parent.textFont(font);
			parent.fill(this.fillColor);
			parent.text(this.text, (float)(this.tx), (float)(this.ty));
		}
	}
	
	public void draw(PGraphics pg) {
		if (!this.isVisible) return;
		if (null != this.font) {
			pg.textFont(font);
			pg.fill(this.fillColor);
			pg.text(this.text, (float)(this.tx), (float)(this.ty));
		}
	}
	
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
		AIFileWriter.textObject(this, pw);
		if (bracketVisible) AIFileWriter.setVisible(true, pw);
		if (bracketLocked) AIFileWriter.setLocked(false, pw);
	}

	/**
	 * Illustrator will display the transformed text. Except for translation, Processing doesn't transform text. 
	 * @see net.paulhertz.aifile.DisplayComponent#transform(net.paulhertz.geom.Matrix3)
	 */
	@Override
	public void transform(Matrix3 matx) {
		// * TODO: We could use the supplied Matrix3 to set our internal 6-element matrix.
		// * For the moment, lets just transform locations.
		// this.setMatrix(matx);
		Point2D.Double pt = new Point2D.Double(this.tx, this.ty);
		pt = matx.multiplyPointByNormalCTM(tx, ty, pt);
		this.tx = pt.x;
		this.ty = pt.y;
		// System.out.println("transformed PointText " + this.id +": "+ tx +", "+ ty);
	}

	/* (non-Javadoc)
	 * This is a terminal node, no children to visit
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor) {
		visitor.visitPointText(this);
	}

	/* (non-Javadoc)
	 * This is a terminal node, no children to visit
	 * @see net.paulhertz.aifile.Visitable#accept(net.paulhertz.aifile.ComponentVisitor)
	 */
	@Override
	public void accept(ComponentVisitor visitor, boolean order) {
		visitor.visitPointText(this);
		
	}
	
	
}
