package multitask2.model.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask2.controller.Input;
import multitask2.model.Game;


public final class Fly implements Game {

  public static final double WIDTH = 100;

  public static final double HEIGHT = 100;

  public static final double FLY_CENTER = WIDTH/2;
  
  private static final double BASE_OBSTACLE_SPEED = 15;
  
  private static final double BASE_SPAWN_INTERVAL = 1.8;

  private static final double GAPSIZE_INITIAL = 30;
  
  private static final double GAPSIZE_MIN = 10;
  
  public static final double OBSTACLE_WIDTH = 4;

  private static final double HEIGHT_GAIN_PER_SEC = 100;
  
  private static final double GRAVITY = 40;
  
  public static final int FLY_HALF_SIZE = 3;
  
  
  public record Obstacle(double x, double gapCenter, double gapSize) {    
  	
  	private Obstacle move(double dx) {
  		return new Obstacle(x-dx, gapCenter, gapSize);
  	}
  	
  	private boolean checkFlyInObstacle() {
  		return (x < (FLY_CENTER + FLY_HALF_SIZE)) && ((x + OBSTACLE_WIDTH) > (FLY_CENTER - FLY_HALF_SIZE)); 
  	}
  	
  	private boolean flyPassObstacle(double altitude) {
  		return ((altitude - FLY_HALF_SIZE) >= (gapCenter - gapSize/2)) && ((altitude + FLY_HALF_SIZE) <= (gapCenter + gapSize/2)); 
  	}
  	
  	private boolean obstacleFinish() {
  		return x + OBSTACLE_WIDTH < 0;
  	}
  	
  }

  private double altitude;
  
  private final List<Obstacle> obstacles;

  private double spawnTimer;

  private final Random random;

  public Fly() {
    altitude = HEIGHT / 2;
    obstacles = new ArrayList<>();
    spawnTimer = 1;
    random = new Random();
    super();
  }


  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);

    readInput(dt,input);
    spawnObstacle(dt, difficulty);
    moveObstacle(dt, difficulty);
    return checkCollision();
  }


  private void readInput(double dt, Input input) {
    var flySpeed = (input.isKeyDown(Key.SPACE) ? HEIGHT_GAIN_PER_SEC : -GRAVITY) ;
    altitude = Math.clamp(altitude + flySpeed * dt, FLY_HALF_SIZE, HEIGHT - FLY_HALF_SIZE);
  }


  private void spawnObstacle(double dt, double difficulty) {
    var spawnInterval = Math.max(0.45, BASE_SPAWN_INTERVAL - difficulty * 0.15);
    var gapSize = Math.max(GAPSIZE_MIN, GAPSIZE_INITIAL - difficulty * 0.15);
    spawnTimer -= dt;
    if (spawnTimer < 0) {
    	var gapCenter = (gapSize/2) + random.nextDouble(HEIGHT - gapSize);
      obstacles.add(new Obstacle(WIDTH, gapCenter, gapSize));
      spawnTimer = spawnInterval;
    }
  }


  private void moveObstacle(double dt, double difficulty) {
    var obstacleSpeed = BASE_OBSTACLE_SPEED + difficulty * 10;
    obstacles.replaceAll(o -> o.move(dt * obstacleSpeed));
  }


  private boolean checkCollision() {
  	var collision = obstacles.stream()
  													 .anyMatch(o -> o.checkFlyInObstacle() && !o.flyPassObstacle(altitude));
  	obstacles.removeIf(Obstacle::obstacleFinish);
  	return !collision;
  }

  public double altitude() {
    return altitude;
  }


  public List<Obstacle> obstacles() {
    return Collections.unmodifiableList(obstacles);
  }
}