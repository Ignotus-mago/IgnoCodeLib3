import net.paulhertz.aifile.*;
import java.awt.Rectangle;
import java.io.PrintWriter;


/**
 * January 29, 2012 3:03:31 PM CST
 * June 25, 2013, modified for IgnoCodeLib 0.3 release
 * Uses low level code that ignores new features in new release (and in old release). 
 * Sample code for IgnoCodeLib 0.2 Processing library by Paul Hertz
 * This example provides low level code, for the curious. IgnoCodeLib graphics of class BezShape
 * and its subclasses can draw to an Adobe Illustrator file just by calling their draw() method.
 * This example shows how to write line and curve paths to Adobe Illustrator by calling path
 * operators directly. Also demonstrates color output to an AI file, including transparency.
 * Transparency markup is not supported in the AI7.0 specification, but it seems to work.
 * Note that in Illustrator transparency affects the entire shape, both fill and stroke,
 * unlike Processing, where fill and stroke can have separate transparency values. This
 * means the most recent transparency value, whether of fill or stroke, will affect the 
 * whole shape in AI.
 */


float xCenter;
float yCenter;
PrintWriter output;
String aiFilename = "directOutput.ai";
/** true if we are saving to a file, false otherwise */
boolean saveToFile;
/** graphics state flag: true if shapes should be filled, false otherwise */
boolean filled;
/** graphics state flag: true if shapes should be stroked, false otherwise */
boolean stroked;
/** graphics state flag: true if shapes should be closed, false otherwise */
boolean closed;
/** graphics state flag: true if colors should be transparent, false otherwise */
boolean transparent;
/** graphics state: the current fill color */
color currentFill;
/** graphics state: the current stroke color */
color currentStroke;
/** graphics state: the current stroke weight */
float currentWeight;


public void setup() {
  size(400, 400);
  background(255);
  smooth();
  xCenter = width/2;
  yCenter = height/2;
  saveToFile = false;
  println("Type 's' to save file");
}

public void draw() {
  doDrawing();
}

void doDrawing() {
  setClosed();
  setNoStroke();
  setFill(color(233, 47, 233));
  bezCircle(xCenter, yCenter, 150, 5);
  setFill(color(47, 233, 233));
  bezCircle(xCenter, yCenter, 100, 5);
  setFill(color(233, 233, 47));
  bezCircle(xCenter, yCenter, 50, 5);
  // 50% transparency stroke
  setStroke(color(55, 89, 144, 127));
  setWeight(8);
  // 50% transparency fill
  setFill(color(233, 0, 0, 127));
  int x = 200; 
  int y = 300;
  float rot = PI/6;
  float ang = getAngle(x - xCenter, y - yCenter) + rot;  
  bezTriangle(x, y, 50, ang);
  x = 200;
  y = 100;
  ang = getAngle(x - xCenter, y - yCenter) + rot;
  bezTriangle(x, y, 50, ang);
  x = 100;
  y = 200;
  ang = getAngle(x - xCenter, y - yCenter) + rot;
  bezTriangle(x, y, 50, ang);
  x = 300;
  y = 200;
  ang = getAngle(x - xCenter, y - yCenter) + rot;
  bezTriangle(x, y, 50, ang);
  setClosed();
  setNoStroke();
  // opaque blue circle in upper left corner
  setFill(color(47, 123, 233, 255));
  bezCircle(50, 50, 25, 4);
  // yellow circle in lower left corner
  setFill(color(233, 233, 47));
  bezCircle(50, height - 50, 15, 4);
  // green circle in lower right corner
  setFill(color(29, 233, 68));
  bezCircle(width - 50, height - 50, 30, 4);
  // red circle in upper right corner, transparent stroke
  setStroke(color(0, 0, 0, 127));
  setFill(color(233, 76, 68));
  bezCircle(width - 50, 50, 35, 4);
}

/**
 * Sets the current fill color, sets graphics state variable filled to true
 * @param fillColor   the fill color to set
 */
void setFill(color fillColor) {
  currentFill = fillColor;
  filled = true;
  fill(fillColor);
}
/**
 * Sets graphics state variable filled to false.
 */
void setNoFill() {
  filled = false;
  noFill();
}

/**
 * Sets the current stroke color, sets graphics state variable stroked to true
 * @param fillColor   the fill color to set
 */
void setStroke(color strokeColor) {
  currentStroke = strokeColor;
  stroked = true;
  stroke(strokeColor);
}
/**
 * Sets graphics state variable stroked to false.
 */
void setNoStroke() {
  stroked = false;
  noStroke();
}

/**
 * Sets the current weight.
 * @param weight   the weight to set.
 */
void setWeight(int weight) {
  currentWeight = weight;
  strokeWeight(weight);
}

/**
 * Sets the graphics state variable closed to true. Shapes draw while 
 * closed is true will be closed when output to Adobe Illustrator.
 */
void setClosed() {
  closed = true;
}
/**
 * Sets the graphics state variable closed to false.  Shapes draw while 
 * closed is false will be open when output to Adobe Illustrator.
 */
void setOpen() {
  closed = false;
}


public void keyPressed() {
  if (key == 's' || key == 'S') {
    saveAI();
  }
}


/**
 * Saves drawing to an Adobe Illustrator file.
 */
public void saveAI() {
  println("saving Adobe Illustrator file " + aiFilename + "...");
  output = createWriter(aiFilename);
  AIFileWriter.writeHeader(output, "Direct Output", "Ignotus", "ignoStudio", 
  612, 792, new Rectangle(width, height));
  AIFileWriter.writeState(output);
  AIFileWriter.setFill(0.0, output);
  AIFileWriter.setStroke(0.0, output);
  saveToFile = true;
  doDrawing();
  saveToFile = false;
  AIFileWriter.writeTrailer(output);
  output.flush();
  output.close();
}


/**
 * Given magnitudes dx and dy return the angle of rotation measured
 * clockwise from 0, degrees in radians
 * @param dx   displacement on x-axis
 * @param dy   displacement on y-axis
 * @return     float value representing angle to vector (dx, dy) in radians.
 */
float getAngle(float dx, float dy) {
  double ang;
  if (dx != 0) {
    ang = Math.atan(Math.abs(dy/dx));
  }
  else if (dy != 0) {
    ang = PI / 2;
  }
  else {
    ang = 0;
  }
  if (dx < 0) {
    if (dy < 0) {
      ang = ang + PI;
    }
    else {
      ang = PI - ang;
    }
  }
  else if (dy < 0) {
    ang = 2 * PI - ang;
  }
  return (float) ang;
}


/**
 * @param pin      the x and y coordinates of a point as a two-member array of float
 * @param xTrans   translation on x-axis
 * @param yTrans   translation on y-axis
 * @return         the translated point as a new two-member array of float
 */
float[] translateCoord(float[] pin, float xTrans, float yTrans) {
  float[] pout = new float[2];
  pout[0] = pin[0] + xTrans;
  pout[1] = pin[1] + yTrans;
  return pout;
}


/**
 * Rotate vector or point (dx,dy) through an angle, degrees in radians, 
 * rotation is counterclockwise from the coordinate axis
 * @param dx      displacement on x-axis
 * @param dy      displacement on y-axis
 * @param theta   angle to rotate, in radians
 * @return        the rotated point (dx, dy) as a two-member array of float 
 */
float[] rotateCoord(float dx, float dy, float theta) {
  float sintheta = sin(theta);
  float costheta = cos(theta);
  float[] newPoint = new float[2];
  newPoint[0] = dx * costheta - dy * sintheta; 
  newPoint[1] = dx * sintheta + dy * costheta;
  return newPoint;
}


/**
 * Draw a triangle centered on xctr, yctr, with radius radius, rotated by ang radians.
 * If global variable saveToFile is true, will output triangle geometry to an 
 * open Adobe Illustrator file pointed to by global variable output. 
 * @param xctr     x-coordinate of center of triangle
 * @param yctr     y-coordinate of center of triangle
 * @param radius   radius of circumcircle of triangle
 * @param ang      angle to rotate triangle, in radians
 */
void bezTriangle(float xctr, float yctr, float radius, float ang) {
  float x0, y0, x1, y1, x2, y2;
  float k = 2.23606797749979f; // sqrt(5)
  float yinc = radius/k;
  float xinc = 2 * yinc;
  x0 = 0;
  y0 = -radius;
  x1 = xinc;
  y1 = yinc;
  x2 = -xinc;
  y2 = y1;
  float[] pt0 = {
    x0, y0
  };
  pt0 = translateCoord(rotateCoord(x0, y0, ang), xctr, yctr);
  float[] pt1 = {
    x1, y1
  };
  pt1 = translateCoord(rotateCoord(x1, y1, ang), xctr, yctr);
  float[] pt2 = {
    x2, y2
  };
  pt2 = translateCoord(rotateCoord(x2, y2, ang), xctr, yctr);
  triangle(pt0[0], pt0[1], pt1[0], pt1[1], pt2[0], pt2[1]);
  if (saveToFile) {
    int pathOp = setAttributes();
    AIFileWriter.psMoveTo(pt0[0], pt0[1], output);
    AIFileWriter.psLineTo(pt1[0], pt1[1], output);
    AIFileWriter.psLineTo(pt2[0], pt2[1], output);
    AIFileWriter.psLineTo(pt0[0], pt0[1], output);
    AIFileWriter.paintPath(pathOp, output);
  }
}


/**
 * Draw a circle with sectors number of Bezier curves, centered on xctr, yctr
 * If global variable saveToFile is true, will output circle geometry to an 
 * open Adobe Illustrator file pointed to by global variable output. 
 * @param xctr      x-coordinate of center of circle
 * @param yctr      y-coordinate of center of circle
 * @param radius    radius of circumcircle of circle
 * @param sectors   number of Bezier curve segments in which the circle is divided.
 */
void bezCircle(float xctr, float yctr, float radius, int sectors) {
  // kappa, constant for calculating Bezier control points for a circle
  float kappa = 0.5522847498f;
  float k = 4 * kappa / sectors;
  float d = k * radius;
  float ax1, ay1, cx1, cy1, cx2, cy2, ax2, ay2;
  float[] cp1 = new float[2];
  float[] cp2 = new float[2];
  float[] ap2 = new float[2];
  ax1 = 0;
  ay1 = radius;
  cx1 = d;
  cy1 = radius;
  cp2 = rotateCoord(-d, radius, -TWO_PI/sectors);
  ap2 = rotateCoord(0, radius, -TWO_PI/sectors);
  cx2 = cp2[0];
  cy2 = cp2[1];
  ax2 = ap2[0];
  ay2 = ap2[1];
  beginShape();
  vertex(ax1 + xctr, ay1 + yctr);
  bezierVertex(cx1 + xctr, cy1 + yctr, cx2 + xctr, cy2 + yctr, ax2 + xctr, ay2 + yctr);
  int pathOp = 0;
  if (saveToFile) {
    pathOp = setAttributes();
    AIFileWriter.psMoveTo(ax1 + xctr, ay1 + yctr, output);
    AIFileWriter.psCurveTo(cx1 + xctr, cy1 + yctr, cx2 + xctr, cy2 + yctr, ax2 + xctr, ay2 + yctr, output);
  }
  for (int i = 1; i < sectors; i++) {
    cp1 = rotateCoord(cx1, cy1, i * -TWO_PI/sectors);
    cp2 = rotateCoord(cx2, cy2, i * -TWO_PI/sectors);
    ap2 = rotateCoord(ax2, ay2, i * -TWO_PI/sectors);
    bezierVertex(cp1[0] + xctr, cp1[1] + yctr, cp2[0] + xctr, cp2[1] + yctr, ap2[0] + xctr, ap2[1] + yctr);
    if (saveToFile) {
      AIFileWriter.psCurveTo(cp1[0] + xctr, cp1[1] + yctr, cp2[0] + xctr, 
      cp2[1] + yctr, ap2[0] + xctr, ap2[1] + yctr, output);
    }
  }
  endShape();
  if (saveToFile) {
    AIFileWriter.paintPath(pathOp, output);
    if (transparent) {
      AIFileWriter.noTransparency(output);
    }
  }
}


/**
 * Uses graphics state variables to determine the path operator to output to an Adobe Illustrator file, 
 * sets current transparency in open Adobe Illustrator file pointed to by global variable output. 
 * Unlike Processing, Illustrator sets transparency for fill and stroke together. 
 * This method will write a transparent Illustrator object if the fill is transparent, 
 * otherwise it writes an opaque object.
 * The path operator indicates whether current geometry is stroked, filled, open or closed. 
 * @return   AI path operator as an int
 */
int setAttributes() {
  int pathOp = 0;
  boolean transparentFill = false;
  boolean transparentStroke = false;
  if (filled) {
    int[] colors = argbComponents(currentFill);
    if (colors[0] < 255) {
      AIFileWriter.setTransparency(colors[0]/255.0, output);
      transparentFill = true;
      transparent = true;
    }
    else if (transparent) {
      AIFileWriter.setTransparency(1, output);
      transparent = false;
    }
    AIFileWriter.setRGBFill(colors[1]/255.0, colors[2]/255.0, colors[3]/255.0, output);
    pathOp += AIFileWriter.FILL;
  }
  if (stroked) {
    int[] colors = argbComponents(currentStroke);
    if (colors[0] < 255) {
      transparentStroke = true;
      if (!transparentFill) println("Fill is opaque; ignoring transparent stroke");
    }
    AIFileWriter.setRGBStroke(colors[1]/255.0, colors[2]/255.0, colors[3]/255.0, output);
    pathOp += AIFileWriter.STROKE;
    AIFileWriter.setWeight(currentWeight, output);
  }
  if (closed) {
    pathOp += AIFileWriter.CLOSE;
  }
  return pathOp;
}


/**
 * @param argb   a Processing color as a 32-bit integer 
 * @return       an array of integers in the range 0..255 for each color component: {A, R, G, B}
 */
public int[] argbComponents(int argb) {
  int[] comp = new int[4];
  comp[0] = (argb >> 24) & 0xFF;  // alpha
  comp[1] = (argb >> 16) & 0xFF;  // Faster way of getting red(argb)
  comp[2] = (argb >> 8) & 0xFF;   // Faster way of getting green(argb)
  comp[3] = argb & 0xFF;          // Faster way of getting blue(argb)
  return comp;
}

