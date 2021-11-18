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


import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
// import java.util.ListIterator;

/**
 * @author Paul Hertz
 * 
 */
/** 
 * Provides a useful subset of Adobe Illustrator 7.0 file tags. If you don't want to bother working with this
 * class directly, use {@link net.paulhertz.aifile.BezShape BezShape} and its subclasses, {@link net.paulhertz.aifile.PointText PointText}, 
 * {@link net.paulhertz.aifile.DocumentComponent DocumentComponent} and the other classes based on 
 * {@link net.paulhertz.aifile.DisplayComponent DisplayComponent}.
 * These have a <code>draw()</code> method to draw to the Processing window and a 
 * <code>write(PrintWriter pw)</code> method to write their geometry or text and formatting to an Illustrator file.
 * {@link net.paulhertz.aifile.DocumentComponent DocumentComponent} can act both as a display list and an export 
 * tool: writing to an AI file can be as simple as one line of code. 
 * <p>
 * This class provides markup specified by the Adobe Illustrator 7.0 specification, the last public specification
 * of the Adobe Illustrator format. The full spec can be downloaded from Adobe. 
 * The {@link #setTransparency(double, PrintWriter) setTransparency} markup is not
 * supported by the AI7.0 spec; however, it seems fully functional. See 
 * {@link net.paulhertz.aifile.AIFileWriter#useTransparency() AIFileWriter.useTransparency} for 
 * further information on how to enable transparency for graphics. Markup to show and hide objects
 * was new in AI 10, but is included here.
 * </p>
 * <p>See <a href="http://www.idea2ic.com/File_Formats/Adobe%20Illustrator%20File%20Format.pdf" target="_blank">Adobe Illustrator 7.0 file</a>
 * or <a href="https://www.yumpu.com/en/document/read/48549703/adobe-illustrator-file-format-specification-adobe-partners" target="_blank">Adobe Illustrator 7.0 file</a>
 * for detailed information on the Adobe Illustrator 7.0 file format.
 * </p>
 * 
 * See the the DirectOutput.pde file for an example of output using AIFileWriter directly. 
 */
public class AIFileWriter {
	// TODO 0 Xw (show) and 1 Xw (hide) as of AI 10 control visibility of objects (DONE)
	// TODO 1 A (lock) and 0 A (unlock) control locking of objects (DONE)
	/**
	 * Tags that operate on paths.
	 * <pre>
	 *		 b	  1		  1		  1		closed filled and stroked path
	 *		 B	  1		  1		  0		open filled and stroked path
	 *		 f	  1		  0		  1		closed filled path
	 *		 F	  1		  0		  0		open filled path
	 *		 s	  0		  1		  1		closed stroked path
	 *		 S	  0		  1		  0		open stroked path
	 *		 n	  0		  0		  1		non-printing closed path
	 *		 N	  0		  0		  0		non-printing open path
	 * </pre>
	 */
	static final char gPathOps[] = { 'N', 'n', 'S', 's', 'F', 'f', 'B', 'b' };
	/** Binary flag for filled path operators. FILL, STROKE and CLOSE values can be summed to index a path operator tag */
	public final static int  FILL = 4;
	/** Binary flag for stroked path operators. FILL, STROKE and CLOSE values can be summed to index a path operator tag */
	public final static int  STROKE = 2;
	/** Binary flag for closed path operators. FILL, STROKE and CLOSE values can be summed to index a path operator tag */
	public final static int  CLOSE = 1;
	/** Closed, filled, and stroked path operator. */
	public final static char CLOSED_FILLED_STROKED = 'b';
	/** Open, filled, and stroked path operator. */
	public final static char OPEN_FILLED_STROKED = 'B';
	/** Closed and filled path operator. */
	public final static char CLOSED_FILLED = 'f';
	/** Open and filled path operator. */
	public final static char OPEN_FILLED = 'F';
	/** Closed and stroked path operator. */
	public final static char CLOSED_STROKED = 's';
	/** Open and stroked path operator. */
	public final static char OPEN_STROKED = 'S';
	/** Closed and non-printing (invisible) path operator. */
	public final static char NONPRINTING_CLOSED = 'n';
	/** Open and non-printing (invisible) path operator. */
	public final static char NONPRINTING_OPEN = 'N';
	/** Adobe Illustrator default curve recursion is 4 */
	static final int gCurveRecursionDepth = 4;
	/** A number formatter: call fourPlaces.format(Number) to return a String with four decimal places. */
	static public DecimalFormat fourPlaces;
	/** optional transparency flag, for export to AI. Transparency is not part of the AI 7.0 spec, but we try to support it. */
	// TODO moved this to AIFileWriter from BezShape, it is only relevant when exporting (DONE)
	protected static boolean useTransparency = false;
	
	// DecimalFormat sets formatting conventions from the local system, unless we tell it not to
	// make sure we use "." for decimal separator, as in US, not a comma, as in many other countries 
	static {
		Locale loc = Locale.US;
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(loc);
		dfSymbols.setDecimalSeparator('.');
		fourPlaces = new DecimalFormat("0.0000", dfSymbols);
	}
	
/*
 // Processing test of decimal format
void testDecimalFormat() {
  println("\nTesting decimal format. \nThe following numbers should appear with a '.' decimal separator.");
  println(AIFileWriter.fourPlaces.format(1000000));
  println(AIFileWriter.fourPlaces.format(.0001));
  println(AIFileWriter.fourPlaces.format(123.456789));
  println(AIFileWriter.fourPlaces.format(10023.897699));
  println(AIFileWriter.fourPlaces.format(-56892.00009));
  println(AIFileWriter.fourPlaces.format(-0.08925));
}
*/
	

	/**
	 * Writes an abbreviated Adobe Illustrator header to a PrintWriter.
	 * 
	 * @param pw		<code>PrintWriter</code> for file output
	 * @param title		<code>String</code> that will appear as window title
	 */
	public static void writeHeader(PrintWriter pw, String title) {
		pw.print(getHeader(title));
	}


	/**
	 * Returns an abbreviated Adobe Illustrator header as a String
	 * 
	 * @param title		<code>String</code> that will appear as window title
	 * @return          String containing AI header to write to file
	 */
	public static String getHeader(String title) {
		StringBuilder buf = new StringBuilder(1200);
		buf.append("%!PS-Adobe-7.0\n")
		.append("%%Creator: Adobe Illustrator(TM) 7.0.0\n")
		.append("%%For: (Paul Hertz) (ignoStudio)\n")
		.append("%%Title: (" + title + ")\n");
		buf.append("%%CreationDate: (4/6/92) (10:47 AM)\n")
		.append("%%BoundingBox: 76 168 559 555\n")
		.append("%%DocumentProcessColors: Black\n")
		.append("%%DocumentNeededResources: procset Adobe_packedarray 2.0 0\n")
		.append("%%+ procset Adobe_cmykcolor 1.1 0\n")
		.append("%%+ procset Adobe_cshow 1.1 0\n")
		.append("%%+ procset Adobe_customcolor 1.0 0\n")
		.append("%%+ procset Adobe_IllustratorA_AI3 1.0 1\n")
		.append("%AI3_ColorUsage: Black&White\n")
		.append("%AI3_TemplateBox: 304 393 304 393\n")
		.append("%AI3_TileBox: 0 0 576 750\n")
		.append("%AI3_DocumentPreview: None\n")
		.append("%%EndComments\n");
		buf.append("%%BeginProlog\n")
		.append("%%IncludeResource: procset Adobe_packedarray 2.0 0\n")
		.append("Adobe_packedarray /initialize get exec\n")
		.append("%%IncludeResource: procset Adobe_cmykcolor 1.1 0\n")
		.append("%%IncludeResource: procset Adobe_cshow 1.1 0\n")
		.append("%%IncludeResource: procset Adobe_customcolor 1.0 0\n")
		.append("%%IncludeResource: procset Adobe_IllustratorA_AI3 1.0 1\n")
		.append("%%EndProlog\n");
		buf.append("%%BeginSetup\n")
		.append("Adobe_cmykcolor /initialize get exec\n")
		.append("Adobe_cshow /initialize get exec\n")
		.append("Adobe_customcolor /initialize get exec\n")
		.append("Adobe_IllustratorA_AI3 /initialize get exec\n")
		.append("%%EndSetup\n");
		String str = buf.toString();
		return str;
	}


	/**
	 * Writes an abbreviated Adobe Illustrator header with user-supplied arguments to a PrintWriter.
	 * 
	 * @param pw		<code>PrintWriter</code> for file output
	 * @param title		<code>String</code> that will appear as window title
	 * @param creator   <code>String</code> name of document creator
	 * @param org       <code>String</code> name of organization
	 * @param width     <code>int</code>, width of document art board
	 * @param height    <code>int</code>, height of document art board
	 * @param bbox      <code>Rectangle</code> bounding rectangle of artwork
	 */
	public static void writeHeader(PrintWriter pw, String title, String creator, String org, 
			                       int width, int height, Rectangle bbox) {
		pw.print(getHeader(title, creator, org, width, height, bbox));
	}


	/**
	 * Creates an abbreviated Adobe Illustrator header with user-supplied arguments and returns it as a String.
	 * 
	 * @param title		<code>String</code> that will appear as window title
	 * @param creator   <code>String</code> name of document creator
	 * @param org       <code>String</code> name of organization
	 * @param width     <code>int</code>, width of document art board
	 * @param height    <code>int</code>, height of document art board
	 * @param bbox      <code>Rectangle</code> bounding rectangle of artwork
	 * @return          String containing AI header
	 */
	public static String getHeader(String title, String creator, String org, 
			                       int width, int height, Rectangle bbox) {
		StringBuilder buf = new StringBuilder(1280);
		buf.append("%!PS-Adobe-7.0\n")
		.append("%%Creator: Adobe Illustrator(TM) 7.0.0\n")
		.append("%%For: (" + creator + ") (" + org + ")\n")
		.append("%%Title: (" + title + ")\n")
		.append("%%CreationDate: " + currentDate() + "\n")
		.append("%%BoundingBox: "+ bbox.x +" "+ bbox.y +" "+ bbox.x + bbox.width +" "+ bbox.y + bbox.height + "\n");
		buf.append("%%DocumentProcessColors: Black\n")
		.append("%%DocumentNeededResources: procset Adobe_packedarray 2.0 0\n")
		.append("%%+ procset Adobe_cmykcolor 1.1 0\n")
		.append("%%+ procset Adobe_cshow 1.1 0\n")
		.append("%%+ procset Adobe_customcolor 1.0 0\n")
		.append("%%+ procset Adobe_IllustratorA_AI3 1.0 1\n")
		.append("%AI3_ColorUsage: Black&White\n");
		// we don't use a template, so template box width and height must be 0
		int midx = bbox.x + bbox.width/2;
		int midy = bbox.y + bbox.height/2;
		buf.append("%AI3_TemplateBox: "+ midx +" "+ midy +" "+ midx +" "+ midy + "\n")
		// used in Mac AI, TileBox is bounds of imageable area of current page size, we default to US Letter
		.append("%AI3_TileBox: 0 0 576 750\n")
		.append("%AI3_DocumentPreview: None\n")
		.append("%AI5_ArtSize: " + width +" "+ height + "\n")
		.append("%%EndComments\n");
		buf.append("%%BeginProlog\n")
		.append("%%IncludeResource: procset Adobe_packedarray 2.0 0\n")
		.append("Adobe_packedarray /initialize get exec\n")
		.append("%%IncludeResource: procset Adobe_cmykcolor 1.1 0\n")
		.append("%%IncludeResource: procset Adobe_cshow 1.1 0\n")
		.append("%%IncludeResource: procset Adobe_customcolor 1.0 0\n")
		.append("%%IncludeResource: procset Adobe_IllustratorA_AI3 1.0 1\n")
		.append("%%EndProlog\n");
		buf.append("%%BeginSetup\n")
		.append("Adobe_cmykcolor /initialize get exec\n")
		.append("Adobe_cshow /initialize get exec\n")
		.append("Adobe_customcolor /initialize get exec\n")
		.append("Adobe_IllustratorA_AI3 /initialize get exec\n")
		.append("%%EndSetup\n");
		String str = buf.toString();
		return str;
	}
	
	
	/**
	 * @return formatted date string, hours in 24-hour format
	 */
	public static String currentDate() {
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat();
		StringBuffer buf = new StringBuffer("(");
		df. applyPattern("MM/dd/yyyy");
		buf.append(df.format(now));
		buf.append(") (");
		df.applyPattern("HH:mm");
		buf.append(df.format(now));
		buf.append(")");
		buf.trimToSize();
		return buf.toString();
	}

	
	/**
	 * Writes Adobe Illustrator 3.0 initial graphics state.
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void writeState(PrintWriter pw) {
		pw.print(getState());
	}

	/**
	 * Creates Adobe Illustrator 3.0 initial graphics state and returns it as a String.
	 * Uses the following operators:
	 * <pre>
     *   tag    values                                                          default
     *   A      [0,1] if subsequent objects are unlocked, 1 if they are locked      0
     *   R      [0, 1] if stroke does not overprint, 1 if stroke overprints         0
     *   G      0..1 stroke gray value, 0 = black, 1 = white                        0
     *   i      0..100, 0 = automatically set by Illustrator                        0
     *   J      [0, 1, 2], linecap: 0 = butt end, 1 = round, 2 = square             0
     *   J      [0, 1, 2] linejoin: 0 = mitered, 1 = round, 2 = beveled             0
     *   w      positive real value, line width                                     1
     *   M      real number &gt; 1, miter limit: ratio of miter length/line width      4
     *   d      array of values for dashes and gaps, phase of dash pattern          []0
     *   D      [0, 1] winding order tag, 0 = clockwise, 1 = counterclockwise       0
	 * </pre>
	 * @return   A String containing the initial graphics state.
	 */
	public static String getState() {
		StringBuffer buf = new StringBuffer(59);
		buf.append("0 A\n")
		.append("0 R\n")
		.append("0 G\n")
		.append("0 i 0 J 0 j 1 w 4 M []0 d\n")
		.append("%AI3_Note:\n")
		.append("0 D\n");
		return buf.toString();
	}

	/**
	 * Writes Adobe Illustrator 3.0 abbreviated trailer.
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void writeTrailer(PrintWriter pw) {
		pw.print(getTrailer());
	}

	/**
	 * Creates an Adobe Illustrator 3.0 abbreviated trailer and returns it as a String.
	 * @return     a <code>String</code> containing an AI 3.0 trailer.
	 */
	public static String getTrailer() {
		StringBuilder buf = new StringBuilder(210);
		buf.append("%%Trailer\n");
		buf.append("Adobe_IllustratorA_AI3 /terminate get exec\n");
		buf.append("Adobe_customcolor /terminate get exec\n");
		buf.append("Adobe_cshow /terminate get exec\n");
		buf.append("Adobe_cmykcolor /terminate get exec\n");
		buf.append("Adobe_packedarray /terminate get exec\n");
		buf.append("%%EOF\n");
		return buf.toString();
	}
	
	
	/**
	 * Writes an open group operator "u" to output.
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void openGroup (PrintWriter pw) {
		pw.println("u");
	}
	/**
	 * Writes a close group operator "U" to output.
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void closeGroup (PrintWriter pw) {
		pw.println("U");
	}

	
	// layer codes format: 
	// visible preview enabled printing dimmed hasMultiLayerMasks colorIndex red green blue Lb
	/**
	 * Writes begin layer operator "%AI5_BeginLayer" and various parameters to output. 
	 * Layer is visible and enabled.
	 * @param layerName		<code>String</code>, name of layer 
	 * @param colorIndex	<code>int</code>, index to color associated with layer
	 * @param pw			<code>PrintWriter</code> for file output
	 */
	public static void beginLayer(String layerName, int colorIndex, PrintWriter pw) {
		StringBuilder buf = new StringBuilder(100);
		buf.append("%AI5_BeginLayer\n");
		buf.append("1 1 1 1 0 0 "+ colorIndex +" 255 79 79 Lb\n");
		buf.append("("+ layerName +") Ln\n");
		pw.print(buf.toString());
	}	
	// layer codes format: 
	// visible preview enabled printing dimmed hasMultiLayerMasks colorIndex red green blue Lb
	/**
	 * Writes begin layer operator "%AI5_BeginLayer" and various parameters to output.
	 * @param layerName		<code>String</code>, name of layer 
	 * @param colorIndex	<code>int</code>, index to color associated with layer
	 * @param isVisible     true if layer is visible, false otherwise
	 * @param isLocked      true if layer is not enabled, false otherwise
	 * @param pw			<code>PrintWriter</code> for file output
	 */
	public static void beginLayer(String layerName, int colorIndex, boolean isVisible, boolean isLocked, PrintWriter pw) {
		StringBuilder buf = new StringBuilder(100);
		buf.append("%AI5_BeginLayer\n");
		if (isVisible) {
			if (!isLocked) {
				// visible and enabled
				buf.append("1 1 1 1 0 0 "+ colorIndex +" 255 80 80 Lb\n");
			}
			else {
				// visible and disabled
				buf.append("1 1 0 1 0 0 "+ colorIndex +" 255 80 80 Lb\n");
			}
		}
		else {
			if (!isLocked) {
				// not visible but enabled
				buf.append("0 1 1 1 0 0 "+ colorIndex +" 255 80 80 Lb\n");
			}
			else {
				// not visible and not enabled
				buf.append("0 1 0 1 0 0 "+ colorIndex +" 255 80 80 Lb\n");
			}
		}
		buf.append("("+ layerName +") Ln\n");
		pw.print(buf.toString());
	}
	/**
	 * Writes end layer operator "LB" to output.
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void endLayer(PrintWriter pw) {
		pw.println("LB");
		pw.println("%AI5_EndLayer--");
	}
		

	/**
	 * Sets linecap and linejoin styles, sets miter limit for lines. 
	 * @param linecap      0 = butt end, 1 = round, 2 = square
	 * @param linejoin     0 = mitered, 1 = round, 2 = beveled
	 * @param miterLimit   miter limit: ratio of miter length/line width, in range 1..10, default 4
	 * @param pw	       <code>PrintWriter</code> for file output
	 */
	public static void setLineAttributes(int linecap, int linejoin, int miterLimit, PrintWriter pw) {
		pw.println(linecap +" j "+ linejoin +" J "+ miterLimit +" M");
	}


	/**
	 * Sets the current dash pattern. Pass in an empty array to reset the pattern to solid lines.
	 * @param pattern   an array of number pairs representing dashes and gaps
	 * @param pw        <code>PrintWriter</code> for file output
	 */
	public static void setDashPattern(double[] pattern, PrintWriter pw) {
		StringBuffer buf = new StringBuffer(32);
		buf.append("[");
		buf.append(fourPlaces.format(pattern[0]));
		for (int i = 1; i < pattern.length; i++) {
			buf.append(" "+ fourPlaces.format(pattern[i]));
		}
		buf.append("]0 d");
		pw.println(buf.toString());
	}
	
	
	/**
	 * Writes grayscale fill value and fill operator "g" to output.
	 * @param shade   <code>double</code> in range 0.0..1.0, grayscale value
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void setFill(double shade, PrintWriter pw) {
		pw.println(fourPlaces.format(shade) + " g");
	}


	/**
	 * Writes grayscale stroke value and stroke operator "G" to output.
	 * @param shade   <code>double</code> in range 0.0..1.0, grayscale value
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void setStroke(double shade, PrintWriter pw) {
		pw.println(fourPlaces.format(shade) + " G");
	}

	
	/**
	 * Writes CMYK fill value and fill operator "k" to output.
	 * @param c   cyan component (0..1)
	 * @param m   magenta component (0..1)
	 * @param y   yellow component (0..1)
	 * @param k   black component (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setCMYKFill(double c, double m, double y, double k, PrintWriter pw) {
		String cs = fourPlaces.format(c);
		String ms = fourPlaces.format(m);
		String ys = fourPlaces.format(y);
		String ks = fourPlaces.format(k);
		pw.println( cs +" "+ ms +" "+ ys +" "+ ks +" k");
	}
	/**
	 * Writes CMYK fill value and fill operator "k" to output.
	 * @param shade   an array of four <code>double</code>s in the range (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setCMYKFill(double[] shade, PrintWriter pw) {
		setCMYKFill(shade[0], shade[1], shade[2], shade[3], pw);
	}
	/**
	 * Writes CMYK fill value and fill operator "k" to output.
	 * @param shade   a CMYKColor instance
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setCMYKFill(CMYKColor shade, PrintWriter pw) {
		setCMYKFill(shade.c, shade.m, shade.y, shade.k, pw);
	}


	/**
	 * Writes CMYK stroke value and fill operator "K" to output.
	 * @param c   cyan component (0..1)
	 * @param m   magenta component (0..1)
	 * @param y   yellow component (0..1)
	 * @param k   black component (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setCMYKStroke(double c, double m, double y, double k, PrintWriter pw) {
		String cs = fourPlaces.format(c);
		String ms = fourPlaces.format(m);
		String ys = fourPlaces.format(y);
		String ks = fourPlaces.format(k);
		pw.println( cs +" "+ ms +" "+ ys +" "+ ks +" K");
	}
	/**
	 * Writes CMYK stroke value and fill operator "K" to output.
	 * @param shade   an array of four <code>double</code>s in the range (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setCMYKStroke(double[] shade, PrintWriter pw) {
		setCMYKStroke(shade[0], shade[1], shade[2], shade[3], pw);
	}
	/**
	 * Writes CMYK stroke value and fill operator "K" to output.
	 * @param shade   a CMYKColor instance
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setCMYKStroke(CMYKColor shade, PrintWriter pw) {
		setCMYKStroke(shade.c, shade.m, shade.y, shade.k, pw);
	}


	/**
	 * Writes RGB fill value and fill operator "Xa" to output.
	 * @param r   red component (0..1)
	 * @param g   green component (0..1)
	 * @param b   blue component (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setRGBFill(double r, double g, double b, PrintWriter pw) {
		String rs = fourPlaces.format(r);
		String gs = fourPlaces.format(g);
		String bs = fourPlaces.format(b);
		pw.println(rs +" "+ gs +" "+ bs +" Xa");
	}
	/**
	 * Writes RGB fill value and fill operator "Xa" to output.
	 * @param shade   an array of four <code>double</code>s in the range (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setRGBFill(double[] shade, PrintWriter pw) {
		setRGBFill(shade[0], shade[1], shade[2], pw);
	}
	/**
	 * Writes RGB fill value and fill operator "Xa" to output.
	 * @param shade   an RGBColor instance
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setRGBFill(RGBColor shade, PrintWriter pw) {
		setRGBFill(shade.r, shade.g, shade.b, pw);
	}


	/**
	 * Writes RGB stroke value and fill operator "XA" to output.
	 * @param r   red component (0..1)
	 * @param g   green component (0..1)
	 * @param b   blue component (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setRGBStroke(double r, double g, double b, PrintWriter pw) {
		String rs = fourPlaces.format(r);
		String gs = fourPlaces.format(g);
		String bs = fourPlaces.format(b);
		pw.println( rs +" "+ gs +" "+ bs +" XA");
	}
	/**
	 * Writes RGB stroke value and fill operator "XA" to output.
	 * @param shade   an array of four <code>double</code>s in the range (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setRGBStroke(double[] shade, PrintWriter pw) {
		setRGBStroke(shade[0], shade[1], shade[2], pw);
	}
	/**
	 * Writes RGB stroke value and fill operator "XA" to output.
	 * @param shade   a RGBColor instance
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setRGBStroke(RGBColor shade, PrintWriter pw) {
		setRGBStroke(shade.r, shade.g, shade.b, pw);
	}


	/**
	 * Writes weight (in points) and weight operator "w" to output.
	 * @param weight   stroke weight (positive decimal value)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setWeight(double weight, PrintWriter pw) {
		pw.println(fourPlaces.format(weight) + " w");
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
	 * See {@link #setTransparency(double, PrintWriter)}.
	 * @param useTransparency   true if transparency value should be included in color attributes
	 */
	public static void setUseTransparency(boolean useTransparency) {
		AIFileWriter.useTransparency = useTransparency;
	}
	/**
	 * Writes current opacity to an Illustrator file (not part of the AI7 spec).
	 * This particular operator is pieced together from inspecting AI files
	 * It is not part of the AI7 specification, the last one published by Adobe.
	 * I do not know what each of the arguments to Xy does, but the second one controls opacity.
	 * @param trans   transparency value, in the range 0..1
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setTransparency(double trans, PrintWriter pw) {
		// 0 0.55 0 0 0 Xy
		pw.println("0 " + fourPlaces.format(trans) + " 0 0 0 Xy");
	}
	/**
	 * If you set transparency, either set it for every object, or reset it to totally opaque by calling noTransparency.
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void noTransparency(PrintWriter pw) {
		pw.println("0 1 0 0 0 Xy");
	}
	
	/**
	 * As of AI 10, visibility of objects is set by 0 Xw (show) and 1 Xw (hide). This
	 * particular operator is pieced together by inspecting AI files, it is not part of the AI7 spec.
	 * Affects all subsequent objects. Bracketing of visibility is automatically handled by
	 * built-in components (BezShape and subclasses, Group, Layer, and Text components). See 
	 * the {@code write()} methods of these components.
	 * @param isVisible   true if subsequent components are visible, false otherwise
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setVisible(boolean isVisible, PrintWriter pw) {
		if (isVisible) { pw.println("0 Xw"); }
		else { pw.println("1 Xw"); }
	}
	
	
	/** 
	 * 1 A (lock) and 0 A (unlock) control locking of objects. This particular operator 
	 * is pieced together by inspecting AI files, it is not part of the AI7 spec. The 
	 * Affects all subsequent objects. Bracketing of locking is automatically handled by
	 * built-in components (BezShape and subclasses, Group, Layer, and Text components). See 
	 * the {@code write()} methods of these components.
	 * @param isLocked   true if subsequent components are visible, false otherwise
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void setLocked(boolean isLocked, PrintWriter pw) {
		if (isLocked) { pw.println("1 A"); }
		else { pw.println("0 A"); }
	}
	
	
	/**
	 * Writes open palette tag and opening tags of color list to output.
	 * Additional colors are optional, but you must call <code>endPalette</code>
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void beginPalette(PrintWriter pw) {
		pw.println("%AI5_BeginPalette");
		pw.println("0 0 Pb");
		pw.println("Pn");
	}
	/**
	 * Writes closing color list tags and close palette tag to output.
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void endPalette(PrintWriter pw) {
		pw.println("Pc");
		pw.println("PB");
		pw.println("%AI5_EndPalette");
	}

	/**
	 * Writes grayscale color value to palette to output. Call between beginPalette and endPalette.
	 * @param shade   <code>double</code> in range 0.0..1.0, grayscale value
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteGrayCell(double shade, PrintWriter pw) {
		pw.println("Pc");
		pw.println(fourPlaces.format(shade) + " g");
	}

	/**
	 * Writes CMYK color values to palette to output. Call between beginPalette and endPalette.
	 * @param c   cyan component (0..1)
	 * @param m   magenta component (0..1)
	 * @param y   yellow component (0..1)
	 * @param k   black component (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteCMYKCell(double c, double m, double y, double k, PrintWriter pw) {
		String cs = fourPlaces.format(c);
		String ms = fourPlaces.format(m);
		String ys = fourPlaces.format(y);
		String ks = fourPlaces.format(k);
		pw.println("Pc");
		pw.println( cs +" "+ ms +" "+ ys +" "+ ks +" k");
	}
	/**
	 * Writes CMYK color values to palette to output. Call between beginPalette and endPalette.
	 * @param shade   an array of four <code>double</code>s in the range (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteCMYKCell(double[] shade, PrintWriter pw) {
		paletteCMYKCell(shade[0], shade[1], shade[2], shade[3], pw);
	}
	/**
	 * Writes CMYK color values to palette to output. Call between beginPalette and endPalette.
	 * @param shade   a CMYKColor instance
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteCMYKCell(CMYKColor shade, PrintWriter pw) {
		paletteCMYKCell(shade.c, shade.m, shade.y, shade.k, pw);
	}

	/**
	 * Writes RGB color values to palette to output. Call between beginPalette and endPalette.
	 * @param r   red component (0..1)
	 * @param g   green component (0..1)
	 * @param b   blue component (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteRGBCell(double r, double g, double b, PrintWriter pw) {
		String rs = fourPlaces.format(r);
		String gs = fourPlaces.format(g);
		String bs = fourPlaces.format(b);
		pw.println("Pc");
		pw.println( rs +" "+ gs +" "+ bs +" Xa");
	}
	/**
	 * Writes RGB color values to palette.
	 * @param shade   an array of four <code>double</code>s in the range (0..1)
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteRGBCell(double[] shade, PrintWriter pw) {
		paletteRGBCell(shade[0], shade[1], shade[2], pw);
	}
	/**
	 * Writes RGB color values to palette to output. Call between beginPalette and endPalette.
	 * @param shade   a RGBColor instance
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paletteRGBCell(RGBColor shade, PrintWriter pw) {
		paletteRGBCell(shade.r, shade.g, shade.b, pw);
	}


	/**
	 * Writes current point and "m" operator to output.
	 * @param x    x coordinate
	 * @param y    y coordinate
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void psMoveTo(double x, double y, PrintWriter pw) {
		pw.println( fourPlaces.format(x) +" "+ fourPlaces.format(y) + " m");
	}
	/**
	 * Writes current point and "m" operator to output.
	 * @param pt   array of two double values, x and y coordinates
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void psMoveTo(double[] pt, PrintWriter pw) {
		psMoveTo( pt[0], pt[1], pw);
	}
	/**
	 * Writes current point and "m" operator to output.
	 * @param pt   a Java Point2D instance
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void psMoveTo(Point2D pt, PrintWriter pw) {
		psMoveTo(pt.getX(), pt.getY(), pw);
	}


	/**	
	 * Writes current point and "L" operator to output.
	 * @param x    x coordinate
	 * @param y    y coordinate
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void psLineTo(double x, double y, PrintWriter pw) {
		pw.println( fourPlaces.format(x) +" "+ fourPlaces.format(y) + " L");
	}


	/**
	 * Writes current point and "c" operator to output.
	 * @param x1   control point 1 x coordinate
	 * @param y1   control point 1 y coordinate
	 * @param x2   control point 2 x coordinate
	 * @param y2   control point 2 y coordinate
	 * @param x3   end point x coordinate
	 * @param y3   end point y coordinate
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void psCurveTo(double x1, double y1, double x2, double y2, double x3, double y3, PrintWriter pw) {
		pw.println(fourPlaces.format(x1) +" "+ fourPlaces.format(y1) +" "+
				   fourPlaces.format(x2) +" "+ fourPlaces.format(y2) +" "+
				   fourPlaces.format(x3) +" "+ fourPlaces.format(y3) +" c");
	}


	/**
	 * Closes a series of path construction operations with the appropriate
	 * operator stored in gPathOps, {'N', 'n', 'S', 's', 'F', 'f', 'B', 'b'}.
	 * Requires an index value for the path operator. It may be simpler to call
	 * the other {@link #paintPath(char, PrintWriter) paintPath} method, that accepts
	 * character constant. 
	 * <pre>
	 *			fill	stroke	close	
	 *		 b	  1		  1		  1		closed filled and stroked path
	 *		 B	  1		  1		  0		open filled and stroked path
	 *		 f	  1		  0		  1		closed filled path
	 *		 F	  1		  0		  0		open filled path
	 *		 s	  0		  1		  1		closed stroked path
	 *		 S	  0		  1		  0		open stroked path
	 *		 n	  0		  0		  1		non-printing closed path
	 *		 N	  0		  0		  0		non-printing open path
	 * </pre>
	 * @param pathIndex   an int in the range (0..7)
	 * @param pw   <code>PrintWriter</code> for file output
	 */
	public static void paintPath(int pathIndex, PrintWriter pw) {
		char pathOp;
		pathOp = gPathOps[pathIndex];
		pw.println(pathOp);
	}	
	/**
	 * Closes a series of path construction operations with the appropriate operator.
	 *
	 * @param pathOp   a <code>char</code> in {'N', 'n', 'S', 's', 'F', 'f', 'B', 'b'}
	 *                 <p> It is simpler just to use one of the supplied constants
	 *                 CLOSED_FILLED_STROKED, OPEN_FILLED_STROKED, CLOSED_FILLED, OPEN_FILLED, 
	 *                 CLOSED_STROKED, OPEN_STROKED, NONPRINTING_CLOSED, NONPRINTING_OPEN</p>
	 * @param pw      <code>PrintWriter</code> for file output
	 */
	public static void paintPath(char pathOp, PrintWriter pw) {
		pw.println(pathOp);
	}	
	
	
	/**
	 * Writes a user-defined custom object to the file, in the format /&lt;tagIdentifier&gt; (&lt;tagValue&gt;) XT, to output.
	 * 
	 * @param tagIdentifier   a <code>String</code> to identify the custom object
	 * @param tagValue        a <code>String</code> that is assigned to the object as its value
	 * @param pw              <code>PrintWriter</code> for file output
	 */
	public static void customObject(String tagIdentifier, String tagValue, PrintWriter pw) {
		pw.println("/" + tagIdentifier +" ("+ tagValue +") " + "XT");
	}
	
	
	/**
	 * Writes the tag structure for a PointText instance to output.
	 * @param pt   a PointText instance
	 * @param pw   PrintWriter for file output
	 */
	public static void textObject(PointText pt, PrintWriter pw) {
		pw.println(pt.outputString());
	}
		
}

