/*
 * Sample code for IgnoCodeLib 0.2 Processing library by Paul Hertz.
 * Updated June 25, 2013, for IgonoCodeLib 0.3 release, compatible with both versions.
 * The methods that are called for output to the Adobe Illustrator file do not 
 * apply a geometric transform (mirror on horizontal axis), so output will 
 * not be identical to the Processing display, which uses a slightly different coordinate system. 
 *
 * Also see the Spinners example, which uses SpinPoint as an inner class in a Processing tab
 * instead of as a separate Java class, like we do here.  
 *
 * An example of how IgnoCodeLib can support interactive animation. 
 * Demonstrates the use of RandUtil.randomElement().
 * click to create a spinner, type "s" to save.
 * 
 */

import net.paulhertz.aifile.*;
import net.paulhertz.geom.*;
import net.paulhertz.util.*;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

String fileName = "spinners.ai";
ArrayList<SpinPoint> displayList;
DocumentComponent doc;
int count = 0;
int shapeLimit = 64;
int[] weights = {1, 2, 3, 5, 8};
int[] signs = {-1, 1};
float[] angles; 
int howMany;
RandUtil rand;
/** IgnoCodeLib library */
IgnoCodeLib igno;


void setup() {
  size(640, 480);
  background(255);
  smooth();
  howMany = 3;
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  rand = new RandUtil();
  initAngles();
  displayList = new ArrayList<SpinPoint>(shapeLimit);
  println("click to create a spinner");
  println("type a number key to change number of spinners");
  println("type 's' to output file");
}

void draw() {
  background(255);
  modifyDisplayList();
  for (SpinPoint pt : displayList) {
    BezShape bShape = pt.bez();
    bShape.draw();
  }
}

void mouseClicked() {
  // create 3 spinning lines at click point
  int x = mouseX;
  int y = mouseY;
  for (int i = 0; i < howMany; i++) {
    SpinPoint pt = new SpinPoint(x, y);
    BezShape bez = pt.bez();
    bez.setStrokeColor(Palette.randColor());
    // set the center of transformation to the geometric center
    // which is also the point where you clicked
    bez.calculateCenter();
    // randomly select a number from the weights array
    bez.setWeight(rand.randomElement(weights));
    // randomly select a number from the angles array
    int sign = rand.randomElement(signs);
    pt.setAngle(sign * rand.randomElement(angles));
    if (count < shapeLimit) {
      displayList.add(pt);
      println("new shape " + count);
      count++;
    }
    else {
      displayList.set(count % shapeLimit, pt);
      println("replaced shape " + count % shapeLimit);
      count++;
    }
  }
}

public void keyPressed() {
  if (key == 's' || key == 'S') {
    PrintWriter pw = createWriter("spinners.ai");
    doc = beginDocument("Spinners", pw);
    for (SpinPoint pt : displayList) {
      BezShape bShape = pt.bez();
      writeComponent(bShape, pw);
    }
    endDocument(doc, pw);
  }
  switch(key) {
    case '1': { howMany = 1; break; }
    case '2': { howMany = 2; break; }
    case '3': { howMany = 3; break; }
    case '4': { howMany = 4; break; }
    case '5': { howMany = 5; break; }
    case '6': { howMany = 6; break; }
    case '7': { howMany = 7; break; }
    case '8': { howMany = 8; break; }
    case '9': { howMany = 9; break; }
  }  
}


void modifyDisplayList() {
  for (SpinPoint pt : displayList) {
    BezShape bShape = pt.bez();
    bShape.rotateShape(pt.angle());
  }

}

void initAngles() {
  angles = new float[8];
  float m = PI/400;
  angles[0] = m;
  angles[1] = m * 2;
  angles[2] = m * 3;
  angles[3] = m * 5;
  angles[4] = m * 8;
  angles[5] = m * 1.5;
  angles[6] = m * 2.5;
  angles[7] = m * 4;
}


public DocumentComponent beginDocument(String docName, PrintWriter pw) {
  println("Creating Adobe Illustrator 7.0 document " + docName);
  DocumentComponent newDoc = new DocumentComponent(this, docName);
  Palette pal = newDoc.getPalette();
  pal.addBlackWhiteGray();
  newDoc.setCreator("Ignotus");
  newDoc.setOrg("IgnoStudio");
  newDoc.setWidth(width);
  newDoc.setHeight(height);
  println("writing header to file");
  newDoc.writeHeader(pw);
  // You can mix direct calls to static methods in AIFileWriter with calls 
  // to DisplayComponents: everything goes through the PrintWriter to the same file. 
  // this call sets lines to have rounded caps and joins
  AIFileWriter.setLineAttributes(1, 1, 4, pw);
  return newDoc;
}

/**
 * Writes a DisplayComponent to a file. DisplayComponent is the superclass of all components
 * in the net.paulhertz.aifile document hierarchy. 
 * @param comp   any DisplayComponent
 * @param pw     PrintWriter for output to file
 */
public void writeComponent(DisplayComponent comp, PrintWriter pw) {
  comp.write(pw);
}

public void endDocument(DocumentComponent document, PrintWriter pw) {
  println("writing trailer and closing file");
  document.writeTrailer(pw);
}

