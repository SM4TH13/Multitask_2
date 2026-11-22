package multitask2.view.games;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask2.model.games.Fly;


public final class FlyView implements GameView {
	
  private static final Color FLY_COLOR = new Color(230, 90, 90);

  private static final Color WALL_COLOR = new Color(75, 150, 50);

  private final Fly fly;

  
  public FlyView(Fly fly) {
    Objects.requireNonNull(fly);
    this.fly = fly;
    super();
  }

  @Override
  public void render(Graphics2D graphics, Rectangle area) {
    GameView.checkInput(graphics, fly, area);
    
    double scaleX = area.width / Fly.WIDTH;
    double scaleY = area.height / Fly.HEIGHT;
    
    var width = (int) Math.ceil(Fly.OBSTACLE_WIDTH * scaleX);
    graphics.setColor(WALL_COLOR);
    for (var o : fly.obstacles()) {
      int left = area.x + (int) (o.x() * scaleX);
      int gapTop = area.y + area.height - (int) ((o.gapCenter() + o.gapSize() / 2) * scaleY);
      int gapBottom = area.y + area.height - (int) ((o.gapCenter() - o.gapSize() / 2) * scaleY);
      graphics.fillRect(left, area.y, width, gapTop - area.y);
      graphics.fillRect(left, gapBottom, width, area.y + area.height - gapBottom);
    }
    
    int diameter = (int) (2 * Fly.FLY_HALF_SIZE * Math.min(scaleX, scaleY));
    int centerX = area.x + (int) (Fly.FLY_CENTER * scaleX);
    int centerY = area.y + area.height - (int) (fly.altitude() * scaleY);
    graphics.setColor(FLY_COLOR);
    graphics.fillOval(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter);    
    
  }
}