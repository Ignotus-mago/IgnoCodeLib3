// Sample code for IgnoCodeLib Processing library by Paul Hertz
// for IgnoCodeLib version 0.3.x and above
// shows very basic use of turtle to make drawings
// The turtle uses the IgnoCodeLib graphics calls behind the scenes.
// See other turtle examples for saving to Adobe Illustrator 7.0 file.

import net.paulhertz.aifile.*;

public Turtle geoTurtle;
/** instance of our library to be initialized in setup */
IgnoCodeLib igno;

public void setup() {
  size(640, 480);
  smooth();
  fill(255);
  stroke(127);
  igno = new IgnoCodeLib(this);
  geoTurtle = new Turtle();
  squareTurtle(geoTurtle, width/4, height/4, 100, color(233, 55, 89));
  starTurtle(geoTurtle, 3 * width/4, height/2, 7, 60, color(240, 220, 20));
  geoTurtle.setTurtleAngle(TWO_PI / 8);
  squareTurtle(geoTurtle, width/2, 16, 320, color(0, 55, 89, 127));
  geoTurtle.setTurtleAngle(0);
  starTurtle(geoTurtle, width/5, height * 0.75, 5, 200, color(160, 192, 20));
}

// make a square
public void squareTurtle(Turtle turtle, float x, float y, float distance, color fillColor) {
  turtle.setTurtleX(x);
  turtle.setTurtleY(y);
  turtle.penDown();
  turtle.forward(distance);
  turtle.right(radians(90));
  turtle.forward(distance);
  turtle.right(radians(90));
  turtle.forward(distance);
  turtle.right(radians(90));
  turtle.forward(distance);
  turtle.setFillColor(fillColor);
  turtle.penUp();
}

// points should be an odd number
public void starTurtle(Turtle turtle, float x, float y, int points, float distance, color fillColor) {
  turtle.setTurtleX(x);
  turtle.setTurtleY(y);
  turtle.penDown();
  float theta = TWO_PI/points;
  theta = theta * 2;
  for (int i = 0; i < points; i++) {
    turtle.forward(distance);
    turtle.right(theta);
  }
  turtle.setFillColor(fillColor);
  turtle.penUp();
}


public void draw() {
  geoTurtle.draw();
}

