/** 
 * Extends Component Visitor to assign new random colors to BezShapes in a display graph. 
 */

class RandomColorVisitor extends ComponentVisitor {
  int[] darks = {21, 34, 47, 55, 76, 89};
  int[] lights = {178, 186, 199, 220, 233};
  float w = -1;

  /**
   * A no-argument constructor.
   */
  public RandomColorVisitor() {
    
  }
  /*
   * A constructor where we set the variable w (stroke weight).
   */
  public RandomColorVisitor(float w) {
    this.w = abs(w);
  }

  
  public void setDarks(int[] newDarks) {
    this.darks = newDarks;
  }
  
  public void setLights(int[] newLights) {
    this.lights = newLights;
  }
  
    /**
   * visits a BezShape node
   * @param comp   a BezShape instance
   * We can implement all sorts of logic here to decide what to do with a BezShape. 
   * You can even do animation. 
   */
  public void visitBezShape(BezShape comp) {
    comp.setStrokeColor(Palette.randColor(darks));
    if (comp.bezType() == BezShape.BezType.BEZ_ELLIPSE) {
      comp.rotateShape(PI * 0.0625);
    }
    else {
      comp.scaleShape(random(0.8, 1.25));
    }
    comp.setFillColor(Palette.randColor(lights));
    if (w > 0) {
      comp.setWeight(w);
    }
    else {
      comp.setWeight(random(0.5, 8.0));
    }
  }

  
}
