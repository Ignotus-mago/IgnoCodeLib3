// this basic example for IgnoCodeLib 0.3 shows how to initialize the library
// with a call to new IgnoCodeLib(this) in the setup method. 

import net.paulhertz.aifile.*;

IgnoCodeLib hello;

void setup() {
  size(400,400);
  smooth();
  hello = new IgnoCodeLib(this);
  PFont font = createFont("",36);
  textFont(font);
}

void draw() {
  background(0);
  fill(255);
  text(hello.sayHello(), 32, 200);
}