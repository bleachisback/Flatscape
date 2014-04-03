import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

public class Flatscape implements KeyListener { 
	
	public static ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public static HashMap<Enemy, Boolean> enemyAddition = new HashMap<Enemy, Boolean>();
	
	public static ArrayList<Physicsable> physics = new ArrayList<Physicsable>();//Array of objects that need to have physics applied to them every frame
	public static HashMap<Physicsable, Boolean> physicsAddition = new HashMap<Physicsable, Boolean>();

	public static Player player = null;
	
	public static final double BULLET_SPEED = 1.75;//The distance the bullet travels, per frame
	public static final double INFINITY = 150;//"Infinity" for use of line hit-detection.
	public static final int ENEMY_DELAY = 1800; //The number of milliseconds in between meteor spawns. Needs to be given a range.
	public static final double SCALE = 100;//The maximum and minimum screen boundaries.
	public static final double SHIP_SPEED = .015;//The number of units the ship moves per frame.
	
	public static boolean gameOver = false;
	public static boolean stop = false;
	
	private static Point bgOffset = new Point(0, 0);
	
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
	
	public void addKeySequence(int[] keys, Runnable runnable) {
		keySequenceProgress.put(keys, 0);
		keySequenceRunnable.put(keys, runnable);
	}
	
	public static void hitDetect() {
		for(Physicsable phys : physics) {
			for(Physicsable _phys : physics) {
				if(phys == _phys) continue;
				if(phys.detectHit(_phys)) {
					phys.onHit(_phys);
					_phys.onHit(phys);
				}
			}
		}
	}
	
	private static void keyboard() {
		player.acceleration.x = player.acceleration.y = 0;
		Point point = FMath.smallerHypot(StdDraw.mouseX() - player.position.x, StdDraw.mouseY() - player.position.y, SHIP_SPEED);
		double angle = 0;
		if(StdDraw.isKeyPressed(KeyEvent.VK_W)) {
			player.acceleration.add(point);
		} if(StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			angle = player.rotation - 90;
			if(angle < 0) angle += 360;
			player.acceleration.add(FMath.circlePoint(SHIP_SPEED / 2.5, angle));
		} if(StdDraw.isKeyPressed(KeyEvent.VK_S)) {
			player.acceleration.subtract(point);
		} if(StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			angle = player.rotation - 270;
			if(angle < 0) angle += 360;
			player.acceleration.add(FMath.circlePoint(SHIP_SPEED / 2.5, angle));
		}
	}
	
	private static void removeEnemies() {
		for(Enemy enemy : enemyAddition.keySet()) {
			if(enemyAddition.get(enemy)) {
				enemies.add(enemy);
				drawables.add(enemy);
				physics.add(enemy);
			} else {
				enemies.remove(enemy);
				drawables.remove(enemy);
				physics.remove(enemy);
			}
		}		
		for(Physicsable physic : physicsAddition.keySet()) {
			if(physicsAddition.get(physic)) {
				physics.add(physic);
			} else {
				physics.remove(physic);
			}
		}
		enemyAddition.clear();
	}
		
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			System.out.println("STAHP");
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
		player = new Player(new Point(0, 0));
		drawables.add(player);
		physics.add(player);
		
		int currentEnemyDelay = ENEMY_DELAY;
		
		long time = System.currentTimeMillis();
		double passed = 0;
		double scale = 1;
		
		try {
			StdDraw.setFont(Font.createFont(Font.TRUETYPE_FONT, Flatscape.class.getResourceAsStream("Digital_tech.ttf")).deriveFont(50.0f));			
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		// main animation loop
		while (!gameOver)  {			
			keyboard();
			if(stop) {
				time = System.currentTimeMillis();
				continue;	
			}
			updateBackground();
			removeEnemies();
			
			StdDraw.textLeft(0, 0, "blah blah");

			passed = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			scale = passed / 10;
			
			if(currentEnemyDelay <= 0) {
				currentEnemyDelay = ENEMY_DELAY + currentEnemyDelay;
				if(Math.random() > .25) {
					enemyAddition.put(new Meteor(), true);
				} else {
					enemyAddition.put(new EnemyShip(), true);
				}
			}
			
			for(Enemy enemy : enemies) {
				if(enemy == null) {
					enemyAddition.put(enemy, false);
					continue;
				}
			}
			
			for(Physicsable phys : physics) {
				phys.physics(scale);
			}			
			for(Drawable draw : drawables) {
				draw.draw();
			}
			
			if(currentEnemyDelay > 0) currentEnemyDelay -= passed;
			
			hitDetect();			
			StdDraw.show(0);
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
	
	public static void updateBackground() {
		double x = player.position.x;
		double y = player.position.y;
		
		double limit = .5;
		
		if(x > SCALE * limit) {			
			for(Physicsable _physics : physics) {
				_physics.position.x -= x - SCALE * limit;
			}
			bgOffset.x -= x - SCALE * limit;
		} else if(player.position.x < -(SCALE * limit)) {			
			for(Physicsable _physics : physics) {
				_physics.position.x -= x + SCALE * limit;
			}
			bgOffset.x -= x + SCALE * limit;
		}
		
		if(y > SCALE * limit) {
			for(Physicsable _physics : physics) {
				_physics.position.y -= y - SCALE * limit;
			}
			bgOffset.y -= y - SCALE * limit;
		} else if(player.position.y < -(SCALE * limit)) {			
			for(Physicsable _physics : physics) {
				_physics.position.y -= y + SCALE * limit;
			}
			bgOffset.y -= y + SCALE * limit;
		}
		
		double reset = 500;
		if(bgOffset.x >= reset) bgOffset.x -= reset;
		if(bgOffset.x <= -reset) bgOffset.x += reset;
		if(bgOffset.y >= reset) bgOffset.y -= reset;
		if(bgOffset.y <= -reset) bgOffset.y += reset;
		
		StdDraw.picture(bgOffset.x, bgOffset.y, "Background.png", 500, 500);
		
		double _x = 0;
		double _y = 0;
		
		if(bgOffset.x >= 150) _x = -500 + bgOffset.x;
		if(bgOffset.x <= -150) _x = 500 + bgOffset.x;
		if(bgOffset.y >= 150) _y = -500 + bgOffset.y;
		if(bgOffset.y <= -150) _y = 500 + bgOffset.y;
				
		if(_x != 0) StdDraw.picture(_x, bgOffset.y, "Background.png", 500, 500);
		if(_y != 0) StdDraw.picture(bgOffset.x, _y, "Background.png", 500, 500);
		if(_x != 0 && _y != 0) StdDraw.picture(_x, _y, "Background.png", 500, 500);
	}
} 