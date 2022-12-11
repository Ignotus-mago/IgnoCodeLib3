/** 
 *
 * Sample code for IgnoCodeLib 0.3 library for Processing by Paul Hertz.
 *
 * Uses a transformation matrix to animate a zigzag multiline BezShape. Shows how to write
 * Adobe Illustrator header and trailer separately from graphics. Shows how to enable transparency.
 * Once a transform is applied to a shape, it is stored with the shape and can be called multiple times,
 * a shown in the doGraphics() method. The BezShape methods getCtm(), getMatrix(PApplet), setCtm(Matrix3), 
 * and setCtm(PMatrix2D) can get or set the ctm field of BezShape without performing a transform. When a 
 * BezShape is instantiated, it obtains Processing's current transformation matrix (PMatrix2D) and stores it
 * in the instance variable ctm.
 * The methods that are called for output to the Adobe Illustrator file do not 
 * apply a geometric transform (mirror on horizontal axis), so output will 
 * not be identical to the Processing display, which uses a different coordinate system. 
 */

import net.paulhertz.aifile.*;
import net.paulhertz.geom.*;


int c1 = color(233, 55, 21, 192);
int c2 = color(21, 123, 233, 24);
int c3 = color(55, 233, 89, 80);
float rot = 0.025;
float inc = 0.0002;
DocumentComponent doc;
PrintWriter pw;
boolean saveToFile;
/** IgnoCodeLib library */
IgnoCodeLib igno;


/**
 * Do standard setup calls. 
 */
public void setup() {
  size(480, 400);
  background(255);
  smooth();
  saveToFile = false;
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  println("type 's' to output file");
}

public void draw() {
  background(254, 246, 220);
  doGraphics();
  // rotate a little more or a little less
  rot += inc;
  // when we reach the limits, change the direction of rotation
  if (rot > 0.084 || rot < 0.0001) inc = -inc;
}

public void keyPressed() {
  if (key == 's' || key == 'S') {
    pw = createWriter("zigzagSwirl.ai");
    doc = beginDocument("Zigzag Swirl", pw);
    saveToFile = true;
    // tell AIFileWriter to use transparency
    AIFileWriter.setUseTransparency(true);
    doGraphics();
    saveToFile = false;
    endDocument(doc, pw);
  }
}

public void doGraphics() {
  stroke(c1);
  fill(c2);
  strokeWeight(5);
  // vertex pairs stored in an array of floats
  float[] points = {80, 240, 130, 260, 170, 180, 210, 270, 250, 200, 260, 330};
  BezShape zigzag = BezMultiLine.makeMultiLine(points);
  zigzag.draw();
  if (saveToFile) writeComponent(zigzag, pw);
  // change the stroke weight
  zigzag.setWeight(1);
  Matrix3 matx = new Matrix3();
  // translate the fifth vertex to (0, 0)
  matx.translateCTM(-250, -200);
  // do a rotation
  matx.rotateCTM(PI * rot);
  // move off center little by little
  matx.translateCTM(-1.125, 2.3);
  // and translate back again
  matx.translateCTM(250, 200);
  // now set the ctm of zigzag to matrix we just loaded
  zigzag.setCtm(matx);
  // apply the matrix multiple times
  for (int i = 0; i < 48; i++) {
    zigzag.draw();
    if (saveToFile) writeComponent(zigzag, pw);
    zigzag.transform();
    color c = lerpColor(c2, c3, i/48.0f);
    zigzag.setFillColor(c);
  }
  zigzag.setWeight(3);
  zigzag.draw();
  if (saveToFile) writeComponent(zigzag, pw);
}

public DocumentComponent beginDocument(String docName, PrintWriter pw) {
  DocumentComponent newDoc = new DocumentComponent(docName);
  Palette pal = newDoc.getPalette();
  pal.addBlackWhiteGray();
  pal.addColor(c1);
  pal.addColor(c2);
  pal.addColor(c3);
  newDoc.setCreator("Ignotus");
  newDoc.setOrg("IgnoStudio");
  newDoc.setWidth(width);
  newDoc.setHeight(height);
  println("writing header to file");
  newDoc.writeHeader(pw);
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
