class SpinPoint extends java.awt.geom.Point2D.Float {
  private BezShape bez;
  private float angle; 

  SpinPoint() {
    this(0,0);
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
      int offset = rand.randomInRange(16, 240);
      // because SpinPoint.pde is an inner class of Spinners, we can access the initialized 
      // IgnoCodeLib and its various classes and methods. If SpinPoint were a java file and  
      // hence a separate class, we would need a reference to Spinners.
      this.bez = BezLine.makeCoordinates(this.x - offset, this.y, this.x + offset, this.y);
    }
    return bez;
  } 
  
  public void setBez(BezShape bez) {
    this.bez = bez;
  }

}




