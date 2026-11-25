package multitask2.view.games;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask2.model.games.Block;
import multitask2.model.games.Touch;

public final class TouchView implements GameView{

  private final Touch touch;
  public TouchView(Touch touch) {
    Objects.requireNonNull(touch);
    this.touch = touch;
    super();
  }
  
  @Override
  public void render(Graphics2D graphics, Rectangle area) {
    GameView.checkInput(graphics, touch, area);

    var centerX = area.x + area.width / 2;
    var centerY = area.y + area.height / 2;
    
    double scaleX = (area.width / 2.0) / Touch.GRID_BOUND; 
    double scaleY = (area.height / 2.0) / Touch.GRID_BOUND;
    
    for(var block : touch.blocks()) {
      int bx = (int) Math.round(centerX + block.x() * scaleX); 
      int by = (int) Math.round(centerY - block.y() * scaleY); 
      int radius = Math.max(1, (int) Math.round(Touch.BLOCK_RADIUS * scaleX));    
      drawTimer(graphics, block, bx, by, radius);
    }
    
    int px = (int) Math.round(centerX + touch.playerX() * scaleX);
    int py = (int) Math.round(centerY - touch.playerY() * scaleY);
    int pSize = Math.max(1, (int) Math.round(2 * Touch.PLAYER_RADIUS * scaleX));

    graphics.setColor(Color.YELLOW);
    graphics.fillOval(px - pSize / 2, py - pSize / 2, pSize, pSize);
  }
  
  private void drawTimer(Graphics2D graphics, Block block, int cx, int cy, int radius) {
    int diameter = radius * 2;

    graphics.setColor(Color.DARK_GRAY);
    graphics.fillOval(cx - radius, cy - radius, diameter, diameter);

    graphics.setColor(Color.ORANGE);
    graphics.drawOval(cx - radius, cy - radius, diameter, diameter);

    double ratio = Math.max(0.0, Math.min(1.0, block.remaining() / Touch.BASE_COOLDOWN));
    int arcAngle = (int) Math.round(360.0 * ratio);
    graphics.setColor(new Color(255, 165, 0, 90));
    graphics.fillArc(cx - radius, cy - radius, diameter, diameter, 90, arcAngle);

    String label = String.format("%.1f", Math.max(0.0, block.remaining()));
    graphics.setColor(Color.WHITE);
    int fontSize = graphics.getFont().getSize();
    int textX = cx - (label.length() * fontSize) / 4;
    int textY = cy + fontSize / 3;
    graphics.drawString(label, textX, textY);
  }
}