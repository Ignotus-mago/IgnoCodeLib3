// This version of the Spinners code shows why having a library initialized with
// a reference to the host PApplet can be useful. See the bez() method. 

import net.paulhertz.aifile.*;

class SpinPoint extends java.awt.geom.Point2D.Float {
  private BezShape bez;
  private float angle; 

  SpinPoint() {
    this(0, 0);
  }

  SpinPoint(float x, float y) {
    this.x = x; 
    this.y = y;
  }

  public float x() {
    return (float) getX();
  }
  public void setX(float newX) {
    this.x = newX;
  }

  public float y() {
    return (float) getY();
  }
  public void setY(float newY) {
    this.y = newY;
  }

  public float angle() {
    return this.angle;
  }
  public void setAngle(float angle) {
    this.angle = angle;
  }

  public BezShape bez() {
    if (null == this.bez) {
      // We write our own version of random() because we don't have access to 
      // the host PApplet in our class, SpinnersTwo, from a Java class.
      // We could have passed it into the constructor, but that's not necessary... 
      int offset = (int) (Math.random() * 240 + 16);
      // ...since BezLine does have access to the (static) PApplet reference stored 
      // in the initialized IgnoCodeLib library! We can't get at it, but all classes
      // in the net.paulhertz.aifile package can. 
      this.bez = BezLine.makeCoordinates(this.x - offset, this.y, this.x + offset, this.y);
    }
    return bez;
  } 

  public void setBez(BezShape bez) {
    this.bez = bez;
  }
}



