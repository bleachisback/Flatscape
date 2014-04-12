import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;

public class Flatscape implements KeyListener, MouseListener { 
	
	public static ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public static HashMap<Enemy, Boolean> enemyAddition = new HashMap<Enemy, Boolean>();
	
	public static ArrayList<Physicsable> physics = new ArrayList<Physicsable>();//Array of objects that need to have physics applied to them every frame
	public static HashMap<Physicsable, Boolean> physicsAddition = new HashMap<Physicsable, Boolean>();

	public static Player player = null;
	
	public static final double BULLET_SPEED = 1.75;//The distance the bullet travels, per frame
	public static final double INFINITY = 200;//"Infinity" for use of line hit-detection.
	public static final int ENEMY_DELAY = 1800; //The number of milliseconds in between meteor spawns. Needs to be given a range.
	public static final double SCALE = 100;//The maximum and minimum screen boundaries.
	public static final double SHIP_SPEED = .015;//The number of units the ship moves per frame.
	
	private static Font storyFont;
	private static Font titleFont;
	private static Font subTitleFont;
	
	public static GameState gameState;
	
	public static int level = 0;
	
	public static boolean pause = false;
	
	public static String[] currentText;
	public static boolean nextText = false;
	public static Scanner scanner;
	
	private static Point bgOffset = new Point(0, 0);
	
	private static Flatscape keyboardListener;
	
	private HashMap<int[], Integer> keySequenceProgress = new HashMap<int[], Integer>();
	private HashMap<int[], Runnable> keySequenceRunnable = new HashMap<int[], Runnable>();
	
	public static int currentEnemyDelay = 0;
	
	public static long time = System.currentTimeMillis();
	public static double passed = 0;
	public static double scale = 1;
	
	public static Physicsable cameraTarget = null;
	
	public static final int TELEPORT_DELAY = 60000;
	public static double currentTeleport;
	
	public static void main(String[] args) {
		JFrame frame = null;
		try {
			Field frameField = StdDraw.class.getDeclaredField("frame");
			frameField.setAccessible(true);
			frame = (JFrame) frameField.get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		keyboardListener = new Flatscape();
		frame.addKeyListener(keyboardListener);
		frame.getContentPane().addMouseListener(keyboardListener);
		// set the scale of the coordinate system
		StdDraw.setXscale(-SCALE, SCALE);//Sets the maximum X coordinates to the scale, with 0 being the center
		StdDraw.setYscale(-SCALE, SCALE);//Same but with Y coordinates
		
		try {
			storyFont = Font.createFont(Font.TRUETYPE_FONT, Flatscape.class.getResourceAsStream("Digital_tech.ttf")).deriveFont(17.0f);
			titleFont = storyFont.deriveFont(72.0f);
			subTitleFont = storyFont.deriveFont(32.0f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		gameState = GameState.START_MENU;
		while(true) gameState();
	}
	
	public void addKeySequence(int[] keys, Runnable runnable) {
		keySequenceProgress.put(keys, 0);
		keySequenceRunnable.put(keys, runnable);
	}
	
	public static void draw() {
		for(Drawable draw : drawables) {
			draw.draw();
		}
		int teleportStage = (int) Math.floor(currentTeleport / TELEPORT_DELAY * 15);
		//System.out.println(currentTeleport + " " + TELEPORT_DELAY + " " + (currentTeleport / TELEPORT_DELAY) + " "+ teleportStage);
		if(gameState != GameState.START_MENU) StdDraw.picture(0, 95, "UI/" + teleportStage + ".png");
		StdDraw.show(0);
		updateBackground();
	}
	
	private static void gameState() {
		switch(gameState) {
			case START_MENU:
				startMenu();
				break;
			case GAME:
				playGame();
				break;
			case PAUSE:
				break;
			case END:
				break;
			case TEXT:
				writeText();
				break;
		}
	}
	
	public static void handleEnemies() {
		removeEnemies();
		
		if(currentEnemyDelay <= 0) {
			currentEnemyDelay = ENEMY_DELAY + currentEnemyDelay;
			if(Math.random() > .25 || (level == 0 && gameState != GameState.START_MENU)) {
				enemyAddition.put(new Meteor(), true);
			} else {
				enemyAddition.put(new EnemyShip(), true);
			}
		}
		if(currentEnemyDelay > 0) currentEnemyDelay -= passed;
		
		for(Enemy enemy : enemies) {
			if(enemy == null) {
				enemyAddition.put(enemy, false);
				continue;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void hitDetect() {
		for(Physicsable phys : (ArrayList<Physicsable>) physics.clone()) {
			for(Physicsable _phys : (ArrayList<Physicsable>) physics.clone()) {
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
		
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(Flatscape.currentTeleport >= Flatscape.TELEPORT_DELAY) {
				Flatscape.nextLevel();
			}
			//pause = !pause;
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
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }
	

	@Override
	public void mouseClicked(MouseEvent arg0) {
		switch(Flatscape.gameState) {
			case START_MENU:
				startGame();
				break;
			case TEXT:
				Flatscape.nextText = true;
				break;
			default:
				break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent arg0) { }

	@Override
	public void mouseReleased(MouseEvent arg0) { }
	
	public static void nextLevel() {
		enemies.clear();
		physics.clear();
		drawables.clear();
		
		level ++;
		
		player.nextLevel();		
		
		currentEnemyDelay = ENEMY_DELAY;
		
		time = System.currentTimeMillis();
		passed = 0;
		scale = 1;
		
		startText(level);
	}
	
	@SuppressWarnings("unchecked")
	public static void physics() {
		for(Physicsable phys : (ArrayList<Physicsable>) physics.clone()) {
			phys.physics(scale);
		}
	}
	
	public static void playGame() {
		keyboard();
		if(pause) {
			time = System.currentTimeMillis();
			return;	
		}		
		
		time();
		handleEnemies();
		physics();
		hitDetect();
		draw();
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
	
	public static void startGame() {
		enemies.clear();
		physics.clear();
		drawables.clear();
		
		player = new Player(new Point(0, 0));
		drawables.add(player);
		physics.add(player);
		cameraTarget = player;
		
		currentEnemyDelay = ENEMY_DELAY;
		
		time = System.currentTimeMillis();
		passed = 0;
		scale = 1;
		
		startText(0);
	}
	
	@SuppressWarnings("unchecked")
	public static void startMenu() {
		FMath.playMusic("Title");
		time();
		handleEnemies();
		physics();
		hitDetect();
		draw();
		
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.setFont(titleFont);
		StdDraw.text(0, 60, "Space Penguins");
		StdDraw.setFont(subTitleFont);
		StdDraw.text(0, 45, "of Utter Deep Nine");		
		StdDraw.setFont(storyFont);
		StdDraw.text(0, -90, "Click to continue");
		
		if(cameraTarget == null || !enemies.contains(cameraTarget) || cameraTarget instanceof Meteor || cameraTarget instanceof Projectile) {
			loop: for(int i = 0; i < 3; i++) {
				for(Physicsable phys : (ArrayList<Physicsable>) physics.clone()) {
					if(i < 1 && phys instanceof Meteor) continue;
					if(i < 2 && phys instanceof Projectile) continue;
					cameraTarget = phys;
					break loop;
				}
			}
		}
	}
	
	public static void startText(int level) {
		scanner = new Scanner(Flatscape.class.getResourceAsStream("Scripts/Level" + level + ".txt"));
		currentText = scanner.next().replace("_", " ").split("]");
		gameState = GameState.TEXT;
		if(level > 0) FMath.playMusic("Level" + level);
		else FMath.playMusic("");
		Flatscape.level = level;
		currentTeleport = 0;
	}
	
	public static void time() {
		passed = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		scale = passed / 10;
		if(gameState != GameState.GAME) return;
		currentTeleport += passed;
		if(currentTeleport > TELEPORT_DELAY) currentTeleport = TELEPORT_DELAY;
	}
	
	@SuppressWarnings("unchecked")
	public static void updateBackground() {
		double x = cameraTarget == null ? 0 : cameraTarget.position.x;
		double y = cameraTarget == null ? 0 : cameraTarget.position.y;
		
		double limit = .5;
		
		if(x > SCALE * limit) {			
			for(Physicsable _physics : (ArrayList<Physicsable>) physics.clone()) {
				if(_physics instanceof Projectile) continue;
				_physics.position.x -= x - SCALE * limit;
			}
			bgOffset.x -= x - SCALE * limit;
		} else if(x < -(SCALE * limit)) {			
			for(Physicsable _physics : (ArrayList<Physicsable>) physics.clone()) {
				if(_physics instanceof Projectile) continue;
				_physics.position.x -= x + SCALE * limit;
			}
			bgOffset.x -= x + SCALE * limit;
		}
		
		if(y > SCALE * limit) {
			for(Physicsable _physics : (ArrayList<Physicsable>) physics.clone()) {
				if(_physics instanceof Projectile) continue;
				_physics.position.y -= y - SCALE * limit;
			}
			bgOffset.y -= y - SCALE * limit;
		} else if(y < -(SCALE * limit)) {			
			for(Physicsable _physics : (ArrayList<Physicsable>) physics.clone()) {
				if(_physics instanceof Projectile) continue;
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
	
	private static void writeText() {		
		time();		
		physics();
		draw();
		if(nextText) {
			if(scanner.hasNext()) currentText = scanner.next().replace("_", " ").split("]");
			else {
				gameState = GameState.GAME;
				scanner.close();
			}
			nextText = false;
		}
		StdDraw.setFont(storyFont);
		StdDraw.setPenColor(StdDraw.WHITE);
		for(int i = 0; i < currentText.length; i++) {
			StdDraw.textLeft(-90, -80 - i * 10, currentText[i]);
		}		
	}
} 