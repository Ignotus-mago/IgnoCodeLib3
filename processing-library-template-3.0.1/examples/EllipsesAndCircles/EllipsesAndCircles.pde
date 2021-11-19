/**
 * March 14, 2012 3:35:47 PM CDT
 * Sample code for IgnoCodeLib Processing library by Paul Hertz
 * for IgnoCodeLib version 0.3.x and above
 * In this example, we use the various new methods in IgnoCodeLib 0.2 for creating ellipses and circles.
 */

import net.paulhertz.aifile.*;
import net.paulhertz.geom.Matrix3;
import java.io.*;
import java.util.ArrayList;


/** list of ellipses */
public ArrayList<BezEllipse> ellipses; 
/** list of circles */
public ArrayList<BezCircle> circles; 
/** list of colors, stored as Integer */
public ArrayList<Integer> farben;
/** IgnoCodeLib instance for initializing the library */
IgnoCodeLib igno;


public void setup() {
  size(480, 720);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  ellipses = new ArrayList<BezEllipse>();
  circles = new ArrayList<BezCircle>();
  initFarben();
  createEllipses();
  createCircles();
  println("Type 'x' to change drawing");
  println("Type 's' to save.");
}

public void draw() {
  background(127);
  // draw by stepping through the lists and calling draw() on each shape
  for (BezEllipse be : ellipses) {
    be.draw();
  }
  for (BezCircle bc : circles) {
    bc.draw();
  }
}


public void keyReleased() {
  if (key == 's' || key == 'S') {
    // save file
    ArrayList<DisplayComponent> shapes = new ArrayList<DisplayComponent>(ellipses);
    shapes.addAll(circles);
    String filename = "EllipsiCircles.ai";
    saveAI(filename, shapes, farben);
    println("saved" + "");
  }
  else if (key == 'x' || key == 'X') {
    // make colors, multiCurves and multiLines again
    initFarben();
    int[] ellipseChannelValues = {34, 55, 89, 144, 233};
    int[] circleChannelValues = {0, 127, 255};
    this.changeFillColors(circles, circleChannelValues);
    this.changeFillColors(ellipses, ellipseChannelValues);
  }
  else {
    ;
  }
}


/**
 * Generate some colors, save them in {@code farben}.
 */
private void initFarben() {
  farben = new ArrayList<Integer>();
  // same as color(32, 64, 128, 255), but we can't use color() in straight Java
  int c = Palette.composeColor(32, 64, 128, 255);
  farben.add(c);
  c = Palette.composeColor(76, 123, 199, 255);
  int[] perm = Palette.colorPermutation(c);
  for (int pc : perm) {
    farben.add(pc);
  }
}


/**
 * Create some ellipses with various static factory methods belonging to 
 * class BezEllipse, save them in {@code ellipses} array.
 */
private void createEllipses() {
  int[] channelValues = {34, 55, 89, 144, 233};
  BezEllipse elly;
  stroke(0);
  int c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  elly = BezEllipse.makeLeftTopWidthHeight(20, 20, 100, 120);
  ellipses.add(elly);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  elly = BezEllipse.makeCenterWidthHeight(width/2, height/2, 120, 100);
  ellipses.add(elly);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  elly = BezEllipse.makeLeftTopRightBottom(200, 20, 400, 180);
  ellipses.add(elly);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  elly = BezEllipse.makeLeftTopWidthHeightSectors(20, 420, 240, 160, 5);
  ellipses.add(elly);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  elly = BezEllipse.makeCenterWidthHeightSectors(width/2,  7 * height/8, 160, 90, 6);
  ellipses.add(elly);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  elly = BezEllipse.makeLeftTopRightBottomSectors(360, 420, 440, 640, 7);
  ellipses.add(elly);
}


/**
 * Create some circles with various static factory methods belonging to
 * class BezCircle, save them in {@code circles} array.
 */
public void createCircles() {
  int[] channelValues = {0, 127, 255};
  BezCircle circ;
  stroke(255);
  int c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  circ = BezCircle.makeCenterRadius(100, 240, 60);
  Matrix3 matx = new Matrix3();
  // throw in a transform
  matx.translateCTM(-circ.xctr(), -circ.yctr());
  float angle = (float) (Math.toRadians(30));
  matx.rotateCTM(angle);
  matx.translateCTM(circ.xctr(), circ.yctr());
  circ.transform(matx);
  circles.add(circ);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  circ = BezCircle.makeCenterRadiusSectors(100, 240, 40, 5);
  circles.add(circ);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  circ = BezCircle.makeLeftTopRadius(180, 200, 40);
  circles.add(circ);
  c = Palette.randColor(channelValues);
  fill(c);
  farben.add(c);
  circ = BezCircle.makeLeftTopRadiusSectors(180, 200, 20, 6);
  circles.add(circ);
}

/**
 * @param shapes          a list of shapes
 * @param channelValues   an array of possible values for color channels
 */
public void changeFillColors(ArrayList<? extends BezShape> shapes, int[] channelValues) {
  for (BezShape sh : shapes) {
    int c = Palette.randColor(channelValues);
    farben.add(c);
    sh.setFillColor(c);
  }
}

/**
 * Saves shapes to an Adobe Illustrator file.
 * @param aiFilename      name of the file, should end with ".ai"
 * @param components      a list of DisplayComponents (any kind except DocumentComponent)
 * @param paletteColors   a list of Integers (colors)
 */
public void saveAI(String aiFilename, ArrayList<DisplayComponent> components, ArrayList<Integer> paletteColors) {
  // println("saving Adobe Illustrator file " + aiFilename + "...");
  PrintWriter output = createWriter(aiFilename);
  DocumentComponent doc = new DocumentComponent("Ellipses and Circles");
  // get lots of feedback as we save
  doc.setVerbose(true);
  Palette pal = doc.getPalette();
  // include black and white in the palette
  pal.addBlackWhiteGray();
  // add our colors
  pal.addColors(paletteColors);
  doc.setCreator("Ignotus");
  doc.setOrg("IgnoStudio");
  doc.setWidth(width);
  doc.setHeight(height);
  for (DisplayComponent comp : components) {
    doc.add(comp);
  }
  // write the file, transforming geometry from the Processing coordinate system 
  // into the Adobe Illustrator coordinate system
  doc.write(output);  
}


