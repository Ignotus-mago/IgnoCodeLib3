// March 14, 2012 3:35:47 PM CDT
// Sample code for IgnoCodeLib Processing library by Paul Hertz
// for IgnoCodeLib version 0.2.x and above
// Updated June 25, 2013 for IgonoCodeLib 0.3 release, compatible with both versions.

import net.paulhertz.aifile.*;

public Turtle turtle;
public int maxTrailLength = int(random(8, 64));
public int maxNumberOfTrails = 40;
boolean readyToSave;
/** IgnoCodeLib library */
IgnoCodeLib igno;

public void setup() {
  size(640, 480);
  smooth();
  noFill();
  stroke(random(16, 255));
  background(255);
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  // declare a new turtle, by default pointing right at the center of the display
  // with current style (stroke and fill color, stroke weight) of Processing environment
  turtle = new Turtle();
  // ready to draw
  turtle.penDown();
  turtle.move(0);
  println("Type 's' to save");
}

public void draw() {
  background(255);
  // moveTo() operates by "divine intervention," sending the turtle to the supplied location
  turtle.moveTo(random(0, width), random(0, height));
  // trailSize() returns the total number of vertices in the current trail
  if (turtle.trailSize() > maxTrailLength) {
    // save current trail and stop drawing
    turtle.penUp();
    // start a new trail
    turtle.penDown();
    // change the stroke color
    int i = int(random(0, 16)) * 16;
    turtle.setStrokeColor(color(i));
    maxTrailLength = int(random(8, 64));
  }
  // size() returns the total number of trails stored in the turtle
  if (turtle.size() > maxNumberOfTrails) {
    // start trails again
    ArrayList<BezShape> trails = turtle.getTurtleTrails();
    trails.clear();
  }
  // draw all the trails so far
  turtle.draw();
  if (readyToSave) {
    saveAI();
    println("saved AI file");
    readyToSave = false;
  }
}

public void keyReleased() {
  if (key == 's' || key == 'S') {
    readyToSave = true;
    println("ready to save..." + "");
  }
}

public void saveAI() {
  PrintWriter pw = createWriter("randomTurtle.ai");
  DocumentComponent document = new DocumentComponent("Random Turtle");
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("IgnoStudio");
  document.setWidth(width);
  document.setHeight(height);
  // the getTrails() method returns all saved trails and the current trail, if there is one, as a
  // GroupComponent which you can add to a DocumentCompenent, LayerComponent, or other GroupComponent
  document.add(turtle.getTrails());
  // In IgnoCodeLib 0.3, write(output) now performs a transform
  // to make the saved file's orientation identical to the display, the same as
  // writeWithAITransform. Call writeNoTransform to omit the transform.
  document.write(pw);
}

