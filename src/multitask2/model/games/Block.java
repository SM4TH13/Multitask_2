package multitask2.model.games;

public final class Block {
  private final double x;
  private final double y;
  private double remaining;
  
  public Block(double x, double y, double remaining) {
    if(remaining < 0) {
      throw new IllegalArgumentException();
    }
    this.x = x;
    this.y = y;
    this.remaining = remaining;
  }
  
  public void tick(double dt) {
    remaining -= dt;
  }
  
  public boolean expiring() {
    return remaining <=0;
  }
  
  public double remaining() {
    return remaining; 
  }
  
  public double x() {
    return x; 
  }

  public double y() {
    return y; 
  }
  
  public boolean touchedBy(double px, double py, double playerRadius) {
    double dx = px - x;
    double dy = py - y;
    double hitRadius = playerRadius + Touch.BLOCK_RADIUS;
    return dx * dx + dy * dy <= hitRadius * hitRadius;
  }

}