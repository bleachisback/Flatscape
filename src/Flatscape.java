import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	public static final int BULLET_DELAY = 300;//The number of milliseconds in between shots.
	public static final double INFINITY = 150;//"Infinity" for use of line hit-detection.
	public static final int METEOR_DELAY = 860; //The number of milliseconds in between meteor spawns. Needs to be given a range.
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
	
	/*@SuppressWarnings("unchecked")
	public static void hitDetect() {
		loop: for(Enemy enemy : enemies) {
			if(enemy.detectHit(player.position)) {
				gameOver = true;
				FMath.playSound("Game_Over");
				return;
			}
			for(ProjectileBullet bullet : (ArrayList<ProjectileBullet>) bullets.clone()) {
				if(enemy.detectHit(bullet.position)) {
					drawables.remove(bullet);
					physicsAddition.put(bullet, false);
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
	}*/
	
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
		
		int currentBulletDelay = 0;
		int currentMeteorDelay = METEOR_DELAY;
		
		long time = System.currentTimeMillis();
		double passed = 0;
		double scale = 1;
		//int frames = 0;

		EnemyShip blah = new EnemyShip(new WeaponBullet(), new Point(50, 50));
		enemyAddition.put(blah, true);
		
		// main animation loop
		while (!gameOver)  {			
			updateBackground();
			keyboard();
			if(stop) continue;			
			removeEnemies();			

			passed = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			scale = passed / 10;
					
			if(currentMeteorDelay <= 0) {
				currentMeteorDelay = METEOR_DELAY + currentMeteorDelay;
				enemyAddition.put(new Meteor(), true);
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
			
			if(currentBulletDelay > 0) currentBulletDelay -= passed;
			if(currentMeteorDelay > 0) currentMeteorDelay -= passed;
			
			//hitDetect();			
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
	
	public static void updateBackground() {
		double x = player.position.x;
		double y = player.position.y;
		
		if(x > SCALE * .9) {			
			for(Physicsable _physics : physics) {
				_physics.position.x -= x - SCALE * .9;
			}
			bgOffset.x -= x - SCALE * .9;
		} else if(player.position.x < -(SCALE * .9)) {			
			for(Physicsable _physics : physics) {
				_physics.position.x -= x + SCALE * .9;
			}
			bgOffset.x -= x + SCALE * .9;
		}
		
		if(y > SCALE * .9) {			
			for(Physicsable _physics : physics) {
				_physics.position.y -= y - SCALE * .9;
			}
			bgOffset.y -= y - SCALE * .9;
		} else if(player.position.y < -(SCALE * .9)) {			
			for(Physicsable _physics : physics) {
				_physics.position.y -= y + SCALE * .9;
			}
			bgOffset.y -= y + SCALE * .9;
		}
		System.out.println(bgOffset);
		
		if(bgOffset.x >= 475) bgOffset.x -= 475;
		if(bgOffset.x <= -475) bgOffset.x += 475;
		if(bgOffset.y >= 475) bgOffset.y -= 475;
		if(bgOffset.y <= -475) bgOffset.y += 475;
		
		StdDraw.picture(bgOffset.x, bgOffset.y, "Background.png", 500, 500);
		
		double _x = 0;
		double _y = 0;
				
		if(bgOffset.x >= 140) _x = -345 - 130 - bgOffset.x;
		if(bgOffset.x <= -140) _x = 345 + 130 + bgOffset.x;
		if(bgOffset.y >= 140) _y = -345 - 130 - bgOffset.y;
		if(bgOffset.y <= -140) _y = 345 + 130 + bgOffset.y;	
		
		if(_x != 0) StdDraw.picture(_x, bgOffset.y, "Background.png", 500, 500);
		if(_y != 0) StdDraw.picture(bgOffset.x, _y, "Background.png", 500, 500);
		if(_x != 0 && _y != 0) StdDraw.picture(_x, _y, "Background.png", 500, 500);
	}
} 