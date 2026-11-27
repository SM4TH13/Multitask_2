package multitask2.view.games;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import multitask2.model.games.Jump;

public final class JumpView implements GameView {

  private static final Color GROUND_COLOR = new Color(120, 100, 80);
  private static final Color PLAYER_COLOR = new Color(90, 200, 220);

  private static final double TOP_MARGIN = 2;
  private static final double WORLD_TOP = Jump.JUMP_HEIGHT + Jump.PLAYER_SIZE + TOP_MARGIN;
  private static final double WORLD_HEIGHT = WORLD_TOP + Jump.FALL_DEPTH;

  private final Jump jump;

  public JumpView(Jump jump) {
    Objects.requireNonNull(jump);
    this.jump = jump;
    super();
  }

  @Override
  public void render(Graphics2D graphics, Rectangle area) {
    GameView.checkInput(graphics, jump, area);

    var scaleX = area.width / Jump.WIDTH;
    var scaleY = area.height / WORLD_HEIGHT;
    var groundY = area.y + (int) Math.round(WORLD_TOP * scaleY);

    var oldClip = graphics.getClip();
    graphics.clipRect(area.x, area.y, area.width, area.height);

    drawPlayer(graphics, area, scaleX, scaleY);
    drawGround(graphics, area, scaleX, groundY);

    graphics.setClip(oldClip);
  }

  private void drawPlayer(Graphics2D graphics, Rectangle area, double scaleX, double scaleY) {
    var left = area.x + (int) Math.round((Jump.PLAYER_X - Jump.PLAYER_SIZE / 2) * scaleX);
    var top = area.y + (int) Math.round((WORLD_TOP - jump.playerY() - Jump.PLAYER_SIZE) * scaleY);
    var width = (int) Math.round(Jump.PLAYER_SIZE * scaleX);
    var height = (int) Math.round(Jump.PLAYER_SIZE * scaleY);

    graphics.setColor(PLAYER_COLOR);
    graphics.fillRect(left, top, width, height);
  }

  private void drawGround(Graphics2D graphics, Rectangle area, double scaleX, int groundY) {
    graphics.setColor(GROUND_COLOR);
    var cursor = 0.0;
    for (var hole : jump.holes()) {
      fillGround(graphics, area, scaleX, groundY, cursor, hole.x());
      cursor = Math.max(cursor, hole.x() + hole.width());
    }
    fillGround(graphics, area, scaleX, groundY, cursor, Jump.WIDTH);
  }

  private static void fillGround(Graphics2D graphics, Rectangle area, double scaleX, int groundY, double from,
      double to) {
    var start = Math.max(0, from);
    var end = Math.min(Jump.WIDTH, to);
    if (end <= start) {
      return;
    }
    var left = area.x + (int) Math.round(start * scaleX);
    var right = area.x + (int) Math.round(end * scaleX);
    graphics.fillRect(left, groundY, right - left, area.y + area.height - groundY);
  }
}