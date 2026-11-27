package multitask2.model.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask2.controller.Input;
import multitask2.model.Game;

public final class Jump implements Game {
  
  public static final double WIDTH = 100;
  public static final double PLAYER_X = 20;
  public static final double PLAYER_SIZE = 4;    
  public static final double FALL_DEPTH = 8;
  public static final double GROUND_Y = 0.0;
  public static final double BASE_PLAYER_SPEED = 50.0;
  public static final double MAX_PLAYER_SPEED = 100.0;
  private static final double FIRST_HOLE_DISTANCE = 20;

  
  public static final double GRAVITY = -180;
  public static final double JUMP_SPEED = 70;
  public static final double JUMP_HEIGHT = JUMP_SPEED * JUMP_SPEED / (2 * -GRAVITY);  
  
  public record Hole(double x, double width) {

    private Hole moveLeft(double dx) {
      return new Hole(x - dx, width);
    }

    private boolean contains(double px) {
      return px >= x && px <= x + width;
    }
  }
  
  private double y;
  private double vy;
  private final List<Hole> holes;
  private double distanceToNextHole;   
  private final Random random;
  
  
  public Jump() {
    y = 0.0;
    vy = 0.0;
    distanceToNextHole = FIRST_HOLE_DISTANCE;
    holes = new ArrayList<>();
    random = new Random();
    super();
  }
  
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);
    var speed = speed(difficulty);
    readInput(input);
    scroll(dt, speed, difficulty);
    applyPhysics(dt);
    return isAlive();
   
  }
  
  private void readInput(Input input) {
    if (input.isKeyJustPressed(Key.SHIFT) && onGround()) {
      vy = JUMP_SPEED;
    }
  }
  
  private double speed(double difficulty) {
    return Math.min(BASE_PLAYER_SPEED + (difficulty) * 10, MAX_PLAYER_SPEED);
  }
  
  private static double jumpDistance(double speed) {
    return speed * JUMP_SPEED * 2 / -GRAVITY;
  }
  
  private void scroll(double dt, double speed, double difficulty) {
    var dx = speed * dt;
    holes.replaceAll(h -> h.moveLeft(dx));
    holes.removeIf(h -> h.x + h.width < 0);
    distanceToNextHole -= dx;
    while (distanceToNextHole <= 0) {
      spawnHole(speed, difficulty);
    }
  }

  private void spawnHole(double speed, double difficulty) {
    var width = randomHoleWidth(speed, difficulty);
    var gap = randomGap(speed);
    holes.add(new Hole(WIDTH + distanceToNextHole, width));
    distanceToNextHole += width + gap;
  }
  
  private static double maxHoleWidth(double speed, double difficulty) {
    var ratio = Math.min(0.65, 0.4 + 0.025 * difficulty);
    return jumpDistance(speed) * ratio;
  }
  
  private double randomHoleWidth(double speed, double difficulty) {
    var max = maxHoleWidth(speed, difficulty);
    var min = Math.max(PLAYER_SIZE * 0.8, max * 0.5);
    return min + random.nextDouble() * Math.max(0, max - min);
  }
  
  private double randomGap(double speed) {
    var distance = jumpDistance(speed);
    return distance * (0.6 + random.nextDouble() * 0.9);
  }

  private void applyPhysics(double dt) {
    var previousY = y;
    vy += GRAVITY * dt;
    y += vy * dt;
    if (previousY >= 0 && y <= 0 && vy <= 0 && hasGroundUnder()) {
      y = 0;
      vy = 0;
    }
  }
  
  private boolean hasGroundUnder() {
    return holes.stream().noneMatch(h -> h.contains(PLAYER_X));
  }
  
  private boolean isAlive() {
    return y > -FALL_DEPTH;
  }
  
  private boolean onGround() {
    return y == 0 && vy == 0;
  }
  
  public double playerY() {
    return y;
  }
  
  public List<Hole> holes(){
    return Collections.unmodifiableList(holes);
  }
  
  

}
