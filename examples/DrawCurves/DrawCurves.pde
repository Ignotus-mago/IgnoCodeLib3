/**
 * March 14, 2012 3:35:47 PM CDT
 * Sample code for IgnoCodeLib 0.3 Processing library by Paul Hertz
 * Not compatible with earlier versions.
 * Updated June 25, 2013 for IgonoCodeLib 0.3 release.
 * Demo applet that provides interactive creation of a general BezShape. 
 * A BezShape may consist of both curved and straight line segments.
 * See the mouseReleased method for code that adds a curve or a line segment to a shape.
 * In this demo, continuity of curvature on either side of an anchor point
 * is achieved by making the anchor point and its two control points collinear.
 * Press and drag to draw curve segments, click to draw line segments.
 * Click in last anchor point or press TAB key to finish a shape.
 * Click on first anchor point to close and finish a shape.
 * I'm sure this can be done more elegantly, as a full-fledged interactive 
 * Bezier curve editor, but I trust it gets the idea across.
 */
 
import net.paulhertz.aifile.*;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ListIterator;

int bgFillColor;
/** array to hold shapes */
ArrayList<BezShape> shapes;
/** flag whether to show or hide control points and blob skeleton */
boolean showSkeleton = false;
/** document to save */
DocumentComponent document;
/**  */
float pressX;
float pressY;
float dragX;
float dragY;
float releaseX;
float releaseY;
boolean firstPoint = false;
boolean dragging = false;
boolean outsideAnchor = false;
float startX;
float startY;
/** BezShape to accumulate points */
BezShape newBez;
// curve creation controls
BezRectangle startRect;
BezRectangle anchor1;
BezRectangle anchor2;
BezEllipse control1;
BezEllipse control2;
BezEllipse control3;
int segmentType;
final static int LINE = 0;
final static int CURVE = 1;
final static int HALF_LINE = 2;
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(640, 480);
  smooth();
  bgFillColor = color(255);
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  shapes = new ArrayList<BezShape>();
  initControls();
  printHelp();
}

public void printHelp() {
  println("Press and drag to draw curve segments, click to draw line segments.");
  println("Click in last anchor point or press TAB key to finish a shape.");
  println("Click on first anchor point to close and finish a shape.");
  println("Press 'r' to rotate shapes.");
  println("Type 'h' to show this help message.");
}

public void initControls() {
  fill(255);
  stroke(127);
  strokeWeight(1);
  startRect = BezRectangle.makeCenterWidthHeight(0, 0, 5, 5);
  anchor1 = BezRectangle.makeCenterWidthHeight(0, 0, 5, 5);
  anchor2 = BezRectangle.makeCenterWidthHeight(0, 0, 5, 5);
  noStroke();
  fill(127);
  control1 = BezEllipse.makeCenterWidthHeight(0, 0, 4, 4);
  control2 = BezEllipse.makeCenterWidthHeight(0, 0, 4, 4);
  control3 = BezEllipse.makeCenterWidthHeight(0, 0, 4, 4);
  control3.setFillColor(color(233, 0, 0));
  hideControls();
}

public void hideControls() {
  startRect.hide();
  anchor1.hide();
  anchor2.hide();
  control1.hide();
  control2.hide();
  control3.hide();
}

public void draw() {
  background(bgFillColor);
  for (BezShape bezzy: shapes) {
    bezzy.draw();
  }
  if (null != newBez) newBez.draw();
  if (firstPoint) {
    stroke(127);
    strokeWeight(1);
    line(anchor1.xctr(), anchor1.yctr(), control1.xctr(), control1.yctr());
    anchor1.draw();
    control1.draw();
  }
  else if (null != newBez) {
    stroke(127);
    strokeWeight(1);
    if (dragging) {
      line(anchor1.xctr(), anchor1.yctr(), control1.xctr(), control1.yctr());
      line(anchor2.xctr(), anchor2.yctr(), control3.xctr(), control3.yctr());
      line(anchor2.xctr(), anchor2.yctr(), control2.xctr(), control2.yctr());
      bezier(anchor1.xctr(), anchor1.yctr(),control1.xctr(), control1.yctr(), 
          control2.xctr(), control2.yctr(),anchor2.xctr(), anchor2.yctr());
    }
    else {
      line(anchor1.xctr(), anchor1.yctr(), control1.xctr(), control1.yctr());
      line(anchor2.xctr(), anchor2.yctr(), control3.xctr(), control3.yctr());
      line(anchor2.xctr(), anchor2.yctr(), control2.xctr(), control2.yctr());
    }
    anchor1.draw();
    control1.draw();
    anchor2.draw();
    control2.draw();
    control3.draw();
  }
}


public void mousePressed() {
  pressX = mouseX;
  pressY = mouseY;
  println("pressX = "+ pressX +", pressY = "+ pressY);
  if (null == newBez) {
    startX = pressX;
    startY = pressY;
    startRect.moveTo(pressX, pressY);
    // move anchor point 1 to mouse location and show it
    anchor1.moveTo(pressX, pressY);
    anchor1.show();
    // move control point 1 to mouse location but don't show it
    control1.moveTo(pressX, pressY);
    // the first point is part of a line segment, initially
    segmentType = LINE;
    firstPoint = true;
  }
  else {
    if (anchor1.containsPoint(pressX, pressY)) {
      finishShape();
      println("curve ended");
      dragging = false;
      outsideAnchor = false;
      return;
    }
    if (startRect.containsPoint(pressX, pressY)) {
      newBez.append(startX, startY);
      newBez.setIsClosed(true);
      finishShape();
      println("curve ended - closed");
      dragging = false;
      outsideAnchor = false;
      return;
    } 
    // we're adding a new point
    anchor2.moveTo(pressX, pressY);
    anchor2.show();
    control2.moveTo(pressX, pressY);
    control3.moveTo(pressX, pressY);
    if (LINE == segmentType) {
      // if the previous segment type was LINE, set the current one to LINE, too
      segmentType = LINE;
    }
    else if (HALF_LINE == segmentType) {
      // if the previous segment type was a HALF_LINE, set the current one to LINE
      segmentType = LINE;
    }
    else {
      // if the previous segment type was a CURVE, set the current one to HALF_LINE
      segmentType = HALF_LINE;
    }
  }
  dragging = false;
  outsideAnchor = false;
}

public void mouseDragged() {
  dragX = mouseX;
  dragY = mouseY;
  // outsideAnchor is set to true the first time we drag outside the anchor
  // and remains true until we create a new anchor point
  if (firstPoint) {
    if (!outsideAnchor) {
      if (!anchor1.containsPoint(dragX, dragY)) {
        outsideAnchor = true;
        control1.moveTo(dragX, dragY);
        control1.show();
        segmentType = CURVE;
        println("dragX = "+ dragX +", dragY = "+ dragY);
        dragging = true;
      }
    }
    else {
      control1.moveTo(dragX, dragY);
    }
  } 
  else {
    if (!outsideAnchor) {
      if (!anchor2.containsPoint(dragX, dragY)) {
         // Continuity of curvature on either side of an anchor point is achieved
         // by making the anchor point and its two control points collinear.
        outsideAnchor = true;
        control3.moveTo(dragX, dragY);
        control3.show();
        float dx = dragX - anchor2.xctr();
        float dy = dragY - anchor2.yctr();
        control2.moveTo(anchor2.xctr() - dx, anchor2.yctr() - dy);
        control2.show();
        segmentType = CURVE;
        println("dragX = "+ dragX +", dragY = "+ dragY);
        dragging = true;
      }
    }
    else {
      control3.moveTo(dragX, dragY);
      control3.show();
      float dx = dragX - anchor2.xctr();
      float dy = dragY - anchor2.yctr();
      control2.moveTo(anchor2.xctr() - dx, anchor2.yctr() - dy);
    }
  }
}

public void mouseReleased() {
  releaseX = mouseX;
  releaseY = mouseY;
  if (firstPoint) {
    newBez = new BezShape(pressX, pressY);
    newBez.setStrokeColor(0);
    newBez.setNoFill();
    newBez.setWeight(1);
    newBez.setIsClosed(false);
    firstPoint = false;
  }
  else if (null != newBez) {
    if (CURVE == segmentType) {
      // for clarity, we'll create some variables, though we could just call:
      // newBez.append(control1.xctr(), control1.yctr(), control2.xctr(), control2.yctr(), anchor2.xctr(), anchor2.yctr());
      float cx1 = control1.xctr();
      float cy1 = control1.yctr();
      float cx2 = control2.xctr();
      float cy2 = control2.yctr();
      float x = anchor2.xctr();
      float y = anchor2.yctr();
      // append two control points and an achor point for a curve
      newBez.append(cx1, cy1, cx2, cy2, x, y);
      println("-- appended a CURVE segment to path");
    }
    else if (LINE == segmentType || HALF_LINE == segmentType) {
      float x = anchor2.xctr();
      float y = anchor2.yctr();
      // append an anchor point for a straight line
      newBez.append(x, y);
      println("-- appended a LINE segment to path");
    }
    else {
      // error!
    }
    anchor1.moveTo(anchor2.xctr(), anchor2.yctr());
    control1.moveTo(control3.xctr(), control3.yctr());
  }
  dragging = false;
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
  if (key == TAB) {
    finishShape();
    println("curve ended");
  }
  else if (key == 's' || key == 'S') {
    createDocument();
    saveAI("drawcurves.ai", document);
  }
  else if (key == 'h' | key == 'H') {
    printHelp();
  }
}

public void finishShape() {
  // set the center of transformation of the shape to the center
  // of its bounding rectangle, otherwise it would be at 0,0
  newBez.calculateCenter();
  // we add the current shape, newBez, to the shapes array
  shapes.add(newBez);
  // we set newBez to null, to get ready to draw a new shape
  // because we have a reference to the shape in the shapes array, it is not lost. 
  newBez = null;
  hideControls();
}

/**
 * Draws a BezShape. If showSkeleton is true, steps through the vertices 
 * of the BezShape and draws anchor and control points.
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
 * The document can act as a display list. We just have to call its draw() or write()
 * methods to draw it to the screen or write it to a file. 
 */
public void createDocument() {
  document = new DocumentComponent("Draw Curves");
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
  // In IgnoCodeLib 0.3, write(output) now performs a transform
  // to make the saved file's orientation identical to the display, the same as
  // writeWithAITransform. Call writeNoTransform to omit the transform.
  document.write(output);
}


