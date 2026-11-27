package multitask2.model.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask2.controller.Input;
import multitask2.model.Game;

public final class Touch implements Game{
  
  public static final double BASE_SPAWN_DELAY = 2.5;
  public static final double MIN_SPAWN_DELAY = 1.0;
  public static final double BASE_COOLDOWN = 10.0;
  public static final double MIN_COOLDOWN = 5.0;
  public static final double GRID_BOUND = 100.0;
  
  public static final double PLAYER_RADIUS = 2.5;
  public static final double BLOCK_RADIUS = 5.0;
  public static final double BASE_PLAYER_SPEED = 60.0;
  public static final double MAX_PLAYER_SPEED = 110.0;
  private static final double SPEED_GAIN_PER_SECOND = 1.5; 

  private double elapsedTime = 0.0;
  private boolean initialSpawnDone = false;

  
  private double playerX;
  private double playerY;
  private double spawnTimer;
  private final List<Block> blocks;
  private Random random;
  
  
  public Touch() {
    playerX = 0;
    playerY = 0;
    spawnTimer = 0.0;
    blocks = new ArrayList<>();
    random = new Random();
  }

  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);
    
    movePlayer(dt, input, difficulty);
    clampPlayer();
    removeTouched();
    tickBlock(dt);
    spawnIfNeeded(dt, difficulty);
    return hasExpiredBlock();
  }
  


  private double currentPlayerSpeed(double difficulty) {
    double timeBonus = elapsedTime * SPEED_GAIN_PER_SECOND;
    double diffBonus = Math.max(0.0, difficulty - 1.0) * 5.0; // optionnel si difficulty est déjà utilisée
    return Math.min(MAX_PLAYER_SPEED, BASE_PLAYER_SPEED + timeBonus + diffBonus);
  }

  private void movePlayer(double dt, Input input, double difficulty) {
    double dx = 0.0;
    double dy = 0.0;

    if (input.isKeyDown(Key.Q)) dx -= 1.0;
    if (input.isKeyDown(Key.D)) dx += 1.0;
    if (input.isKeyDown(Key.Z)) dy += 1.0;
    if (input.isKeyDown(Key.S)) dy -= 1.0;

    if (dx != 0.0 && dy != 0.0) {
      double invSqrt2 = 1.0 / Math.sqrt(2.0);
      dx *= invSqrt2;
      dy *= invSqrt2;
    }

    double speed = currentPlayerSpeed(difficulty);
    playerX += dx * speed * dt;
    playerY += dy * speed * dt;
  }
  
  private void clampPlayer() {
    playerX = Game.dontExceed(playerX, GRID_BOUND);
    playerY = Game.dontExceed(playerY, GRID_BOUND);
  }
  
  private double cooldown(double difficulty) {
    double scaled = Math.max(0.0, difficulty - 1.0) * 0.25;
    return Math.max(MIN_COOLDOWN, BASE_COOLDOWN / (1.0 + scaled));
  }
  
  private boolean hasExpiredBlock() {
    for(var block : blocks) {
      if(block.expiring()) {
        return true;
      }
    }
    return false;
  }
  
  private void spawnIfNeeded(double dt, double difficulty) {
    elapsedTime += dt;

    if (!initialSpawnDone && elapsedTime >= 1.0) {
      initialSpawnDone = true;
      spawnTimer = 0.0;
      createBlock(difficulty);
      return;
    }

    if (!initialSpawnDone) {
      return;
    }

    spawnTimer += dt;
    double delay = spawnDelayFor(difficulty);
    if (spawnTimer >= delay) {
      spawnTimer -= delay;
      createBlock(difficulty);
    }
  }
  
  private void createBlock(double difficulty) {
    var x = random.nextDouble() * 2 * GRID_BOUND - GRID_BOUND;
    var y = random.nextDouble() * 2 * GRID_BOUND - GRID_BOUND;
    var cooldown = cooldown(difficulty);
    x = Game.dontExceed(x, GRID_BOUND);
    y = Game.dontExceed(y, GRID_BOUND);
   
    
    var block = new Block(x, y, cooldown);
    blocks.add(block);
    
  }
  
  private void removeTouched() {
    double hitRadius = PLAYER_RADIUS;
    blocks.removeIf(block -> block.touchedBy(playerX, playerY, hitRadius));
  }
  
  private double spawnDelayFor(double difficulty) {
    double scaled = Math.max(0.0, difficulty - 1.0) * 0.25;
    return Math.max(MIN_SPAWN_DELAY, BASE_SPAWN_DELAY / (1.0 + scaled));
  }
  
  private void tickBlock(double dt) {
    for(var block : blocks) {
      block.tick(dt);
      }
    }
  
  public double playerX() {
    return playerX;
  }
  
  public double playerY() {
    return playerY;
  }
  
  public List<Block> blocks(){
    return Collections.unmodifiableList(blocks);
  }
}