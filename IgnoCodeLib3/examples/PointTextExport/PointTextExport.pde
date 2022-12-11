/*
 * January 29, 2012 3:35:28 PM CST
 * Created for IgnoCodeLib 0.2 Processing library by Paul Hertz.
 * Updated June 25, 2013 for IgonoCodeLib 0.3 release, not compatible with earlier releases.
 * The writeNoTransform call is only available in 0.3 and the packages have been renamed
 * from com.ignotus to net.paulhertz in the import section. 
 * Creates Ilustrator "point text" objects with different display attributes:
 * plain text, stroked text, rotated text. Approximates their appearance in Processing. 
 * When you open the file in Adobe Illustrator, you will need to update the text format
 * to see the text correctly.
 */

import java.util.*;
import java.io.*;  
import java.awt.geom.Point2D;
import net.paulhertz.aifile.*;   // library that handles Adobe Illustrator document structure and export
import net.paulhertz.geom.*;     // library for geometric transforms and matrices

ArrayList<Integer> farben;
Matrix3 aiTransform;
String fileName = "pointText.ai";
ArrayList<DisplayComponent> textList;
DocumentComponent doc;
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(480,720);
  background(255);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  // array of colors
  farben = new ArrayList<Integer>();
  setupTransform();
  textList = new ArrayList<DisplayComponent>();
  createDocument();
  println("type 's' to output file");
}


public void draw() {
  // the document knows how to draw (so do layers and groups and shapes)
  // but Processing text only supplies some basic display functions while 
  // Adobe Illustrator has very rich text capabilities.
  background(255);
  doc.draw();
}


/**
 * 2-D geometric transforms can be captured by a matrix with three rows and three columns,
 * these matrices are handled by the class net.paulhertz.geom.Matrix3.
 * In its initial state, a matrix is the "identity matrix," to which we add various transforms
 * The matrix transforms 2D geometry through the operation of matrix multiplication. The
 * nuts and bolts need not concern you: the essential thing to understand is that you can 
 * add a series of geometric transforms to the matrix and then execute them all at once.
 * The matrix is also referred to as the CTM or Current Transoformation Matrix.
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
 * Creates a document with "point text" objects
 */
public void createDocument() {
  doc = new DocumentComponent("Point Text Example");
  doc.setVerbose(true);
  textList.add(simpleText());
  textList.add(rotatedText());
  textList.add(strokedText());
  doc.add(textList);
}


void keyPressed() {
  if (key == 's') {
    saveAI(fileName, doc, farben);
  }  
}


/**
 * @return a simple PointText
 */
public PointText simpleText() {
  PointText ptext = new PointText(100, 100, "Textos de Vicente Huidobro y Leon Felipe");
  // we'll use Futura Medium im Processing, but let Adobe use its default font
  // 12 points is the default size set by the PointText class.
  // tell Processing to load the font
  ptext.loadFont("Futura-Medium-12.vlw");
  // also store the name of the font in our PointText instance
  ptext.setPFontname("Futura-Medium-12.vlw");
  return ptext;
}


/**
 * @return a PointText that has been rotated
 */
public PointText rotatedText() {
  PointText ptext = new PointText(this);
  // tell Processing to load the font
  ptext.loadFont("Futura-Medium-12.vlw");
  // also store the name of the font in our PointText instance
  ptext.setPFontname("Futura-Medium-12.vlw");
  ptext.setText("Los cuatro puntos cardinales son tres: el sur y el norte.");
  // We construct a matrix to rotate and then translate the text.
  // If we had constructed the text at 200, 200 to begin with, the results
  // not be the same.
  Matrix3 matx = new Matrix3();
  matx.rotateCTM(PI/8);
  matx.translateCTM(200, 200);
  // setting the matrix will tell AI to do the rotation 
  // and translation when it opens the output file
  ptext.setMatrix(matx);
  ptext.setFillColor(color(233, 55, 34));
  // we tell Illustrator to use Futura Medium
  ptext.setFontname("Futura-Medium");
  return ptext;
}

/**
 * @return a PointText that has a stroke and no fill (will not display as such in Processing)
 */
public PointText strokedText() {
  // Processing breaks lines with "\n", but Illustrator breaks them with "\r": we use both
  // to break lines in both applications.
  String txt = "Todos somos marineros,\r\nmarineros que saben bien navegar.\r\nTodos somos capitanes,\r\ncapitanes de la mar.";
  PointText ptext = new PointText(50, 400, txt);
  // tell Processing to load the font
  ptext.loadFont("Times-Bold-24.vlw");
  // store the font name in the instance of PointText
  ptext.setPFontname("Times-Bold-24.vlw");
  ptext.setNoFill();
  ptext.setStrokeColor(color(47, 123, 199));
  // 0.5 pt stroke
  ptext.setWeight(0.5);
  // we tell both Illustrator and Processing to use Times Bold 
  ptext.setFontname("Times-Bold");
  ptext.setSize(24);
  return ptext;
}


/**
 * saves shapes to an Adobe Illustrator file
 * @param aiFilename   name of our file
 * @param doc          the DocumentComponent to save
 * @param colors       colors for the palette
 */
public void saveAI(String aiFilename, DocumentComponent doc, ArrayList<Integer> colors) {
  println("saving Adobe Illustrator file " + aiFilename + "...");
  PrintWriter output = createWriter(aiFilename);
  Palette pal = doc.getPalette();
  pal.addBlackWhiteGray();
  //pal.add(colors);
  doc.setCreator("Ignotus");
  doc.setOrg("IgnoStudio");
  doc.setWidth(width);
  doc.setHeight(height);
  // The transform we created reflects everything around a horizontal line
  // it's a "symmetrical" transform: doing it a second time undoes it.
  // Version 0.2 of IgnoCodeLib includes a new method in the DocumentComponent class, 
  // writeWithAITransform(), the handles creating and applying the coordinate system transform 
  // for you. It could replace the next three lines of code.
  // When we transform text, we just transform its point of origin. Scale and rotation
  // of the text itself are not changed, even if they are part of the transform. If you
  // call PointText.setMatrix instead, as above, you can change scale, rotation, skew, and reflection
  // of the text.
  doc.transform(aiTransform);
  // In IgnoCodeLib 0.3, write(output) now performs a transform
  // to make the saved file's orientation identical to the display, the same as
  // writeWithAITransform. Call writeNoTransform to omits the transform. We do that
  // here because we're applying a transform to the file before we write and 
  // undoing it afterwards (the transform is its own inverse). 
  doc.writeNoTransform(output);
  doc.transform(aiTransform);
}


