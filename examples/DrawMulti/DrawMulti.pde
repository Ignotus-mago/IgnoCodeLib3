/**
 * March 14, 2012 3:35:47 PM CDT
 * Updated June 25, 2013 for IgonoCodeLib 0.3 release.
 * Sample code for IgnoCodeLib 0.3 Processing library by Paul Hertz
 * Not compatible with earlier versions.
 * Shows how to create multi-segment lines and curves. 
 * Also: hide and show layers, set opacity on lists of shapes, 
 * use document as a display list in the draw() method.
 */

import net.paulhertz.aifile.*;
import net.paulhertz.util.RandUtil;
import java.io.*;
import java.util.ArrayList;

/** list of straight line shapes */
public ArrayList<BezMultiLine> multiLines; 
/** list of curved line shapes */
public ArrayList<BezMultiCurve> multiCurves;
/** layers we put the shapes into */
public LayerComponent zigzagLayer;
public LayerComponent curvyLayer;
/** the document, top of the display list hierarchy */
public DocumentComponent document;
/** random number utility  */
public RandUtil rand;
/** storage for colors (as Integers) */
public ArrayList<Integer> farben;
/** grid spacing */
int spacer = 40;
/** number of horizontal grid cells */
int horizontalGrid = 20;
/** number of vertical grid cells */
int verticalGrid = 16;
/** color channel values in range 0..255 to use for our curves and lines */
int[] zigzagChannelValues = {34, 47, 55, 76, 89};
int[] curvyChannelValues = {89, 123, 144, 199, 233};
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  // this Processing 2.0 call is not valid in Processing 3.0
  // size(spacer * horizontalGrid, spacer * verticalGrid);
  size(800, 640);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  multiLines = new ArrayList<BezMultiLine>();
  multiCurves = new ArrayList<BezMultiCurve>();
  // arroy of colors
  farben = new ArrayList<Integer>();
  rand = new RandUtil();
  createMultiLines();
  createMultiCurves();
  // create the document up front because 
  // we're going to use its display list functionality
  createDocument();
  printHelp();
}


public void draw() {
  background(233, 233, 246);
  // the simplest way to draw everything is just to call document.draw()
  // just put new components into the document as you create them and they will draw 
  document.draw();
}

public void printHelp() {
  println("\nType 's' to save file");
  println("Type 'x' to change colors");
  println("Type '1' or '2' to show and hide layers");
  println("Press '3,' '4,' to fade curves and '5,' or '6' to fade zigzag lines out or in");
  println("Type 'h' to show this help message");
}


public void keyReleased() {
  if (key == 's' || key == 'S') {
    // save file, put shapes in two layers
    String filename = "Multi.ai";
    saveAI(filename, farben, document);
    println("saved" + "");
  }
  else if (key == 'x' || key == 'X') {
    // make colors, multiCurves and multiLines again
    farben.clear();
    changeStroke(multiCurves, curvyChannelValues, 4, 12);
    changeStroke(multiLines, zigzagChannelValues, 2, 8);

  }
  else if (key == '1') {
    // hide or show curvyLayer
    if (curvyLayer.isVisible()) {
      curvyLayer.hide();
    }
    else {
      curvyLayer.show();
    }
  }
  else if (key == '2') {
    // hide or show zigzagLayer
    if (zigzagLayer.isVisible()) {
      zigzagLayer.hide();
    }
    else {
      zigzagLayer.show();
    }
  }
  else if (key == 'h' || key == 'H') {
    printHelp();
  }
  else {
    ;
  }
}

public void keyPressed() {
  // key presses are used to fade layers in and out by steps
  // holding a key down repeats the fade action
  int fadeStep = 8;
  if (key == '3') {
    fadeOut(multiCurves, fadeStep);
  }
  else if (key == '4') {
    fadeIn(multiCurves, fadeStep);
  }
  else if (key == '5') {
    fadeOut(multiLines, fadeStep);
  }
  else if (key == '6') {
    fadeIn(multiLines, fadeStep);
  }
}

  
/**
 * Create some multi-segment lines, save them in {@code multiLines}.
 */
private void  createMultiLines() {
  int numberOfPoints = horizontalGrid;
  float[] gridCoords = new float[numberOfPoints * 2];
  int j = 0;
  for (int i = 0; i < numberOfPoints; i++) {
    gridCoords[j++] = i * spacer + spacer * 0.5f;
    gridCoords[j++] = (i % 2) == 1 ? spacer * 0.5f : spacer * 1.5f;
  }
  int count = 0;
  while (count < verticalGrid - 1) {
    multiLines.add(ziggyLine(gridCoords));
    for (int i = 0; i < gridCoords.length;) {
      i++;                                  // step over x-coord
      float y = gridCoords[i] + spacer;     // y = spacer + y-coord
      gridCoords[i++] = y;                  // set y-coord
    }
    count++;
  }
}

/**
 * @param coords   coordinate points for a multi-segment line
 * @return         a BezMultiLine instance created from coords
 */
private BezMultiLine ziggyLine(float[]coords) {
  // int[] zigzagChannelValues = {34, 47, 55, 76, 89};
  int c = Palette.randColor(zigzagChannelValues);
  farben.add(c);
  stroke(c);
  strokeWeight(rand.randomInRange(2, 8));
  BezMultiLine mul = BezMultiLine.makeMultiLine(coords);
  return mul;
}


/**
 * create some multi-segment curves, save them in {@code multiCurves}
 */
public void createMultiCurves() {
  // bias determines the amount of curvature
  float bias = 0.5f;
  int numberOfPoints = horizontalGrid;
  float[] gridCoords = new float[numberOfPoints * 2];
  int j = 0;
  // fill a list with grid points
  for (int i = 0; i < numberOfPoints; i++) {
    gridCoords[j++] = i * spacer + spacer * 0.5f;
    gridCoords[j++] = (i % 2) == 1 ? spacer * 1.5f : spacer * 0.5f;
  }
  // Set aside storage for curve vertices. the first vertex uses
  // 2 coordinates for one anchor point, subsequent vertices require 6
  // for two control points and one anchor point
  float[] curveCoords = new float[2 + (numberOfPoints -1) * 6];
  float x = gridCoords[0];
  float y = gridCoords[1];
  curveCoords[0] = x;
  curveCoords[1] = y;
  j = 2;
  float offset = spacer * bias;
  // create the first curvy line
  for (int i = 2; i < gridCoords.length;) {
    // first control point, offset from most recent anchor point
    curveCoords[j++] = x + offset;
    curveCoords[j++] = y;
    // set x and y to coordinates of the next anchor point
    x = gridCoords[i++];
    y = gridCoords[i++];
    // second control point, offset from next anchor point
    curveCoords[j++] = x - offset;
    curveCoords[j++] = y;
    // store next anchor point
    curveCoords[j++] = x;
    curveCoords[j++] = y;
  }
  multiCurves.add(curvyLine(curveCoords));
  int count = 1;
  // create the rest of the curvy lines
  while (count < verticalGrid - 1) {
    for (int i = 0; i < gridCoords.length;) {
      i++;                             // step over x-coord
      y = gridCoords[i] + spacer;      // y = spacer + y-coord
      gridCoords[i++] = y;             // set y-coord
    }
    x = gridCoords[0];
    y = gridCoords[1];
    curveCoords[0] = x;
    curveCoords[1] = y;
    j = 2;
    for (int i = 2; i < gridCoords.length;) {
      // first control point
      curveCoords[j++] = x + offset;
      curveCoords[j++] = y;
      x = gridCoords[i++];
      y = gridCoords[i++];
      // second control point
      curveCoords[j++] = x - offset;
      curveCoords[j++] = y;
      // anchor point
      curveCoords[j++] = x;
      curveCoords[j++] = y;
    }
    multiCurves.add(curvyLine(curveCoords));
    count++;
  }
}

/**
 * @param coords   coordinate points for a multi-segment curve
 * @return         a BezMultiLine instance created from coords
 */
private BezMultiCurve curvyLine(float[]coords) {
  // int[] curvyChannelValues = {89, 123, 144, 199, 233};
  int c = Palette.randColor(curvyChannelValues);
  farben.add(c);
  stroke(c);
  strokeWeight(rand.randomInRange(4, 12));
  BezMultiCurve mul = BezMultiCurve.makeMultiCurve(coords);
  return mul;
}


public void createDocument() {
  document = new DocumentComponent("MultiCurves and MultiLines");
  // get lots of feedback as we save
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("IgnoStudio");
  document.setWidth(width);
  document.setHeight(height);
  curvyLayer = new LayerComponent("Curvy");
  curvyLayer.add(multiCurves);
  document.add(curvyLayer);
  zigzagLayer = new LayerComponent("Zigzag");
  zigzagLayer.add(multiLines);
  document.add(zigzagLayer);
}


/**
 * @param shapes   list of BezShape
 * @param step     amount to fade
 * @return         true when fade out is complete (opacity == 0)
 */
public boolean fadeOut(ArrayList<? extends BezShape> shapes, int step) {
  int opacity = shapes.get(0).strokeOpacity();
  opacity = opacity < step ? 0 : opacity - step;
  //println("fade out opacity = " + opacity);
  boolean isFadedOut = (opacity == 0);
  for (BezShape sh : shapes) {
      sh.setStrokeOpacity(opacity);
  }
  return isFadedOut;
}

/**
 * @param shapes   list of BezShape
 * @param step     amount to fade
 * @return         true when fade in is complete (opacity == 255)
 */
public boolean fadeIn(ArrayList<? extends BezShape> shapes, int step) {
  int opacity = shapes.get(0).strokeOpacity();
  opacity = opacity + step > 255 ? 255 : opacity + step;
  //println("fade in opacity = " + opacity);
  boolean isFadedIn = (opacity == 255);
  for (BezShape sh : shapes) {
      sh.setStrokeOpacity(opacity);
  }
  return isFadedIn;
}


/**
 * @param shapes          list of shapes to change
 * @param channelValues   possible values for color channels
 * @param weightLo        low end of range for stroke weight
 * @param weightHi        high end of range for stroke weight
 */
public void changeStroke(ArrayList<? extends BezShape> shapes, int[] channelValues, float weightLo, float weightHi) {
  for (BezShape sh : shapes) {
    int c = Palette.randColor(channelValues);
    farben.add(c);
    sh.setStrokeColor(c);
    sh.setWeight(rand.randomInRange(weightLo, weightHi));
  }
}

/**
 * Saves shapes to an Adobe Illustrator file.
 * @param aiFilename      name of the file, should end with ".ai"
 * @param paletteColors   a list of Integers (colors)
 * @param document        the DocumentComponent to save
 */
public void saveAI(String aiFilename, ArrayList<Integer> paletteColors, DocumentComponent document) {
  // tell BezShape to use opacity values when exporting to AI
  AIFileWriter.setUseTransparency(true);
  println("saving Adobe Illustrator file " + aiFilename + "...");
  PrintWriter output = createWriter(aiFilename);
  Palette pal = document.getPalette();
  // include black and white in the palette
  pal.addBlackWhiteGray();
  // add our colors
  pal.addColors(paletteColors);
  // write the file, transforming geometry from the Processing coordinate system 
  // into the Adobe Illustrator coordinate system
  document.writeWithAITransform(output);
}