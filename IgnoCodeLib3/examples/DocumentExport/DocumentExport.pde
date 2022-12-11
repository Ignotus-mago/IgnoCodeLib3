/**
 * January 29, 2012 3:03:31 PM CST
 * Sample code for IgnoCodeLib 0.3 Processing library by Paul Hertz
 * Updated June 25, 2013 for IgonoCodeLib 0.3 release.
 * The writeNoTransform call is only available in 0.3 and the packages have been renamed
 * from com.ignotus to net.paulhertz in the import section. Calls to component constructors
 * no longer require you to pass in a reference to the host PApplet, if you initialize
 * the library correctly as in "igno = new IgnoCodeLib(this);"
 * Shows how to create a document tree with layers, groups, and shapes and use it to draw 
 * to the screen, perform a global transform, and export to file. 
 */

import java.util.*;
import java.io.*;  
import java.awt.geom.Point2D;
import net.paulhertz.aifile.*;   // library that handles Adobe Illustrator document structure and export
import net.paulhertz.geom.*;     // library for geometric transforms and matrices

/** list of colors */
ArrayList<Integer> farben;
/** a geometric transform */
Matrix3 aiTransform;
String fileName = "documentExport.ai";
/** document component, root of the document structure */
DocumentComponent doc;
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(480,720);
  background(127);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  farben = new ArrayList<Integer>();
  setupTransform();
  // we'll create the document up front and use it for drawing and saving
  createDocument();
  println("type 's' to output file");
}


public void draw() {
  // the document knows how to draw itself--so do layers and groups and shapes, but if they are 
  // part of the document, we just have to tell the document to draw.
  doc.draw();
}


/**
 * Processing and Illustrator use different coordinate systems. We can move from one to 
 * the other by applying a geometric transform to our graphics before outputting to Illustrator.
 * The DocumentComponent class now performs the transform automatically when you call write, so 
 * this method is here for informational purposes. 
 * See the SaveAI() method for more information.
 * 2-D geometric transforms can be captured by a matrix with three rows and three columns.
 * The class net.paulhertz.geom.Matrix3 handles transforms with such matrices.
 * In its initial state, a Matrix3 is the "identity matrix," to which we add various transforms.
 * The matrix transforms 2D geometry through the operation of matrix multiplication. The
 * nuts and bolts need not concern you: the essential thing to understand is that you can 
 * add a series of geometric transforms to the matrix and then execute them all at once.
 * The matrix is also referred to as the CTM or Current Transoformation Matrix.
 * This transform is also available directly from the document through the getAITransform() method.
 */
public void setupTransform() {
  // start with the identity matrix
  aiTransform = new Matrix3();
  // add a horizontal reflection around x = 0
  aiTransform.scaleCTM(1.0, -1.0);
  // and translate by "height" distance on the y-axis
  aiTransform.translateCTM(0, height);
}

/**
 * Creates a document tree with layers, groups, and shapes 
 */
public void createDocument() {
  float x, y, radius, h, w;
  int sides;
  color c;
  // initialize an integer array with values constrained to 0..255
  int[] channelValues = {34, 55, 89, 144, 233};
  float minX = width * 0.125;
  float maxX = width * 0.875;
  float minY = height * 0.125;
  float maxY = height * 0.875;
  // create a document
  doc = new DocumentComponent("Layers and Groups");
  // by setting verbose to true, we get some feedback as we add components
  doc.setVerbose(true);
  // create and add a layer
  LayerComponent bg = new LayerComponent("background");
  doc.add(bg);
  // add some geometry to the layer
  bg.add(bgRect());
  // create another layer and add it to the document
  LayerComponent polygons = new LayerComponent("Polygons");
  doc.add(polygons);
  // create some geometry and add it to the polygons layer
  ArrayList<BezShape> bezzies = new ArrayList<BezShape>();
  for (int i = 0; i < 10; i++) {
    x = random(minX, maxX);
    y = random(minY, maxY);
    radius = random(2, 48);
    sides = int(random(3, 18));
    BezShape bez = BezRegularPoly.makeCenterRadiusSides(x, y, radius, sides);
    c = Palette.randColor(channelValues);
    farben.add(c);
    bez.setFillColor(c);
    bez.setNoStroke();
    bezzies.add(bez);
    polygons.add(bez);
  }
  // create a group and add it to the polygons layer
  GroupComponent boxes = new GroupComponent(this);
  polygons.add(boxes);
  // step through the polygons we created and put a bounding box around each one
  // add the bounding boxes to the group
  for (BezShape b : bezzies) {
    float[] bounds = b.bounds(this);
    float left = bounds[0];
    float top = bounds[1];
    float right = bounds[2];
    float bottom = bounds[3];
    color s = b.fillColor();
    BezShape br = bezRect(left, top, right, bottom, color(0), s);
    br.setNoFill();
    br.setWeight(2);
    boxes.add(br);
  }
  // create a new layer and add it to the document 
  LayerComponent ellipses = new LayerComponent("Ellipses");
  doc.add(ellipses);
  // add some geometry to the layer
  for (int i = 0; i < 10; i++) {
    x = random(minX, maxX);
    y = random(minY, maxY);
    w = random(16, 96);
    h = random(16, 96);
    sides = int(random(4, 7));
    BezShape bez = BezEllipse.makeCenterWidthHeightSectors(x, y, w, h, sides);
    c = Palette.randColor(channelValues);
    farben.add(c);
    bez.setFillColor(c);
    bez.setNoStroke();
    ellipses.add(bez);
  }
}


void keyPressed() {
  if (key == 's') {
    saveAI(fileName, doc, farben);
  }  
}


/**
 * Creates a rectangle from four points, with fill and stroke colors
 */
public BezShape bezRect(float left, float top, float right, float bottom, color f, color s) {
  // instatiate a BezShape with a reference to our PApplet
  //  and the x and y coordinates of its starting vertex.
  BezShape r = new BezShape(left, top);
  r.setFillColor(f);
  r.setStrokeColor(s);
  r.setWeight(3.0);
  r.append(right, top);
  r.append(right, bottom);
  r.append(left, bottom);
  r.append(left, top);
  return r;
}


public BezShape bgRect() {
  color f = color(233, 220, 254);
  color s = color(21, 34, 55);
  return bezRect(0, height, width, 0, f, s);
}


/**
 * saves shapes to an Adobe Illustrator file
 */
public void saveAI(String aiFilename, DocumentComponent doc, ArrayList<Integer> colors) {
  println("saving Adobe Illustrator file " + aiFilename + "...");
  PrintWriter output = createWriter(aiFilename);
  Palette pal = doc.getPalette();
  pal.addBlackWhiteGray();
  pal.addColors(colors);
  doc.setCreator("Ignotus");
  doc.setOrg("IgnoStudio");
  doc.setWidth(width);
  doc.setHeight(height);
  // the transform we created reflects everything around a horizontal line
  // it's a "symmetrical" transform: doing it a second time undoes it.
  // we do it once to flip graphics into Adobe Illustrator's coordinate system, 
  // and once more to restore Processing's coordinate system.
  // the new version of IgnoCodeLib (0.2) provides a single method, writeWithAITransform()
  // that creates and applies the Processing to Illustrator coordinate system transform.
  // "doc.writeWithAITransform(output)" could replace the next three lines, and you would 
  // not need to generate aiTransform.
  // As of IgnoCodeLib 0.3, write(output) performs the transform for you. The writeWithAITransform
  // method is superfluous and will be deprecated in the future. Use writeNoTransform if you
  // don't want the default transform applied.
  /*
  // the old way...or what to do if you want your own transform applied (however, it modifies 
  // the document geometry, so be prepared to run an inverse transform). 
  doc.transform(aiTransform);
  doc.writeNoTransform(output);
  doc.transform(aiTransform);
  */
  doc.write(output); 
}



