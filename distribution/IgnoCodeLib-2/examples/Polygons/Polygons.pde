/**
 * for IgnoCodeLib version 0.3.x and above
 * January 29, 2012 3:54:19 PM CST
 * Updated June 25, 2013 for IgonoCodeLib 0.3 release, not compatible with earlier versions.
 * Demonstrates calls for creating polygons. Shows how to drag a shape and snap it 
 * to a grid. Demonstrates calls to {@code makeCurrentStateVisitor()} and {@code makeBoundsCalculationVisitor()}.
 */


import net.paulhertz.aifile.*;
import net.paulhertz.util.*;
import java.util.*;
import java.io.PrintWriter;


/** irregular polygon list */
public ArrayList<BezShape> irregulars;
/** regular polygon list */
public ArrayList<BezShape> regulars;
/** grid points list */
public ArrayList<BezShape> grid;
/** random number utility */
public RandUtil rando;
/** how many grid steps horizontally */
int hzSteps = 8;
/** how many grid steps vertically */
int vtSteps;
/** size of a step in pixels */
float step;
/** background fill color */
int bgFillColor = Palette.composeColor(127);
/** shape that is selected for dragging */
BezShape selectedShape;
/** true if we should snap dragged shapes to the grid */
boolean snapToGrid = false;
/** the document for saving shapes */
public DocumentComponent document;
/** layer for shapes */
LayerComponent shapeLayer;
/** layer for grid */
LayerComponent gridLayer;
/** group for regular polygons */
GroupComponent regularGroup;
/** group for irregular */
GroupComponent irregularGroup;
/** group for grid points (circles) */
GroupComponent gridGroup;
/** bounding box for irregular shapes */
BezRectangle box;
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(800, 600);
  smooth();
  // calculate grid steps
  step = width/hzSteps;
  vtSteps = (int) Math.floor(height/step);
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  // initialize arrays of BezShapes
  irregulars = new ArrayList<BezShape>();
  regulars = new ArrayList<BezShape>();
  grid = new ArrayList<BezShape>();
  // create a random number generator
  rando = new RandUtil();
  // create polygons and put them into arrays
  createPolys();
  createRegularPolys();
  // create the vidible grid
  createGrid();
  // create the document and build its layers
  createDocument();
  printHelp();
}

void printHelp() {
  // message to the user
  println("\nType 's' to save");
  println("Press and drag with the mouse to move a polygon");
  println("Hold down shift key to snap polygon to grid");
  println("Type 'x' or 'z' to change appearance of grid");
  println("Type 'v' or 'V' to show/hide grid");
  println("Type 'b' to show bounds of irregular polygons.");
  println("Type 'h' to print this help message.");
}


public void draw() {
  background(bgFillColor);
  // this is one way to draw:
  // step through lists and tell each shape to draw itself
  /*
  for (BezShape sh : regulars) {
    sh.draw();
  }
  for (BezShape sh : irregulars) {
    sh.draw();
  }
  for (BezShape sh : grid) {
    sh.draw();
  }
  */
  // but since we have put all the geometry into the document, 
  // we can draw it with one simple call
  document.draw();
  // if there is a shape selected, move the it the distance between the 
  // current mouse position and the previous mouse position
  if (mousePressed) {
    if (null != selectedShape) {
      float tx = mouseX - pmouseX;
      float ty = mouseY - pmouseY;
      selectedShape.translateShape(tx, ty);
    }
  }
}


/* We use this overridden method to check if the mouse is pressed inside a shape.
 * We do this by stepping through the lists to see if the mouse point is inside one of them.
 * It's a brute force approach, but with so few shapes it will work quickly enough.
 */
public void mousePressed() {
  float x = mouseX;
  float y = mouseY;
  for (BezShape sh : regulars) {
    if (sh.containsPoint(x, y)) {
      selectedShape = sh;
      return;
    }
  }
  for (BezShape sh : irregulars) {
    if (sh.containsPoint(x, y)) {
      selectedShape = sh;
      return;
    }
  }
  selectedShape = null;
}


/* We use this overridden method to decide if we should relocate a shape
 * and if it should snap to the grid or not.
 */
public void mouseReleased() {
  snapToGrid = false;
  // check if the shift key is down
  if (keyPressed && key == CODED) {
    if (keyCode == SHIFT) {
      snapToGrid = true;
    }
  }
  // if a shape was selected and snapToGrid is true, snap it to grid
  if (null != selectedShape) {
    if (snapToGrid) {
      if (selectedShape.bezType() == BezShape.BezType.BEZ_REGULAR_POLY) {
        // snap center to grid
        LineVertex pt = selectedShape.centerVertex();
        LineVertex snap = closestGridPoint(pt);
        selectedShape.translateShape(snap.x() - pt.x(), snap.y() - pt.y());
      }
      else if (selectedShape.bezType() == BezShape.BezType.BEZ_POLY) {
        // snap start point to grid
        LineVertex pt = selectedShape.startVertex();
        LineVertex snap = closestGridPoint(pt);
        selectedShape.translateShape(snap.x() - pt.x(), snap.y() - pt.y());
      }
    }
  }
  snapToGrid = false;
}


/*  We use this overridden method to catch key presses used as commands.
 */
public void keyReleased() {
  if (key == 's' || key == 'S') {
    // save file, put shapes in two layers
    String filename = "Polygons.ai";
    saveAI(filename, document);
    println("saved" + "");
  }
  else if (key == 'x' || key == 'X') {
    // use a visitor to change the grid group's attributes to a random fill color and a white stroke
    fill(Palette.randColor());
    stroke(255);
    strokeWeight(0.5f);
    ShapeAttributeVisitor visitor = ShapeAttributeVisitor.makeCurrentStateVisitor(this);
    gridGroup.accept(visitor);
  }
  else if (key == 'z' || key == 'Z') {
    // use a visitor to change the grid group's attributes to no stroke and fill of 240
    noStroke();
    fill(240);
    ShapeAttributeVisitor visitor = ShapeAttributeVisitor.makeCurrentStateVisitor(this);
    gridGroup.accept(visitor);
  }
  else if (key == 'v' || key == 'V') {
    gridGroup.setVisible(!gridGroup.isVisible());
  }
  else if (key == 'b' || key == 'B') {
    // calculate irregular polygons bounding box with a visitor and show it
    BoundsCalculationVisitor visitor = BoundsCalculationVisitor.makeBoundsCalculationVisitor();
    irregularGroup.accept(visitor);
    if  (null != box) {
      boolean gone = document.getDefaultLayer().remove(box); 
      println("removed box = "+ gone);
    }
    box = visitor.bounds();
    box.setNoFill();
    box.setWeight(1.5f);
    box.setStrokeColor(Palette.composeColor(255));
    box.setStrokeOpacity(127);
    document.add(box);
  }
  else if (key == 'h' || key == 'H') {
    printHelp();
  }
}


/**
 * create some regular polygons
 */
private void createRegularPolys() {
  int totalPolys = hzSteps;
  int[] sidesValues = new int[totalPolys];
  for (int i = 0; i < totalPolys; i++) {
    sidesValues[i] = 3 + i;
  }
  rando.shuffle(sidesValues);
  float halfstep = 0.5f * step;
  float radius = step * 0.4f;
  float xctr;
  float yctr = step + halfstep;
  for (int i = 0; i < totalPolys; i++) {
    xctr = halfstep + i * step;
    stroke(Palette.randColor());
    fill(Palette.randColor());
    strokeWeight(rando.randomInRange(1, 5));
    BezRegularPoly regPoly = BezRegularPoly.makeCenterRadiusSides(xctr, yctr, radius, sidesValues[i]);
    regulars.add(regPoly);
  }
}

/**
 * create some irregular polygons that use grid points
 * we do this by first creating a list of grid points and then 
 * copying points from it to the polygons. 
 */
private void createPolys() {
  // our grid is shifted a half step over and down
  float halfstep = 0.5f * step;
  float xctr;
  float yctr;
  ArrayList<LineVertex> points = new ArrayList<LineVertex>();
  // just list grid points below the row of regualr polygons
  for (int column = 2; column < vtSteps; column++) {
    yctr = halfstep + column * step;
    for (int row = 0; row < hzSteps; row++) {
      xctr = halfstep + row * step;
      points.add(new LineVertex(xctr, yctr));
    }
  }
  fill(32, 0, 216);
  strokeWeight(2);
  stroke(Palette.randColor());
  // a list to store our polygon points in
  ArrayList<LineVertex> poly = new ArrayList<LineVertex>();
  // put some points into the list
  // avoid trouble: clone the points, do not pass references to them
  // note that first and last points must be the same
  poly.add(points.get(0).clone());
  poly.add(points.get(1).clone());
  poly.add(points.get(hzSteps).clone());
  poly.add(points.get(0).clone());
  irregulars.add(BezPoly.makePolyLineVertex(poly));
  // clear the list and add some new points to it
  poly.clear();
  fill(216, 80, 32);
  stroke(Palette.randColor());
  poly.add(points.get(hzSteps + 2).clone());
  poly.add(points.get(hzSteps + hzSteps + 3).clone());
  poly.add(points.get(hzSteps + 4).clone());
  poly.add(points.get(hzSteps + 3).clone());
  poly.add(points.get(3).clone());
  poly.add(points.get(2).clone());
  poly.add(points.get(hzSteps + 2).clone());
  irregulars.add(BezPoly.makePolyLineVertex(poly)); 
  // clear the list and add some new points to it
  poly.clear();
  fill(216, 199, 55);
  stroke(Palette.randColor());
  poly.add(points.get(2 * hzSteps).clone());
  poly.add(points.get(hzSteps + 1).clone());
  poly.add(points.get(2 * hzSteps + 2).clone());
  poly.add(points.get(2 * hzSteps + 1).clone());
  poly.add(points.get(3 * hzSteps + 2).clone());
  poly.add(points.get(3 * hzSteps + 1).clone());
  poly.add(points.get(2 * hzSteps).clone());
  irregulars.add(BezPoly.makePolyLineVertex(poly)); 
  poly.clear();
  // get a deep copy of the last polygon we added
  BezShape sh = irregulars.get(irregulars.size() - 1).clone();
  // call calculateCenter to make all transforms operate on the center of the shape
  sh.calculateCenter();
  sh.rotateShape(PApplet.PI);
  sh.translateShape(4 * step, 0);
  irregulars.add(sh); 
}

/**
 * Creates a regular grid.
 */
private void createGrid() {
  // create a regular grid
  float halfstep = 0.5f * step;
  float xctr;
  float yctr;
  noStroke();
  fill(240);
  for (int i = 0; i < vtSteps; i++) {
    yctr = halfstep + i * step;
    for (int j = 0; j < hzSteps; j++) {
      xctr = halfstep + j * step;
      BezCircle circ = BezCircle.makeCenterRadius(xctr, yctr, 2);
      grid.add(circ);
    }
  }
}


/**
 * Returns the closest grid point to a supplied LineVertex (i.e., a point)
 * @param vtx   a point stored as a LineVertex
 * @return      closet grid point as a LineVertex
 */
public LineVertex closestGridPoint(LineVertex vtx) {
  double halfstep = step/2.0;
  // get nearest x in a halfstep grid to the left
  double x = Math.floor(vtx.x() / halfstep) * halfstep;
  // get nearest y in a halfstep grid to the top
  double y = Math.floor(vtx.y() / halfstep) * halfstep;
  int mx = (int) (x / halfstep) % 2;
  int my = (int) (y / halfstep) % 2;
  // if mx or my is even, that indicates that x or y is not on the grid
  // but at a position half a step away from the grid
  if (0 == mx) {
    x += halfstep;
  }
  if (0 == my) {
    y += halfstep;
  }
  return new LineVertex((float) x, (float) y);
}


/**
 * Builds a document hierarchy with layers and groups for the lists of shapes we created.
 * The document will act as a display list. We just have to call its draw() or write()
 * methods to draw it to the screen or write it to a file. 
 */
public void createDocument() {
  document = new DocumentComponent("Regular and Irregular Polygons");
  // get lots of feedback as we save
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("IgnoStudio");
  document.setWidth(width);
  document.setHeight(height);
  Palette pal = document.getPalette();
  pal.addBlackWhiteGray();
  shapeLayer = new LayerComponent("Shapes");
  BezRectangle rect = BezRectangle.makeLeftTopWidthHeight(0, 0, width, height);
  rect.setFillColor(bgFillColor);
  shapeLayer.add(rect);
  regularGroup = new GroupComponent(this);
  regularGroup.add(regulars);
  shapeLayer.add(regularGroup);
  irregularGroup = new GroupComponent(this);
  irregularGroup.add(irregulars);
  shapeLayer.add(irregularGroup);
  document.add(shapeLayer);
  gridLayer = new LayerComponent("Grid");
  gridGroup = new GroupComponent();
  gridGroup.add(grid);
  gridLayer.add(gridGroup);
  // lock the gridLayer 
  gridLayer.setLocked(true);
  document.add(gridLayer);
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


