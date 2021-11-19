// March 14, 2012 3:35:47 PM CDT
// for IgnoCodeLib version 0.3.x and above
// Updated June 25, 2013 for IgonoCodeLib 0.3 release, not compatible with earlier versions.


import net.paulhertz.aifile.*;

import java.util.*;


public ArrayList<Turtle> turtles;
public Random randGenerator;
public boolean readyToSave = false;
/** IgnoCodeLib library */
IgnoCodeLib igno;


public void setup() {
  size(640, 480);
  smooth();
  // Set up the library, which will store a reference to the host PApplet 
  // for other classes in the library to use.
  igno = new IgnoCodeLib(this);
  turtles = new ArrayList<Turtle>();
  createTurtles();
  // create an instance of the Java class Random
  randGenerator = new Random();
  background(255);
  println("Type 's' to save; type 'x' to clear");
}

public void createTurtles() {
  noFill();
  // fill(255, 127);
  stroke(0, 127);
  strokeWeight(2);
  int turtleCount = int(random(12, 60));
  float step = height/turtleCount;
  for (int i = 0; i < turtleCount; i++) {
    Turtle t = new Turtle(0, i * step + step * 0.5);
    turtles.add(t);
    color c = color(int(random(0, 255)), int(random(0, 255)), int(random(0, 255)), 127);
    t.setStrokeColor(c);
  }
}

public void moveTurtles() {
  float distance = 0;
  do {
    distance = (float) gauss(16, 0.25);
  } while (distance < 1);
  for (Turtle t : turtles) {
    double theta = gauss() * 0.01;
    t.turn(theta);
    t.forward(distance);
  }
  Turtle firstTurtle = turtles.get(0);
  double x = firstTurtle.getTurtleX();
  double y = firstTurtle.getTurtleY();
  if (outOfBounds((float) x, (float) y)) {
    if (readyToSave) {
      saveAI();
      println("saved AI file");
      readyToSave = false;
    }
    turtles.clear();
    createTurtles();
  }
}

public void draw() {
  moveTurtles();
  for (Turtle t : turtles) {
    t.draw();
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
    readyToSave = true;
    println("ready to save..." + "");
  }
  else if (key == 'x' || key == 'X') {
    background(255);
  }
}


/**
 * Returns a Gaussian variable using Java library call to <code>Random.nextGaussian</code>.
 * @return a Gaussian-distributed random number with mean 0.0 and variance 1.0
 */
public double gauss() {
  return randGenerator.nextGaussian();
 }
 
/**
 * Returns a Gaussian variable using a Java library call to <code>Random.nextGaussian</code>.
 * @param mean
 * @param variance
 * @return a Gaussian-distributed random number with mean <code>mean</code> and variance <code>variance</code>
 */
public double gauss(double mean, double variance) {
  return randGenerator.nextGaussian() * Math.sqrt(variance) + mean;
}



public void saveAI() {
  PrintWriter pw = createWriter("turtleTrails.ai");
  DocumentComponent document = new DocumentComponent("Turtle Trails");
  document.setVerbose(true);
  document.setCreator("Ignotus");
  document.setOrg("IgnoStudio");
  document.setWidth(width);
  document.setHeight(height);
  // the getTrails() method returns all saved trails and the current trail, if there is one, as a
  // GroupComponent which you can add to a DocumentCompenent, LayerComponent, or other GroupComponent
  for (Turtle t : turtles) {
    document.add(t.getTrails());
  }
  // In IgnoCodeLib 0.3, write(output) now performs a transform
  // to make the saved file's orientation identical to the display, the same as
  // writeWithAITransform. Call writeNoTransform to omit the transform.
  document.write(pw);
}





