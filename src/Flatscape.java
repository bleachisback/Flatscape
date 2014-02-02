import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

public class Flatscape implements KeyListener{ 
	
	private static ArrayList<Point> bp = new ArrayList<Point>();// position (bullets). Needs to be added to projectile class
	private static ArrayList<Point> bv = new ArrayList<Point>();// velocity (bullets). Needs to be added to projectile class
	private static ArrayList<Point> bulletRemoval = new ArrayList<Point>();
	private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private static ArrayList<Enemy> enemyAddition = new ArrayList<Enemy>();
	private static ArrayList<Enemy> enemyRemoval = new ArrayList<Enemy>();
	private static double rx  = 48, ry = 86;// position (character). Needs to be added to player class.
	private static double vx = 1.5, vy = 02.3;// velocity (character). Needs to be added to player class.
	
	public static final double BULLET_SPEED = 1.75;//The distance the bullet travels, per frame
	public static final int BULLET_DELAY = 15;//The number of frames in between shots. Needs to be changed to ms.
	public static final double INFINITY = 150;//"Infinity" for use of line hit-detection.
	public static final int METEOR_DELAY = 86; //The number of frames in between meteor spawns. Needs to be changed to ms and given a range.
	public static final double SCALE = 100;//The maximum and minimum screen boundaries.
	public static final double SHIP_SPEED = 1.5;//The number of units the ship moves per frame.
	
	public static boolean gameOver = false;
	public static boolean stop = false;
	
	private HashMap<int[], Integer> keySequenceProgress = new HashMap<int[], Integer>();
	private HashMap<int[], Runnable> keySequenceRunnable = new HashMap<int[], Runnable>();
	
	public static void main(String[] args) {
		JFrame frame = null;
		try {
			Field frameField = StdDraw.class.getDeclaredField("frame");
			frameField.setAccessible(true);
			frame = (JFrame) frameField.get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		Flatscape flatscape = new Flatscape();
		frame.addKeyListener(flatscape);
		// set the scale of the coordinate system
		StdDraw.setXscale(-SCALE, SCALE);//Sets the maximum X coordinates to the scale, with 0 being the center
		StdDraw.setYscale(-SCALE, SCALE);//Same but with Y coordinates
		
		startMenu(flatscape);
	}
	
	private static void addBullet() {
		FMath.playSound("Laser_Shoot0");
		bv.add(FMath.smallerHypot(StdDraw.mouseX() - rx, StdDraw.mouseY() - ry, BULLET_SPEED)); //velocity is equal to BULLET_SPEED in the direction of the mouse pointer in relation to the character
		bp.add(new Point(rx, ry)); //initial position of bullet is equal to position of character
	}
	
	public static void addEnemy(Enemy enemy) {
		enemyAddition.add(enemy);
	}
	
	public void addKeySequence(int[] keys, Runnable runnable) {
		keySequenceProgress.put(keys, 0);
		keySequenceRunnable.put(keys, runnable);
	}
	
	//Every frame, draw every bullet and advance their position
	private static void drawBullets() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		
		Point[] posArray = bp.toArray(new Point[0]);
		Point[] velArray = bv.toArray(new Point[0]);
		bp.clear();
		bv.clear();
		for(int i = 0;i < posArray.length; i++) {
			Point bPos = posArray[i];
			Point bVelocity = velArray[i];
			if (Math.abs(bPos.x) >= 107.5 || Math.abs(bPos.y) >= 107.5) { 
				continue;
			}				
			bPos.x = bPos.x + bVelocity.x;
			bPos.y = bPos.y + bVelocity.y;
			StdDraw.filledCircle(bPos.x, bPos.y, .9);
			bp.add(bPos);
			bv.add(bVelocity);
		}
	}
	
	private static void drawCursor() {
		double TriAngle = Math.toDegrees(Math.atan((StdDraw.mouseY() - ry)/(StdDraw.mouseX() - rx))) - 90;
		if (StdDraw.mouseX() < rx) {
			TriAngle = TriAngle * -1;
			if(TriAngle > 90) {
				TriAngle = 90 - (TriAngle - 90);
			} else {
				TriAngle = 180 - TriAngle;
			}
		}
		
		rx = rx + vx;
		ry = ry + vy;
		rx = rx > SCALE ? SCALE : rx < -SCALE ? -SCALE : rx;
		ry = ry > SCALE ? SCALE : ry < -SCALE ? -SCALE : ry;

		// Target drawn
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.circle(StdDraw.mouseX(), StdDraw.mouseY(), 5);

		// draw character on the screen
		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.picture(rx, ry, "Triangle.png", 10, 10, TriAngle);
		System.out.println("" + TriAngle);
	}
	
	public static void hitDetect() {
		loop: for(Enemy enemy : enemies) {
			if(enemy.detectHit(new Point(rx, ry))) {
				gameOver = true;
				FMath.playSound("Game_Over");
				return;
			}
			for(Point point : bp) {
				if(enemy.detectHit(point)) {
					removeBullet(point);
					enemy.onHit();
					continue loop;
				}
			}
			for(Enemy _enemy : enemies) {
				if(enemy != _enemy && enemy instanceof Meteor && _enemy instanceof MeteorHitable) {
					if(enemy.detectHit(_enemy.position)) {
						_enemy.onMeteorHit((Meteor) enemy);
					}
				}
			}
		}
	}
	
	private static void keyboard() {
		vx = vy = 0;
		if(StdDraw.isKeyPressed(KeyEvent.VK_W)) {
			vy += SHIP_SPEED;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			vx -= SHIP_SPEED;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_S)) {
			vy -= SHIP_SPEED;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			vx += SHIP_SPEED;
		}
	}
	
	public static void removeBullet(Point point) {
		bulletRemoval.add(point);
	}
	
	public static void removeEnemy(Enemy enemy) {
		enemyRemoval.add(enemy);
	}
	
	private static void removeEnemies() {
		for(Point point : bulletRemoval) {
			if(!bp.contains(point)) continue;
			bv.remove(bp.indexOf(point));
			bp.remove(point);
		}
		for(Enemy enemy : enemyAddition) {
			enemies.add(enemy);
		}
		for(Enemy enemy : enemyRemoval) {
			enemies.remove(enemy);
		}
		bulletRemoval.clear();
		enemyAddition.clear();
		enemyRemoval.clear();		
	}
		
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			stop = !stop;
		}
		for(int[] keys : keySequenceProgress.keySet()) {
			int progress = keySequenceProgress.get(keys);
			if(keys[progress] == e.getKeyCode()) {
				if(progress == keys.length - 1) {
					keySequenceRunnable.get(keys).run();
					keySequenceRunnable.remove(keys);
					keySequenceProgress.remove(keys);
					continue;
				}
				keySequenceProgress.put(keys, progress + 1);
			} else {
				keySequenceProgress.put(keys, 0);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public static void startGame() {
		int currentBulletDelay = 0;
		int currentMeteorDelay = METEOR_DELAY;
		
		//long time = System.currentTimeMillis();
		//int frames = 0;

		// main animation loop		
		while (!gameOver)  {			
			StdDraw.picture(0,0, "Background.png", 500, 500);
			keyboard();
			if(stop) continue;
			drawCursor();
			removeEnemies();			

			if(currentBulletDelay <= 0) {
				currentBulletDelay = 0;
				if(StdDraw.mousePressed()) {
					currentBulletDelay = BULLET_DELAY;
					addBullet();					
				}
			}			
			/*if(currentMeteorDelay <= 0) {
				currentMeteorDelay = METEOR_DELAY;
				enemies.add(new Meteor());
			}*/

			drawBullets();			
			for(Enemy enemy : enemies) {
				if(enemy == null) {
					removeEnemy(enemy);
					continue;
				}
				enemy.draw();
				enemy.move();
			}			
			
			currentBulletDelay--;
			currentMeteorDelay--;
			
			hitDetect();
			StdDraw.show(0);						
			
			/*if(System.currentTimeMillis() >= time + 1000) {
				System.out.println(frames);
				time = System.currentTimeMillis();
				frames = 0;
			} else {
				frames++;
			}*/
		} 
	}
	
	public static void startMenu(Flatscape flatscape) {
		int[] keys = {KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN,  KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A, KeyEvent.VK_ENTER};
		flatscape.addKeySequence(keys, new Runnable() {
			public void run() {
				FMath.SOUND_PATH = "/sounds/secret/";
			}
		});
		/*boolean start = false;
		while(!start) {
			
		}*/
		startGame();
	}
} 