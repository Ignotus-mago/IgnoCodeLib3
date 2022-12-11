// March 14, 2012 3:35:47 PM CDT
// Sample code for IgnoCodeLib Processing library by Paul Hertz
// for IgnoCodeLib version 0.2.x and above
// Updated June 25, 2013 for IgonoCodeLib 0.3 release, compatible with both versions.
// Type 's' to save display to an Adobe Illustrator file
// Type 'x' to clear display
// Type 'w' to show or hide turtle icons
// Type 'f' to freeze or unfreeze drawing

import net.paulhertz.aifile.*;

public Turtle turtle0;
public Turtle turtle1;
public Turtle turtle2;
public Turtle turtle3;
public Turtle turtle4;

public boolean freeze = false;

/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(640, 480);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  createTurtles();
  printHelp();
  background(255);
}

public void createTurtles() {
  noFill();
  stroke(0);
  float turn = TWO_PI/5.0;
  turtle0 = new Turtle();
  turtle1 = new Turtle();
  turtle1.left(turn);
  turtle2 = new Turtle();
  turtle2.left(2 * turn);
  turtle3 = new Turtle();
  turtle3.left(3 * turn);
  turtle4 = new Turtle();
  turtle4.left(4 * turn);
  turtle0.setStrokeColor(color(240, 80, 10, 255));
  turtle1.setStrokeColor(color(80, 160, 10, 255));
  turtle2.setStrokeColor(color(80, 10, 240, 255));
  turtle3.setStrokeColor(color(210, 192, 10, 255));
  turtle4.setStrokeColor(color(30, 210, 160, 255));
}

public void printHelp() {
  println("Type 's' to save display to an Adobe Illustrator file");
  println("Type 'x' to clear display");
  println("Type 'w' to show or hide turtle icons");
  println("Type 'f' to freeze or unfreeze drawing");
  println("Type 'h' to show this help message");
}

public void draw() {
  if (freeze) return;
  background(255);
  moveTurtles();
  turtle0.draw();
  turtle1.draw();
  turtle2.draw();
  turtle3.draw();
  turtle4.draw();
}

public void moveTurtles() {
  float distance = random(2, 24);
  float turn = random(-30, 30);
  turtle0.forward(distance);
  turtle1.forward(distance);
  turtle2.forward(distance);
  turtle3.forward(distance);
  turtle4.forward(distance);
  turtle0.left(radians(turn));
  turtle1.left(radians(turn));
  turtle2.left(radians(turn));
  turtle3.left(radians(turn));
  turtle4.left(radians(turn));
  float nearBound = height/2;
  float pointX = width/2;
  float pointY = height/2;
  if (turtle0.trailSize() > 255) {
    turtle0.penUp();
    turtle0.penDown();
  }
  if (turtle1.trailSize() > 255) {
    turtle1.penUp();
    turtle1.penDown();
  }
  if (turtle2.trailSize() > 255) {
    turtle2.penUp();
    turtle2.penDown();
  }
  if (turtle3.trailSize() > 255) {
    turtle3.penUp();
    turtle3.penDown();
  }
  if (turtle4.trailSize() > 255) {
    turtle4.penUp();
    turtle4.penDown();
  }
  if (!closeToPoint(pointX, pointY, 
      (float) turtle0.getTurtleX(), (float) turtle0.getTurtleY(), nearBound)) {
    turtle0.setTurtleX(pointX);
    turtle0.setTurtleY(pointY);
    turtle0.penUp();
    turtle0.penDown();
  }
  if (!closeToPoint(pointX, pointY, 
      (float) turtle1.getTurtleX(), (float) turtle1.getTurtleY(), nearBound)) {
    turtle1.setTurtleX(pointX);
    turtle1.setTurtleY(pointY);
    turtle1.penUp();
    turtle1.penDown();
  }
  if (!closeToPoint(pointX, pointY, 
      (float) turtle2.getTurtleX(), (float) turtle2.getTurtleY(), nearBound)) {
    turtle2.setTurtleX(pointX);
    turtle2.setTurtleY(pointY);
    turtle2.penUp();
    turtle2.penDown();
  }
  if (!closeToPoint(pointX, pointY, 
      (float) turtle3.getTurtleX(), (float) turtle3.getTurtleY(), nearBound)) {
    turtle3.setTurtleX(pointX);
    turtle3.setTurtleY(pointY);
    turtle3.penUp();
    turtle3.penDown();
  }
  if (!closeToPoint(pointX, pointY, 
      (float) turtle4.getTurtleX(), (float) turtle4.getTurtleY(), nearBound)) {
    turtle4.setTurtleX(pointX);
    turtle4.setTurtleY(pointY);
    turtle4.penUp();
    turtle4.penDown();
  }
}

public boolean outOfBounds(float x, float y) {
  return (x < 0 || x > width || y < 0 || y > height);
}

public boolean closeToPoint(float pointX, float pointY, float testX, float testY, float nearBound) {
  return (dist(pointX, pointY, testX, testY) < nearBound);
}


public void keyReleased() {
  if (key == 's' || key == 'S') {
    saveAI();
    println("saved" + "");
  }
  else if (key == 'x' || key == 'X') {
    turtle0.clear();
    turtle1.clear();
    turtle2.clear();
    turtle3.clear();
    turtle4.clear();
    background(255);
  }
  else if (key == 'w' || key == 'W') {
    if (turtle0.isTurtleVisible()) {
      turtle0.hide();
      turtle1.hide();
      turtle2.hide();
      turtle3.hide();
      turtle4.hide();
    }
    else {
      turtle0.show();
      turtle1.show();
      turtle2.show();
      turtle3.show();
      turtle4.show();
    }
  }
  else if (key == 'h' || key == 'H') {
    printHelp();
  }
  else if (key == 'f' || key == 'F') {
    freeze = !freeze;
  }
}

public void saveAI() {
  PrintWriter pw = createWriter("turtleTrails.ai");
  DocumentComponent document = new DocumentComponent("Turtle Trails");
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("IgnoStudio");
  document.setWidth(width);
  document.setHeight(height);
  // the getTrails() method returns a turtle's trails, saved and current, bundled into a GroupComponent
  // the Groupcomponent can be added to a DocumentComponent, LayerComponent, or another GroupComponent
  document.add(turtle0.getTrails());
  document.add(turtle1.getTrails());
  document.add(turtle2.getTrails());
  document.add(turtle3.getTrails());
  document.add(turtle4.getTrails());
  document.writeWithAITransform(pw);
}



