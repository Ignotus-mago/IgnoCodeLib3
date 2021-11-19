/**
* Uses IgnoCodeLib 0.3 Processing library, http://paulhertz.net/ignocodelib/
* not compatible with earlier versions.
*
* Demo applet that creates blobby shapes as BezShapes, general shapes composed of vertices and
* Bezier vertices, defined in my IgnoCodeLib. Also shows how to sort an array of BezShapes, 
* how to rotate each shape around its center, how to draw control and anchor points, how to save to file. 
* Sorry, I have not yet written a Bezier shape editor to move those points and control points around
* interactively, though the DrawCurves sample code that comes with the IgnoCodeLib library shows how you 
* might start to do create one.
*
* Click to create a blob. 
* Type 'k' to show/hide control and anchor points.
* Type 'o' to change sort order of shapes (steps through 4 different orders). 
* Type 'r' to rotate shapes.
*
*/

import net.paulhertz.aifile.*;
import net.paulhertz.util.RandUtil;
import net.paulhertz.geom.*;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;


int bgFillColor;
/** a random number generator */
RandUtil rand;
/** 
 *  Given a circle that is divided into 4 sectors of 90 degrees:
 *  KAPPA = (distance between Bezier anchor and its associated control point) / (circle radius)
 *  see <a href="http://www.whizkidtech.redprince.net/bezier/circle/kappa/">http://www.whizkidtech.redprince.net/bezier/circle/kappa/</a>
 */
float kappa = 0.5522847498f;
/** modifiable version of kappa, try multiplying kappa by different factors */
float kk = kappa;
/** array to hold shapes */
ArrayList<BezShape> shapes;
/** variance in angular measurement of sectors (positive float) */
float sectorVariance = 0.2f;
/**  variance in amount of curvature (positive float) */
float curveVariance = 0.1f;
/** variance in length of radius at each Bezier anchor point (positive float) */
float radiusVariance = 0.16f;
/** flag whether to show or hide control points and blob skeleton */
boolean showSkeleton = false;
/** select sorting technique based on value of sortSelector */
int sortSelector = 0;
/** document to save */
DocumentComponent document;
/** white color */
int white = color(255, 255, 255);
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void settings() {
  size(640, 480);
}

public void setup() {
  smooth();
  bgFillColor = color(255);
  rand = new RandUtil();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  shapes = new ArrayList<BezShape>();
  PApplet.println("Click to create a blob."); 
  PApplet.println("Type 'k' to show/hide control and anchor points.");
  PApplet.println("Type 'o' to change sort order of shapes (steps through 4 different orders)."); 
  PApplet.println("Press 'r' to rotate shapes.");
}

public void draw() {
  background(bgFillColor);
  for (BezShape bez: shapes) {
    drawBezShape(bez, showSkeleton);
  }
}

public void mouseReleased() {
  // prepare to create a random blob
  int sectors = rand.randomInRange(4, 13);
  float rad = (float) rand.gauss(60, 0.25);
  rad = rad * PApplet.map(mouseY, 0, height, 0.5f, 2.0f);
  do {
    sectorVariance = ((float) rand.quickGauss(0.1, 0.001));
  } while (sectorVariance < 0);
  curveVariance = abs((float) rand.quickGauss(0.0, 0.001));
  radiusVariance = abs((float) rand.quickGauss(0.0, 0.005));
  float angle = PApplet.radians(rand.randomInRange(0, 90));
  int[] channelValues = {64, 96, 128, 160, 192, 224};
  int farb = Palette.randColor(channelValues);
  float fade = map(mouseY, 0, height, 0.3f, 1);
  farb = lerpColor(white, farb, fade);
  BezShape shape = createBlob(sectors, rad, sectorVariance, curveVariance, radiusVariance, angle, mouseX, mouseY, farb);
  shapes.add(shape);
}

public void keyPressed() {
  if (key == 'r' || key == 'R') {
    // rotate shapes
    for (BezShape bez : shapes) {
      bez.rotateShape(PApplet.radians(3));
    }
  }
}

public void keyReleased() {
  if (key == 'k' || key == 'K') {
    // hide/show Bezier control and anchor points
    showSkeleton = !showSkeleton;
  }
  else if (key == 'o' || key == 'O') {
    // sort shapes
    if (0 == sortSelector) {
      Collections.sort(shapes, new CompareX());
      PApplet.println("sorting on X coordinate");
    }
    else if (1 == sortSelector) {
      Collections.sort(shapes, new CompareY());
      PApplet.println("sorting on Y coordinate");
    }
    else if (2 == sortSelector) {
      Collections.sort(shapes, new CompareCtr(mouseX, mouseY));
      println("sorting on mouse location: " + mouseX +" "+ mouseY);
    }
    else if (3 == sortSelector) {
      Collections.sort(shapes, new CompareAng(mouseX, mouseY));
      println("sorting by angle on mouse location: " + mouseX +" "+ mouseY);
    }
    sortSelector = (sortSelector + 1) % 4;
  }
  else if (key == 's' || key == 'S') {
    createDocument();
    saveAI("blobby.ai", document);
  }
}

/**
 * creates a blobby Bezier shape
 * sectorVariance of 0 sets all sectors equal, value > 1 may result in negative sector angles
 * curveVariance of 0 sets all curvatures to approximate a circle 
 * radiusVariance of 0 sets all radial distances equal (to radius variable)
 * for example, createBlob(5, radius, 0.1, 0.0, 0.0) will draw circles with 5 slightly irregular sectors
 * @param sectors          number of sectors
 * @param rad              radius
 * @param sectorVariance   variance in angular measurement of sectors (positive float)
 * @param curveVariance    variance in amount of curvature (positive float)
 * @param radiusVariance   variance in length of radius at each Bezier anchor point (positive float)
 * @param rot              radians to rotate sectors
 * @param dx               x-coordinate of center point (used for transforms)
 * @param dy               y-coordinate of center point (used for transforms)
 * @param farb             fill color for blob
 */
public BezShape createBlob(int sectors, float rad, float sectorVariance, float curveVariance, 
    float radiusVariance, float rot, float dx, float dy, int farb) {
  float[] sects = new float[sectors];
  float sum = 0;
  // fill the sects array with a series of increasing numbers at a nearly uniform 
  // distance from each other, with a variability determined by sectorVariance
  for (int i = 0; i < sectors; i++) {
    float num = (float) rand.quickGauss(2, sectorVariance);
    sum = sum + num;
    sects[i] = num;
    // println("sects[" + i + "] = " + num);
  }
  // println("sum = " + sum + "\n");
  // scale the sects array to fill a range of 2 * pi, its values  will determine
  // the angles between anchor points with respect to the center point dx, dy
  sects = reapportion(sects, PApplet.TWO_PI);
  // calculate the factor k that determines the distance between 
  // a Bezier anchor point and its associated control points
  float k = (4 * kk)/PApplet.TWO_PI;
  float d;
  // local variables: starting point, control point 1, control point 2, anchor point
  Point2D.Float pt, cp1, cp2, ap1;
  // cumulative rotation from the starting point
  float theta = 0;
  float kfac = (float) rand.quickGauss(1, curveVariance);
  float rfac = (float) rand.quickGauss(1, radiusVariance);
  float r = rad * rfac;
  float r0 = r;
  // starting point
  pt = GeomUtils.rotateCoor(r0, 0, rot);
  // an array for the points, one to start with, then three more for each Bezier vertex
  ArrayList<Point2D.Float> bezPoints = new ArrayList<Point2D.Float>();
  // translate starting point by dx, dy and add it to the array 
  bezPoints.add(GeomUtils.translateCoor(pt, dx, dy));
  for (int i = 0; i < sectors; i++) {
    kfac = (float) rand.quickGauss(1, curveVariance);
    // calculate distance between anchor point and control points
    d = sects[i] * k * r * kfac;
    // first control point
    cp1 = GeomUtils.rotateCoor(r, d, theta + rot);
    bezPoints.add(GeomUtils.translateCoor(cp1, dx, dy));
    theta += sects[i];
    if (i != sectors - 1) {
      rfac = (float) rand.quickGauss(1, radiusVariance);
      r = rad * rfac;
    } 
    else {
      r = r0;
    }
    // second control point
    cp2 = GeomUtils.rotateCoor(r, -d, theta + rot);
    bezPoints.add(GeomUtils.translateCoor(cp2, dx, dy));
    // anchor point
    ap1 = GeomUtils.rotateCoor(r, 0, theta + rot);
    bezPoints.add(GeomUtils.translateCoor(ap1, dx, dy));
  }
  // we have the points, now make a Bezier shape
  pt = bezPoints.get(0);
  BezShape bezo = new BezShape(pt.x, pt.y);
  bezo.setCenter(dx, dy);
  bezo.setFillColor(farb);
  bezo.setNoStroke();
  for (int i = 1; i < bezPoints.size(); i += 3) {
    cp1 = bezPoints.get(i);
    cp2 = bezPoints.get(i + 1);
    ap1 = bezPoints.get(i + 2);
    bezo.append(cp1.x, cp1.y, cp2.x, cp2.y, ap1.x, ap1.y);
  }
  return bezo;
}

/**
 * @param series   an array of floats, all > 1.0
 * @param range    the desired scaled sum of the values in series
 * @return         the array with its values scaled so they sum to range
 */
public float[] reapportion(float[] series, float range) {
  float sum = 0;
  for (int i = 0; i < series.length; i++) {
    sum += series[i];
  }
  float d = range/sum;
  for (int i = 0; i < series.length; i++) {
    series[i] *= d;
  }
  sum = 0;
  for (int i = 0; i < series.length; i++) {
    float num = series[i];
    sum += num;
    // println("sects[" + i + "] = " + num);
  }
  // println("sum = " + sum + "\n");
  return series;
}

/**
 * draws a BezShape, optionally showing its construction lines
 * @param bShape         the BezShape to draw
 * @param showSkeleton   true if you want Bezier knots and other construction lines to show
 */
void drawBezShape(BezShape bShape, boolean showSkeleton) {
  bShape.draw();
  if (showSkeleton) {
    Point2D.Float pt;
    float dx = bShape.xctr();
    float dy = bShape.yctr();
    pt = new Point2D.Float(bShape.x(), bShape.y());
    stroke(246, 233, 0);
    line(dx, dy, pt.x, pt.y);
    ListIterator<Vertex2DINF> it = bShape.curveIterator();
    while (it.hasNext()) {
      Vertex2DINF bv = it.next();
      strokeWeight(0.5f);
      if (BezShape.CURVE_SEGMENT == bv.segmentType()) {
        float[] coords = bv.coords();
        stroke(64);
        fill(160);
        line(pt.x, pt.y, coords[0], coords[1]);
        line(coords[2], coords[3], coords[4], coords[5]);
        noStroke();
        ellipse(coords[0], coords[1], 5, 5);
        ellipse(coords[2], coords[3], 5, 5);
        pt.setLocation(coords[4], coords[5]);
        stroke(255);
        line(dx, dy, pt.x, pt.y);
      }
    }
  }
}

/**
 * Builds a document for the shapes we created.
 */
public void createDocument() {
  document = new DocumentComponent("Blobs");
  // get lots of feedback as we save
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("IgnoStudio");
  document.setWidth(width);
  document.setHeight(height);
  // get the palette for the document
  Palette pal = document.getPalette();
  // add default black, white and gray colors
  pal.addBlackWhiteGray();
  // now add each of our fill colors. 
  // Palette is a set (LinkedHashSet) that will not repeat a color.
  for (BezShape bez : shapes) {
    pal.addColor(bez.fillColor());
  }
  // create a layer
  LayerComponent shapeLayer = new LayerComponent("Shapes");
  // background rectangle
  BezRectangle rect = BezRectangle.makeLeftTopWidthHeight(0, 0, width, height);
  rect.setFillColor(bgFillColor);
  // add background rectangle and shapes to layer
  shapeLayer.add(rect);
  shapeLayer.add(shapes);
  // add layer to document 
  document.add(shapeLayer);
}

/**
 * Saves a document in Adobe Illustrator 7.0 format, transforming it into AI coordinate system.
 * @param aiFilename   name of the file
 * @param doc          a DocumentComponent to save
 */
private void saveAI(String aiFilename, DocumentComponent doc) {
  PrintWriter output = createWriter(aiFilename);
  // In IgnoCodeLib 0.3, write automatically applies a geometric transform 
  // (reflect on horizontal axis) so that the saved file has the same orientation
  // as the Processing display. Call writeNoTransform to write without the transform.
  document.write(output);
}


/************* Comparator classes, used for sorting shapes *************/

/**
 * sorts shapes by center point increasing on x-axis
 */
public class CompareX implements Comparator <BezShape> {

  public int compare(BezShape sh1, BezShape sh2) {
    float x1 = sh1.xctr();
    float y1 = sh1.yctr();
    float x2 = sh2.xctr();
    float y2 = sh2.yctr();
    if (x1 < x2) return -1;
    if (x1 > x2) return 1;
    if (y1 < y2) return -1;
    if (y1 > y2) return 1;
    return 0;
  }
}


/**
 * sorts shapes by center point increasing on y-axis
 */
public class CompareY implements Comparator <BezShape> {

  public int compare(BezShape sh1, BezShape sh2) {
    float x1 = sh1.xctr();
    float y1 = sh1.yctr();
    float x2 = sh2.xctr();
    float y2 = sh2.yctr();
    if (y1 < y2) return -1;
    if (y1 > y2) return 1;
    if (x1 < x2) return -1;
    if (x1 > x2) return 1;
    return 0;
  }
}


/**
 * Sorts shapes based on distance of center from initial point;
 * farthest points will come before nearest points.
 */
public class CompareCtr implements Comparator <BezShape> {
  float xctr = 0;
  float yctr = 0;

  public CompareCtr(float xctr, float yctr) {
    this.xctr = xctr;
    this.yctr = yctr;
  }

  public int compare(BezShape sh1, BezShape sh2) {
    float x1 = sh1.xctr();
    float y1 = sh1.yctr();
    float x2 = sh2.xctr();
    float y2 = sh2.yctr();
    // compare squares of distances
    float d1 = (x1 - xctr) * (x1 - xctr) + (y1 - yctr) * (y1 - yctr);
    float d2 = (x2 - xctr) * (x2 - xctr) + (y2 - yctr) * (y2 - yctr);
    if (d1 > d2) return -1;
    if (d1 < d2) return 1;
    return 0;
  }
}


/**
 * Compares angles two points make with intial point.
 */
public class CompareAng implements Comparator <BezShape> {
  float xctr = 0;
  float yctr = 0;

  public CompareAng(float xctr, float yctr) {
    this.xctr = xctr;
    this.yctr = yctr;
  }

  public int compare(BezShape sh1, BezShape sh2) {
    float x1 = sh1.xctr();
    float y1 = sh1.yctr();
    float x2 = sh2.xctr();
    float y2 = sh2.yctr();
    float ang1 = getAngle(x1, y1);
    float ang2 = getAngle(x2, y2);
    if (ang1 > ang2) return -1;
    if (ang1 < ang2) return 1;
    float d1 = (x1 - xctr) * (x1 - xctr) + (y1 - yctr) * (y1 - yctr);
    float d2 = (x2 - xctr) * (x2 - xctr) + (y2 - yctr) * (y2 - yctr);
    if (d1 > d2) return -1;
    if (d1 < d2) return 1;
    return 0;
  }

  float getAngle(float x, float y) {
    // return the angle of rotation measured clockwise from 0
    // degrees in radians
    double ang;
    float dx = x - xctr; 
    float dy = y - yctr;
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
}

public boolean pointInShape(BezShape blob, float x, float y) {
  BezRectangle b = (blob.boundsRect());
  if (b.containsPoint(x, y)) {
    if (blob.containsPoint(x, y)) {
      return true;
    }
  }
  return false;
}
