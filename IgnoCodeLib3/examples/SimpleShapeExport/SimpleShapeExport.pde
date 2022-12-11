/*
 * Sample code for IgnoCodeLib 0.3 Processing library by Paul Hertz.
 * Updated November 16, 2021, for IgonoCodeLib 0.3 revision.
 * Demonstrates the basics of creating BezShapes and loading them into a list.  
 * Steps through the list to draw shapes to the display or write them to a file.
 * Demonstrates methods for creating colors and adding them to a palette that can
 * be used in Processing and exported to an Illustrator file.  
 * Note that Illustrator and Processing use different coordinate systems.
 * The DocumentComponent.write() method handles the coordinate system transform for you.
 */

import java.util.*;
import java.io.*;  
import net.paulhertz.aifile.*;   // library that defines AIFileWriter and static file i/o methods
import net.paulhertz.geom.*;   


/** a list of shapes to draw and save */
ArrayList<BezShape> shapes;
/** a palette of colors, stored as 32-bit integers */
ArrayList<Integer> farben;
/** the file we'll save to */
String fileName = "simple.ai";
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(480,720);
  background(127);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  createColors();
  createShapes();
  println("type ' ' to generate new shapes");
  println("type 's' to output file");
}


/**
 * To draw, we just step through the shapes and tell each one to draw, passing
 * in a reference to the Processing PApplet "this" so that the shapes have access
 * to Processing's drawing functions. 
 */
public void draw() {
  for (BezShape bez : shapes) {
     bez.draw(); 
  }
}


void keyPressed() {
  if (key == 's' || key == 'S') {
    saveAI(fileName, shapes, farben);
  }  
  else if (key == ' ') {
    createShapes();
  }
}


/**
 * Add some colors to the farben array. The static method Palette.colorPermutation
 * returns an array of all the unique permutations of the r, g and b color volues.
 */
public void createColors() {
  farben = new ArrayList();
  color c = color(32, 64, 128);
  farben.add(c);
  c = color(76, 123, 199);
  color[] perm = Palette.colorPermutation(c);
  for (color pc : perm) {
    farben.add(pc);
  }
}


/**
 * We create some shapes
 */
public void createShapes() {
  float x, y, radius, h, w;
  int sides;
  color c;
  int[] channelValues = {34, 55, 89, 144, 233};
  // add a background rectangle
  shapes = new ArrayList<BezShape>();
  shapes.add(bgRect());
  for (int i = 0; i < 10; i++) {
    x = random(width);
    y = random(height);
    radius = random(2, 48);
    sides = int(random(3, 18));
    // call static method BezShape.bezRegularPoly to create a regular polygon
    BezShape bez = BezRegularPoly.makeCenterRadiusSides(x, y, radius, sides);
    // generate a color using randomly selected values from an array for r, g and b
    c = Palette.randColor(channelValues);
    farben.add(c);
    bez.setFillColor(c);
    bez.setStrokeColor(0);
    shapes.add(bez);
  }
  for (int i = 0; i < 10; i++) {
    x = random(width);
    y = random(height);
    w = random(16, 96);
    h = random(16, 96);
    sides = int(random(4, 7));
    // call static method BezShape.bezEllipse to create an ellipse
    BezShape bez = BezEllipse.makeCenterWidthHeightSectors(x, y, w, h, sides);
    c = Palette.randColor(channelValues);
    farben.add(c);
    bez.setFillColor(c);
    bez.setNoStroke();
    shapes.add(bez);
  }
}


/**
 * Creates a rectangle from four points, with fill and stroke colors
 */
public BezShape bezRect(float left, float top, float right, float bottom, color f, color s) {
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
  color f = color(233, 231, 236);
  color s = color(21, 34, 55);
  return bezRect(0, height, width, 0, f, s);
}


 /**
  * saves shapes to an Adobe Illustrator file
  * Illustrator and Porcessing use different coordinate systems, so the AI file
  * will look the Processing window flipped on its horizontal axis.
  * @param aiFilename   name fo the file to save to
  * @param comps        an array of BezShapes to save to the file
  * @param colors       an array of Processing colors to use as the palette
  */
public void saveAI(String aiFilename, ArrayList<BezShape> comps, ArrayList<Integer> colors) {
  println("saving Adobe Illustrator file " + aiFilename + "...");
  PrintWriter output = createWriter(aiFilename);
  // create the document
  DocumentComponent doc = new DocumentComponent("Simple Shape Export");
  // get its palette
  Palette pal = doc.getPalette();
  // add black, white, and gray to the palette
  pal.addBlackWhiteGray();
  // add our array of colors to the palette
  pal.addColors(colors);
  // include some information for Illustrator's header
  doc.setCreator("Ignotus");
  doc.setOrg("IgnoStudio");
  // set width and height of the document
  doc.setWidth(width);
  doc.setHeight(height);
  println("adding components...");
  // add ArrayList<? extends DisplayComponent> to the the default layer of the document
  doc.add(comps);
  doc.write(output);
}
  
  
